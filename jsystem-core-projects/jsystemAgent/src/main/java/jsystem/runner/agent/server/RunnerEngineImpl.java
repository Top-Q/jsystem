/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.loader.LoadersManager;
import jsystem.runner.projectsync.AutomationProjectUtils;
import jsystem.runner.projectsync.MD5Calculator;
import jsystem.runner.remote.ScenarioExecutor;
import jsystem.utils.ClassSearchUtil;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * Agent execution engine.<br>
 * 
 * @see RunnerEngine
 * @author goland
 */
public class RunnerEngineImpl implements RunnerEngine {

	private ScenarioExecutor executor;

	/**
	 * Tracks engine execution state
	 */
	private ExecutionStateListener executionStateListener;

	private static Logger log = Logger.getLogger(RunnerEngineImpl.class.getName());

	/**
	 * Single point where project path properties are set.
	 * 
	 * @param testClassesPAth
	 */
	public static void setTestsPath(String testClassesPath) {
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, testClassesPath);
		File parent = null;
		try {
			parent = new File(testClassesPath).getCanonicalFile().getParentFile();
		} catch (IOException e) {
			log.log(Level.WARNING,"Failed to get parent of " + testClassesPath);
		}
		if (parent != null) {
			File testsSrcFolder = new File(parent, "tests");
			File resourcesSrcFolder = new File(parent,"tests");
			if (!testsSrcFolder.exists()) {
				//ITAI: This is adaptation to Maven project structure. 
				testsSrcFolder = new File(parent.getParentFile(), "src/main/java");
				if (!testsSrcFolder.exists()) {
					log.log(Level.WARNING, "Tests source folder doesn't exist. " + testsSrcFolder.getAbsolutePath());
				}
				resourcesSrcFolder = new File(parent.getParentFile(), "src/main/resources");
				if (!resourcesSrcFolder.exists()) {
					log.log(Level.WARNING, "Tests source folder doesn't exist. " + testsSrcFolder.getAbsolutePath());
				}

			}
			JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER,
					testsSrcFolder.getPath());
			JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER,
					resourcesSrcFolder.getPath());

		}
	}

	/**
	 */
	public RunnerEngineImpl() throws Exception {
		executor = new ScenarioExecutor();
		redirectOutputStream();
		RunnerListenersManager.getInstance().addListener(new ActiveTestStateListener());
		executionStateListener = new ExecutionStateListener();
		RunnerListenersManager.getInstance().addListener(executionStateListener);
	}

	/**
	 * @see RunnerEngine#getId()
	 */
	public String getId() {
		return "local";
	}

	/**
	 * @see RunnerEngine#changeProject(String)
	 */
	public void changeProject(String classesPath) throws Exception {
		File testsClassesPathFile = new File(classesPath);
		if (!testsClassesPathFile.exists()) {
			throw new Exception("Selected automation project folder doesn't exist. "
					+ testsClassesPathFile.getAbsolutePath());
		}
		if (!testsClassesPathFile.isDirectory()) {
			throw new Exception("Selected automation project path should be a folder. "
					+ testsClassesPathFile.getAbsolutePath());
		}
		setTestsPath(testsClassesPathFile.getPath());
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, "");
		ScenariosManager.getInstance().setScenariosDirectoryFiles(new File(testsClassesPathFile.getPath()));
		refresh();
		Scenario scenario = ScenariosManager.getInstance().getScenario(null);
		setActiveScenario(scenario);
	}

	/**
	 * Checks whether engine was signaled to start execution upon start<br>
	 * if so, starts executing current scenario. This method was written to
	 * support continuous execution of scenario after restart of the engine.
	 * 
	 * should be called by the agent before the {@link #init()} method. (since
	 * the {@link #init()} method sets engine state to idle.
	 * 
	 * @see RunnerStatePersistencyManager
	 */
	public void checkAndRunOnStart() throws Exception {
		Scenario s = null;
		// if we changes scenario state, let's reverse it back
		int[] enabledTests = RunnerStatePersistencyManager.getInstance().getEnabledTests(true);
		if (enabledTests != null) {
			s = ScenariosManager.getInstance().getCurrentScenario();
			s.loadParametersAndValues();
			s.setEnabledTestsIndexes(enabledTests);
			s.save();
		}

		// check whether to execute
		if (!RunnerStatePersistencyManager.getInstance().getRunOnStart()) {
			log.info("Engine was not set to start running.");
			return;
		}

		// turn off the flag so next time engine will start, it will not start
		// running.
		RunnerStatePersistencyManager.getInstance().setRunOnStart(false);
		// get the index of the test to start running from
		int lastActivatedTestIndex = RunnerStatePersistencyManager.getInstance().getActiveTestIndex();
		if (lastActivatedTestIndex == -1) {
			log.info("Invalid start test index");
			return;
		}
		lastActivatedTestIndex++;
		log.info("Starting to run scenario from test number " + lastActivatedTestIndex);
		if (s == null) {
			// get scenario to activate and load scenario
			s = ScenariosManager.getInstance().getCurrentScenario();
			s.loadParametersAndValues();
		}
		// disable all tests before the start test.
		Vector<JTest> v = s.getTests();
		List<JTest> listOfTest = ScenarioHelpers.filterFixtures(v);
		if (lastActivatedTestIndex >= listOfTest.size()) {
			log.info("Start test index equals or bigger then number of tests in scenario");
			return;
		}
		// before changing selected tests, saving current state
		enabledTests = s.getEnabledTestsIndexes();
		RunnerStatePersistencyManager.getInstance().setEnabledTests(enabledTests);

		// now update scenario tests
		Iterator<JTest> testIter = listOfTest.iterator();
		for (int i = 0; i < lastActivatedTestIndex; i++) {
			JTest t = testIter.next();
			t.setDisable(true);
		}
		// save scenario
		s.save();

		log.info("Starting to run. Scenario name is " + s.getName() + " from test index " + lastActivatedTestIndex);

		try {
			//
			// Check if before restart engine was signaled to run a sub
			// scenario.
			// if so, run the sub scenario.
			//
			String internalScenarioId = RunnerStatePersistencyManager.getInstance().getInternalScenarioUUID();
			if (!StringUtils.isEmpty(internalScenarioId)) {
				try {
					run(internalScenarioId);
				} finally {
					RunnerStatePersistencyManager.getInstance().setInternalScenarioUUID("");
				}
			} else {
				run();
			}
		} finally {
			// if we changes scenario state, let's reverse it back
			enabledTests = RunnerStatePersistencyManager.getInstance().getEnabledTests(true);
			if (enabledTests != null) {
				s.setEnabledTestsIndexes(enabledTests);
				s.save();
			}
		}
		log.info("run on start ended");
	}

	/**
	 * @see RunnerEngine#changeSut(String)
	 */
	public void changeSut(String sutName) throws Exception {
		if (sutName == null) {
			sutName = "";
			log.warning("Sut file name is set to empty value");
		}
		SutFactory.getInstance().setSut(sutName);
	}

	/**
	 * @see RunnerEngine#enableRepeat(boolean)
	 */
	@Override
	public void enableRepeat(boolean isEnabled) throws Exception {
		executor.setRepeat(isEnabled);
	}

	/**
	 * @see RunnerEngine#pause()
	 */
	@Override
	public void pause() throws Exception {
		executor.pause();
	}

	/**
	 * @see RunnerEngine#run()
	 */
	@Override
	public void run() throws Exception {
		executor.execute();
	}

	@Override
	public void run(String uuid) throws Exception {
		// note that is is important that setting runner internal scenario will
		// be done before executing scenario
		// so scenario id will be saved in case agent is restarted.
		RunnerStatePersistencyManager.getInstance().setInternalScenarioUUID(uuid);
		executor.execute(uuid);
	}

	/**
	 * @see RunnerEngine#stop()
	 */
	public void stop() throws Exception {
		executor.stop();

	}

	/**
	 * @see RunnerEngine#gracefulStop()
	 */
	public void gracefulStop() throws Exception {
		executor.gracefulStop();
	}

	/**
	 * @see RunnerEngine#resume()
	 */
	public void resume() throws Exception {
		executor.resume();
	}

	/**
	 * @see RunnerEngine#setRepeat(int)
	 */
	public void setRepeat(int number) throws Exception {
		executor.setRepeatNumber(number);
	}

	/**
	 * @see RunnerEngine#setActiveScenario(Scenario)
	 */
	public void setActiveScenario(Scenario scenario) throws Exception {
		ScenariosManager.getInstance().setCurrentScenario(scenario);
	}

	/**
	 * @see RunnerEngine#addListener(Object)
	 */
	public void addListener(Object eventListsner) {
		RunnerListenersManager.getInstance().addListener(eventListsner);
	}

	/**
	 * @see RunnerEngine#removeListener(Object)
	 */
	public void removeListener(Object eventListsner) {
		RunnerListenersManager.getInstance().removeListener(eventListsner);
	}

	/**
	 * @see RunnerEngine#removeListener(Object)
	 */
	public void initReporters() {
		((RunnerListenersManager) RunnerListenersManager.getInstance()).initReporters();
	}

	/**
	 * If a scenario is executed, stops scenario execution.
	 * 
	 * @see RunnerEngine#close()
	 */
	public void close() {
		try {
			stop();
		} catch (Exception e) {
			log.log(Level.FINE, "Failed stopping execution", e);
		}
	}

	/**
	 * 
	 */
	public void init() throws Exception {
		// set engine state to idle.
		executionStateListener.executionEnded("");
	}

	/**
	 * @see RunnerEngine#getLogUrl()
	 */
	public URL getLogUrl() throws Exception {
		String logDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER);
		File f = new File(logDir, "current/index.html");
		String uri = f.toURI().toString().replace("\\", "/");
		return new URL(uri);
	}

	/**
	 * @see RunnerEngine#refresh()
	 */
	public void refresh() throws Exception {
		LoadersManager.getInstance().dropAll();
		JSystemProperties.getInstance().rereadPropertiesFile();
		FixtureManager.getInstance().initFixtureModel();
		SutFactory.resetSutFactory();
		// ScenariosManager.init();
		HtmlCodeWriter.init();
	}

	/**
	 * in case that the path of file is relative, it converts it to absolute
	 * path to the tests classes directory
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private File getFileUsingPath(String path) throws Exception {
		if (!FileUtils.isRelativePath(path)) {
			return new File(path);
		}

		File projectClassesDir = new File(JSystemProperties.getCurrentTestsPath().substring(0,
				JSystemProperties.getCurrentTestsPath().length())).getParentFile();

		return new File(projectClassesDir, path);
	}

	/**
	 * Takes the stdout and stderr of the process and redirects them to a file
	 * and to the console to enable it use "stdout.file.name" attribute. <br>
	 * It should be added to the jsystem.properties file with the file name to
	 * redirect the output to. <br>
	 * If "console.disable" is set to true the console output will be blocked.<br>
	 * The class that is used to merge the streams is StdFilePrintStream.
	 */
	private void redirectOutputStream() throws Exception {
		String stdOutFile = JSystemProperties.getInstance().getPreference(FrameworkOptions.STDOUT_FILE_NAME);
		// if set will redirect stdout/stderr to a file
		if (stdOutFile == null) {
			return;
		}

		boolean isAppend = Boolean.parseBoolean(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.STDOUT_FILE_APPEND));
		PrintStream fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(
				getFileUsingPath(stdOutFile), isAppend)));

		StdFilePrintStream streamOut;
		StdFilePrintStream streamErr;
		// if disabled console send null as the second stream
		if ("true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.CONSOLE_DISABLE))) {
			streamOut = new StdFilePrintStream(fileStream, null);
			streamErr = new StdFilePrintStream(fileStream, null);
		} else {
			streamOut = new StdFilePrintStream(fileStream, System.out);
			streamErr = new StdFilePrintStream(fileStream, System.err);
		}
		System.setOut(streamOut);
		System.setErr(streamErr);
	}

	/**
	 * 
	 */
	@Override
	public String getActiveScenario() throws Exception {
		return ScenariosManager.getInstance().getCurrentScenario().getName();
	}

	/**
	 * 
	 */
	@Override
	public String getCurrentProjectName() throws Exception {
		String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		return new File(classesFolder).getParentFile().getPath();
	}

	/**
	 * 
	 */
	@Override
	public RunnerEngineExecutionState getEngineExecutionState() {
		return executionStateListener.getExecutionState();
	}

	/**
	 * Calculates project's MD5
	 */
	public String calculateCurrentProjectMD5() throws Exception {
		String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		MD5Calculator calculator = new MD5Calculator(new File(classesFolder));
		if (!AutomationProjectUtils.isValidProject(new File(classesFolder))) {
			throw new Exception("Invalid current project");
		}
		Scenario s = ScenariosManager.getInstance().getCurrentScenario();
		return calculator.calculateProjectMD5(s.getName());
	}

	@Override
	public ConnectionState getConnectionState() {
		return ConnectionState.connected;
	}

	@Override
	public String getEngineVersion() throws Exception {
		return ClassSearchUtil.getPropertyFromClassPath("META-INF/jsystemAgent.build.properties", "jversion");
	}

	@Override
	public Properties getRunProperties() throws Exception {
		return RunProperties.getInstance().getRunProperties();
	}

	public void setEnabledTests(int[] enabledTestsIndices) throws Exception {
		Scenario s = ScenariosManager.getInstance().getCurrentScenario();
		s.loadParametersAndValues();
		s.setEnabledTestsIndexes(enabledTestsIndices);
		s.save();
	}
}
