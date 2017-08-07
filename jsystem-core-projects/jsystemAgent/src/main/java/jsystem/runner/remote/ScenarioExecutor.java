/*
 * Created on 11/06/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.runner.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class ScenarioExecutor implements ExecutionListener, ExtendTestListener {

	/**
	 * signals if current run should stop
	 */
	volatile private boolean stop = false;

	volatile private boolean paused = false;

	volatile boolean runEnd = false;

	volatile private boolean remoteExit = false;

	volatile private boolean running = false;

	private RunMode executeMode = RunMode.DROP_EVERY_RUN;

	private String scenarioFile = null;

	private RemoteExecutor executor;

	private JSystemListeners executionListener;

	/**
	 * is the execution marked as repeat
	 */
	volatile private boolean isRepeat;

	/**
	 * the amount of loops for the current run, 0 is loop forever
	 */
	volatile private int repeatNumber;

	/**
	 * the amount of loops remaining for current execution
	 */
	volatile private int repeatLeftNumber;
	
	/**
	 * signals if a test was started in current execution
	 */
	volatile private boolean testStarted;
	

	private static Logger log = Logger.getLogger(ScenarioExecutor.class.getName());

	public ScenarioExecutor() {
	}

	/**
	 * Executes test/scenario with UUID <code>uuid</code> in the context of the current scenario.<br>
	 * In case that <code>uuid</code> points to a scenario, the scenario is executed in run.mode 1.
	 */
	public void execute(String uuid) throws Exception {
		JTest test = ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(),uuid);
		if (test == null){
			throw new Exception("Test with id " + uuid + " was not found in the system");
		}
		if (test.getParent() == null){
			throw new Exception("Only internal tests/scenarios can be executed with this method");
		}
		executionListener = RunnerListenersManager.getInstance();
		executionListener.addListener(this);
		createEmptyPropsFile();
		executor = null;
				
		if ("false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.SAVE_RUN_PROPERTIES))) {
			RunProperties.getInstance().resetRunProperties();
		}
		
		try {
			Properties commandProps = new Properties();
			Scenario scenario;
			if (test instanceof Scenario){
				scenario = test.getParent().getMyScenario();
			}else {
				scenario = test.getMyScenario();
			}
			commandProps.setProperty(RunningProperties.UUID_PARENT_TAG, scenario.getParentFullUUID());
			commandProps.setProperty(RunningProperties.UUID_TAG, scenario.getUUID());
			commandProps.setProperty(RunningProperties.PARENT_NAME, ScenarioHelpers.buildFullPathName(scenario.getParent()));
			getExecutor().run(scenario.getScenarioFile().getAbsolutePath(),
					new String[] { test.getTestId() }, commandProps);
			waitForRunEnd();
			getExecutor().exit();
			waitForRemoteExit();
		}finally{
			cycleEnded();
			executionEnded();
		}
	}
	/***************************************************************************
	 * 
	 * Methods called by UI
	 * 
	 */
	public void execute() throws Exception {
		executionListener = RunnerListenersManager.getInstance();
		executionListener.addListener(this);

		parseRunMode();
		
		createEmptyPropsFile();

		executor = null;

		scenarioFile = ScenariosManager.getInstance().getCurrentScenario().getScenarioFile().getAbsolutePath();

		log.log(Level.INFO, "Execute count: " + ScenariosManager.getInstance().getCurrentScenario().countTestCases());

		setRepeatLeftNumber(getRepeatNumber());
		
		testStarted = false;

		try {
			while (true) {

				// deleting run.properties file before a new cycle.
				if ("false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.SAVE_RUN_PROPERTIES))) {
					RunProperties.getInstance().resetRunProperties();
				}

				runRound();

				cycleEnded();

				if (stop) {
					return;
				}
				if (getRepeatNumber() == 0 && isRepeat()) {
					continue;
				}
				if (!(getRepeatLeftNumber() > 0) || !isRepeat()) {
					break;
				}
			}
		} finally {
			executionEnded();
			if (!testStarted && !stop){
				ListenerstManager.getInstance().showConfirmDialog("No tests were executed!",
																  "There was an error with the tests execution.\n please look at the JSystem console for more info.",
																  JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
			}

		}
	}

	public synchronized void pause() throws Exception {
		if (!running) {
			return;
		}
		paused = true;
		((RunnerListenersManager)RunnerListenersManager.getInstance()).flushReporters();
		getExecutor().pause();
	}

	public synchronized void gracefulStop(){
		if(!running){
			return;
		}
		stop = true;
		if (!runEnd) {
			if(executor != null){
				executor.gracefulStop();
			}
		}
		runEnd = true;
	}

	public synchronized void stop() throws Exception {
		if (!running) {
			return;
		}
		stop = true;
		if (getExecutor() != null) {
			getExecutor().interruptTest();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// ignored
		}
		ListenerstManager.getInstance().report("The test was interrupted by the user", false);
		ListenerstManager.getInstance().endTest(null);
		remoteExit = true;
		runEnd = true;
		notifyAll();
	}

	public synchronized void resume() throws Exception {
		paused = false;
		getExecutor().resume();
	}

	/**
	 * ********************************************************************************8
	 * 
	 * Methods called by remote executor
	 * 
	 */
	public synchronized void endRun() {
		/*
		 * If the run ended and the pause was pressed set the gui to running
		 */
		if (paused) {
			paused = false;
		}
		runEnd = true;
		notifyAll();
	}

	/**
	 * 
	 */
	public synchronized void remoteExit() {
		remoteExit = true;
		notifyAll();
	}

	public synchronized void executionEnded(String scenarioName) {
		remoteExit = true;
		notifyAll();
		executionListener.executionEnded(scenarioName);
	}

	/**
	 * update repeat counter and signal run end (cycle end) to execution listener
	 */
	private void cycleEnded() {
		executionListener.endRun();
		repeatLeftNumber--;
	}

	/**
	 * reset repeat counter, signal to execution listener that execution ended
	 */
	private void executionEnded() {
		setRepeat(false);
		setRepeatNumber(0);
		executionListener.executionEnded(ScenariosManager.getInstance().getCurrentScenario().getName());

	}

	/**
	 * Runs a round of the current scenario. Please read the documentation of
	 * RunnerTest.getPropertiesInAntCanonicalFormat to understand how test
	 * parameters are passed to the tests and to understand the differences
	 * between the run modes
	 */
	private void runRound() throws Exception {
		stop = false;
		paused = false;
		int[] testsIndexes;

		switch (executeMode) {
		case DROP_EVERY_RUN:
			getExecutor().run(scenarioFile, null, null);
			waitForRunEnd();
			getExecutor().exit();
			waitForRemoteExit();
			break;
		case DROP_EVERY_TEST:
			// TODO: Ant flow control won't work !
			testsIndexes = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
			Scenario currentScenario = ScenariosManager.getInstance().getCurrentScenario();
			for (int i = 0; i < testsIndexes.length; i++) {
				RunnerTest test = currentScenario.getTest(testsIndexes[i]);
				if (test instanceof RunnerFixture) {
					continue;
				}

				Properties commandProps = new Properties();
				Scenario scenario = (Scenario)test.getParent();
				commandProps.setProperty(RunningProperties.UUID_PARENT_TAG, scenario.getParentFullUUID());
				commandProps.setProperty(RunningProperties.UUID_TAG, scenario.getUUID());
				commandProps.setProperty(RunningProperties.PARENT_NAME, ScenarioHelpers.buildFullPathName(scenario.getParent()));
				try {
					getExecutor().run(scenario.getScenarioFile().getAbsolutePath(),
							new String[] { test.getTestId() }, commandProps);
					waitForRunEnd();
					getExecutor().exit();
					waitForRemoteExit();
				} finally {
				}
				if (stop) {
					return;
				}
			}
			break;

		case DROP_EVERY_SCENARIO:
			/*
			 * get all the tests indexes to execute
			 */
			testsIndexes = ScenariosManager.getInstance().getCurrentScenario().getEnabledTestsIndexes();
			int i = 0;
			Scenario curreScenario = null;
			while (i != testsIndexes.length) {
				/*
				 * Get the scenario of the first test in the list
				 */
				RunnerTest startTest = ScenariosManager.getInstance().getCurrentScenario().getTest(testsIndexes[i]);
				curreScenario = ScenarioHelpers.getFirstScenarioAncestor(startTest);

				StringBuffer testsToRun = new StringBuffer();
				Properties props = new Properties();
				for (; i < testsIndexes.length; i++) {
					RunnerTest test = ScenariosManager.getInstance().getCurrentScenario().getTest(testsIndexes[i]);
					/*
					 * As long as the test scenario is the same as the first
					 * test add it to the list of tests to run.
					 */
					if (test.getParent().equals(curreScenario)) {
						if (testsToRun.length() > 0) {
							testsToRun.append(',');
						}
						testsToRun.append(test.getTestId());
						props.putAll(test.getPropertiesInAntCanonicalFormat());
					} else {
						break;
					}
				}
				
				Properties commandProps = new Properties();
				commandProps.setProperty(RunningProperties.UUID_PARENT_TAG, curreScenario.getParentFullUUID());
				commandProps.setProperty(RunningProperties.UUID_TAG, curreScenario.getUUID());
				commandProps.setProperty(RunningProperties.PARENT_NAME, ScenarioHelpers.buildFullPathName(curreScenario.getParent()));

				/*
				 * Execute tests if tests were found
				 */
				if (testsToRun.length() > 0) {

					try {
						/*
						 * execute the sub scenario
						 */
						getExecutor().run(curreScenario.getScenarioFile().getAbsolutePath(),
								testsToRun.toString().split(","), commandProps);
						waitForRunEnd();
						getExecutor().exit();
						waitForRemoteExit();
					} finally {
						
					}

					if (stop) {
						return;
					}
				}
			}

		}
	}

	private synchronized RemoteExecutor getExecutor() {
		if (executor == null) {
			executor = new RemoteExecutorImpl();
			executor.setRunEndListener(this);
		}
		return executor;
	}

	private synchronized void waitForRunEnd() {
		running = true;
		runEnd = false;
		while (!runEnd) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignored
			}
		}
		runEnd = false;
		running = false;
	}

	private synchronized void waitForRemoteExit() {
		while (!remoteExit) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignroed
			}
		}
		remoteExit = false;
		testStarted = testStarted || executor.isTestStarted();
		executor = null;
	}

	/***************************************************************************
	 * Methods for creating tests parameters properties file Please read
	 * runRound doc.
	 */

	@SuppressWarnings("unused")
	private File saveTestsPropertiesToPropertyFile(Properties props) throws Exception {
		File file = generatePropertiesFileName();
		FileOutputStream outStream = new FileOutputStream(file);
		try {
			props.store(outStream, "tests parameters file");
		} finally {
			outStream.close();
		}
		return file;
	}

	@SuppressWarnings("unused")
	private Properties createCommandProps(File file) {
		Properties commandProps = new Properties();
		commandProps.put(RunningProperties.TEST_PARAMETERS_FILE_NAME_PARAMETER, file.getName());
		return commandProps;
	}

	private File generatePropertiesFileName() {
		File f = new File(RunningProperties.TEST_PARAMETERS_FILE_NAME_PREFIX + System.currentTimeMillis()
				+ ".properties");
		int i = 0;
		while (f.exists()) {
			f = new File(RunningProperties.TEST_PARAMETERS_FILE_NAME_PREFIX + System.currentTimeMillis() + i
					+ ".properties");
			i++;
		}
		return f;
	}

	/**
	 * Creates an empty test parameters properties file. please read
	 * Scenario class documentation
	 */
	private void createEmptyPropsFile() {
		File f = new File(RunningProperties.TEST_PARAMETERS_EMPTY_FILE);
		try {
			f.createNewFile();
		} catch (Exception e) {
			log.warning("Failed creating empty properties file for tests parameters" + e.getMessage());
		}
	}

	/**
	 * print the current run mode to the logger
	 */
	private void parseRunMode() {
		String runMode = JSystemProperties.getInstance().getPreference(FrameworkOptions.RUN_MODE);
		if (runMode != null) {
			try {
				executeMode = RunMode.valueOf(runMode.toUpperCase());
			} catch (Exception ex) {
				try{ // backward compatibility and users who are used to old system
					int runModeAsNumber = Integer.parseInt(runMode);
					executeMode = RunMode.enumFromNum(runModeAsNumber);
				}catch (NumberFormatException e) {
					JSystemProperties.getInstance().setPreference(FrameworkOptions.RUN_MODE,RunMode.DROP_EVERY_RUN.toString());
					log.warning("Found incompetible value in Run Mode, replaced to "+RunMode.DROP_EVERY_RUN);
				}
			}

			if (executeMode == null) {
				executeMode = RunMode.DROP_EVERY_RUN;
			}
		}

		log.log(Level.INFO, "Execute mode: ");
		log.log(Level.INFO, executeMode.toString());
	}

	public void startTest(Test test) {
		// ignored
	}

	public void addWarning(Test test) {
		// ignored
	}

	public void startTest(TestInfo testinfo) {
		// ignored
	}

	public void addError(Test test, Throwable t) {
		// ignored
	}

	public void addFailure(Test test, AssertionFailedError t) {
		// ignored
	}

	public synchronized void endTest(Test test) {
		// ignored
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public int getRepeatLeftNumber() {
		return repeatLeftNumber;
	}

	public void setRepeatLeftNumber(int repeatLeftNumber) {
		this.repeatLeftNumber = repeatLeftNumber;
	}

	public int getRepeatNumber() {
		return repeatNumber;
	}

	public void setRepeatNumber(int repeatNumber) {
		this.repeatNumber = repeatNumber;
	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		executionListener.errorOccured(title, message, level);
	}

	public void remotePause() {
		executionListener.remotePause();		
	}

	@Override
	public void endContainer(JTestContainer container) {
		getExecutor().endContainer(container);
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startContainer(JTestContainer container) {
		getExecutor().startContainer(container);
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}
}
