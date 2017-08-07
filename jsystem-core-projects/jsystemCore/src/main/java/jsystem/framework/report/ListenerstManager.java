/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.runner.ErrorLevel;
import jsystem.runner.SOCheckStatus;
import jsystem.runner.remote.RemoteTestRunner;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;

/**
 * Manage the listeners in the framework: TestListener FixtureListener
 * TestReporter
 * <br>
 * <br>
 * The ListenersManager is a Reporter implementation on the test VM, when executing from the JRunner.<br>
 * It dispatches events to all Listeners registered to it and passes on events to the JRunner JVM
 * 
 * @author Guy Arieli
 */
public class ListenerstManager extends DefaultReporterImpl implements
		JSystemListeners {

	private static Logger log = Logger.getLogger(ListenerstManager.class
			.getName());

	private static JSystemListeners manager = null;

	ArrayList<Object> listeners = new ArrayList<Object>();

	boolean silent = false;

	boolean timeStampEnabled = true;

	private static boolean lastTestFail = false;

	boolean isPause = false;

	Object pasueSynch = new Object();

	RemoteTestRunner remoteRunner = null;

	boolean failDebug = false;

	/**
	 * signals if stop was pressed
	 */
	volatile boolean isGracefulStop = false;

	/**
	 * signals if an exception should be thrown (to stop the test)
	 */
	volatile boolean throwException = false;

	public void addListener(Object listener) {
		listeners.add(listener);
	}

	public void removeListener(Object listener) {
		listeners.remove(listener);
	}

	/**
	 * A RunnerListenersManager will be returned if the JVM is the Reporter VM,
	 * otherwise, if the executed from the JRunner and on the test VM, return ListenersManager instance
	 * 
	 * @return
	 */
	public static JSystemListeners getInstance() {
		if (JSystemProperties.getInstance().isReporterVm()
				|| !("true".equals(System
						.getProperty(RunningProperties.RUNNER_EXIST)))) {
			return RunnerListenersManager.getInstance();
		}
		if (manager == null) {
			manager = new ListenerstManager();
		}
		return manager;
	}

	public static boolean isInit() {
		return (manager != null);
	}

	private ListenerstManager() {
		remoteRunner = RemoteTestRunner.getInstance();
	}

	public synchronized void addError(Test test, Throwable t) {
		if (isSilent()) {
			return;
		}
		if (!isFailToPass()) {
			if (failDebug) {
				writeToDebugFile("Test was set to fail from addError\n"
						+ StringUtils.getStackTrace(t));
			}
			setLastTestFailed(true);
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof TestListener) {
				TestListener tl = (TestListener) o;
				try {
					tl.addError(test, t);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to add error to testlistener",
							ex);
				}
			}
		}
		remoteRunner.addError(test, t);
	}

	public synchronized void addError(Test test, String message, String stack) {
		throw new RuntimeException("Unexpected call to addError");
	}

	public synchronized void addFailure(Test test, AssertionFailedError t) {
		if (isSilent()) {
			return;
		}

		if (!isFailToPass()) {
			if (failDebug) {
				writeToDebugFile("Test was set to fail from addFailure\n"
						+ StringUtils.getStackTrace(t));
			}
			setLastTestFailed(true);
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof TestListener) {
				TestListener tl = (TestListener) o;
				try {
					tl.addFailure(test, t);
				} catch (Throwable ex) {
					log.log(Level.SEVERE,
							"Fail to add failure to testlistener", ex);
				}

			}
		}
		remoteRunner.addFailure(test, t);
	}

	public synchronized void addFailure(Test test, String message,
			String stack, boolean analyzerException) {
		throw new RuntimeException("Unexpected call to addFailure");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsystem.framework.report.ExtendTestListener#addWarning(junit.framework
	 * .Test)
	 */
	public synchronized void addWarning(Test test) {
		if (isSilent()) {
			return;
		}

		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) o;
				try {
					tl.addWarning(test);
				} catch (Throwable ex) {
					log.log(Level.SEVERE,
							"Fail to add warning to testlistener", ex);
				}
			}
		}
		remoteRunner.addWarning(test);
	}

	public synchronized void endTest(Test test) {
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			TestListener tl = (TestListener) o;
			try {
				tl.endTest(test);
			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Fail to add endTest", ex);
			}
		}
		remoteRunner.endTest(test);
		checkExecutionStatus();
	}

	public void startTest(TestInfo testInfo) {
		// not implemented (will use startTest(Test test))
	}

	public synchronized void startTest(Test test) {
		if (isGracefulStop) {
			remoteRunner.exit();
			remoteRunner.processExit();
			isGracefulStop = false;
			return;
		}
		if (failDebug) {
			writeToDebugFile("**** startTest: " + test.getClass().getName()
					+ "." + ((TestCase) test).getName());

		}
		/*
		 * See that no reports are buffered
		 */
		stopBufferingReports();
		clearReportsBuffer();

		updateCurrentTest(test);
		setFailToPass(false);
		setFailToWarning(false);
		setLastTestFailed(false);
		this.silent = false;
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof TestListener) {
				TestListener tl = (TestListener) o;
				try {
					tl.startTest(test);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to startTest", ex);
				}
			}
		}
		remoteRunner.startTest(test);
	}

	public synchronized void aboutToChangeTo(Fixture fixture) {
		remoteRunner.aboutToChangeTo(fixture);
	}

	public synchronized void fixtureChanged(Fixture fixture) {
		remoteRunner.fixtureChanged(fixture);
	}

	public synchronized void startFixturring() {
		remoteRunner.startFixturring();
	}

	public synchronized void endFixturring() {
		remoteRunner.endFixturring();
	}

	public void setSilent(boolean status) {
		this.silent = status;
		remoteRunner.setSilent(status);
	}

	public boolean isSilent() {
		return silent;
	}

	public void setTimeStamp(boolean enable) {
		timeStampEnabled = enable;
		remoteRunner.setTimeStamp(enable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendReporter#saveFile(java.lang.String,
	 * java.io.InputStream)
	 */
	public synchronized void saveFile(String fileName, byte[] content) {
		try {

			File file = new File(getCurrentTestFolder(), fileName);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content);
			out.close();
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to save file", e);
		}
		checkExecutionStatus();
		;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendReporter#report(java.lang.String,
	 * java.lang.String, int, boolean)
	 */
	public synchronized void setData(String data) {
		if (isSilent()) {
			return;
		}
		remoteRunner.setData(data);
		checkExecutionStatus();
	}

	public synchronized void sutChanged(String sutName) {
		remoteRunner.sutChanged(sutName);
	}

	public synchronized void scenarioChanged(Scenario current,
			ScenarioChangeType changeType) {
		// for (int i = 0; i < listeners.size(); i++){
		// Object o = listeners.get(i);
		// if (o instanceof ScenarioListener){
		// try {
		// ((ScenarioListener)o).scenarioChanged(current);
		// } catch (Throwable ex){
		// log.log(Level.SEVERE, "Fail to scenarioChanged", ex);
		// }
		// }
		// }
	}

	public synchronized void scenarioDirectoryChanged(File directory) {
		// for (int i = 0; i < listeners.size(); i++){
		// Object o = listeners.get(i);
		// if (o instanceof ScenarioListener){
		// try {
		// ((ScenarioListener)o).scenarioDirectoryChanged(directory);
		// } catch (Throwable ex){
		// log.log(Level.SEVERE, "Fail to scenarioDirectoryChanged", ex);
		// }
		// }
		// }
	}

	public synchronized void startReport(String name, String parameters,
			String classDoc, String testDoc) {
		if (isSilent()) {
			return;
		}

		remoteRunner.startReport(name, parameters, classDoc, testDoc);
		checkExecutionStatus();
	}

	public void startLevel(String level, int place) throws IOException {
		if (isSilent()) {
			return;
		}
		if (buffering) {
			if (reportsBuffer == null) {
				reportsBuffer = new ArrayList<ReportElement>();
			}
			ReportElement re = new ReportElement();
			re.setTitle(level);
			re.setOriginator(Thread.currentThread().getName());
			re.setTime(System.currentTimeMillis());
			re.setStartLevel(true);
			re.setLevelPlace(place);
			reportsBuffer.add(re);
			return;
		}
		remoteRunner.startLevel(level, place);
	}

	public void stopLevel() throws IOException {
		if (isSilent()) {
			return;
		}
		if (buffering) {
			if (reportsBuffer == null) {
				reportsBuffer = new ArrayList<ReportElement>();
			}
			ReportElement re = new ReportElement();
			re.setOriginator(Thread.currentThread().getName());
			re.setTime(System.currentTimeMillis());
			re.setStopLevel(true);
			reportsBuffer.add(re);
			return;
		}
		remoteRunner.stopLevel();
	}

	public synchronized void endReport(String steps, String failCause) {
		if (isSilent()) {
			return;
		}
		remoteRunner.endReport(steps, failCause);
		checkExecutionStatus();
	}

	/**
	 * <b>NOTICE</b>: This event is not send to the system objects.
	 */
	public synchronized void endRun() {
		remoteRunner.endRun();
	}

	public void setFailToPass(boolean status) {
		super.setFailToPass(status);
		remoteRunner.setFailToPass(status);
	}

	public void setFailToWarning(boolean status) {
		super.setFailToWarning(status);
		remoteRunner.setFailToWarning(status);
	}

	public void pause() throws Exception {
		SystemManagerImpl.getInstance().pausedAllObjects();
		isPause = true;
	}

	/**
	 * the stop button has been pressed - raise a gracefull stop flag<br>
	 * next reporter action will throw an exception
	 * 
	 * @throws Exception
	 */
	public void gracefulStop() throws Exception {
		isGracefulStop = true;
		throwException = true;
	}

	public boolean isPause() {
		return isPause;
	}

	/**
	 * was graceful stop set (stop pressed)
	 * 
	 * @return
	 */
	public boolean isGracefulStop() {
		return isGracefulStop;
	}

	/**
	 * cancels the exception throwing due to graceful stop
	 * 
	 */
	public void cancelExceptionThrowing() {
		throwException = false;
	}

	public void resume() {
		isPause = false;
		paused = false;
		synchronized (pasueSynch) {
			pasueSynch.notifyAll();
		}
		SystemManagerImpl.getInstance().resumeAllObjects();
	}

	boolean paused = false;

	public boolean isPaused() {
		return paused;
	}

	private void checkExecutionStatus() {
		checkGracefulStop();
		checkPause();
	}

	/**
	 * check if graceful stop flag has been raised.<br>
	 * throw an exception if it was
	 * 
	 */
	private void checkGracefulStop() {
		if (throwException) {
			throwException = false;
			throw new GracefulStopException("User aborted test");
		}
	}

	private void checkPause() {
		if (isPause) {
			remoteRunner.paused();
			paused = true;
			synchronized (pasueSynch) {
				while (isPause) {
					try {
						pasueSynch.wait(5000);
					} catch (InterruptedException e) {
						// ignored
					}
				}
			}
		}
	}

	public synchronized void report(String title, String message, int status,
			boolean bold, boolean html, boolean step, boolean link, long time) {
		checkExecutionStatus();
		if (isSilent()) {
			return;
		}
		if (buffering) {
			if (reportsBuffer == null) {
				reportsBuffer = new ArrayList<ReportElement>();
			}
			ReportElement currentReportElement = new ReportElement();
			currentReportElement.setTitle(title);
			currentReportElement.setMessage(message);
			currentReportElement.setStatus(status);
			currentReportElement.setBold(bold);
			currentReportElement.setHtml(html);
			currentReportElement.setStep(step);
			currentReportElement.setLink(link);
			currentReportElement
					.setOriginator(Thread.currentThread().getName());
			currentReportElement.setTime(time);
			reportsBuffer.add(currentReportElement);
			if (!printBufferdReportsInRunTime) {
				return;
			}
		}
		remoteRunner.report(title, String.valueOf(message), status, bold, html,
				step, link, time);
		if (status == Reporter.FAIL && !isFailToPass() && !isFailToWarning() && !isSilent()) {
			if (failDebug) {
				writeToDebugFile("Test was set to fail from report");
			}
			setLastTestFailed(true);
			if (systemTest != null) {
				systemTest.setPass(false);
				systemTest.addFailCause(title);
			}
		}
		
		if (step) {
			if (systemTest != null) {
				systemTest.addExecutedSteps(title);
			}
		}
	}

	public void blockReporters(boolean block) {
		// ignore
	}

	public boolean getLastTestFailed() {
		return lastTestFail;
	}

	public void setLastTestFailed(boolean fail) {
		lastTestFail = fail;
	}

	private void writeToDebugFile(String toWrite) {
		try {
			FileWriter fw = new FileWriter("debug.txt", true);
			fw.write(toWrite);
			fw.write("\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "couldn't close fileWriter");
		}
	}

	public void addProperty(String key, String value) {
		checkExecutionStatus();
		if (isSilent()) {
			return;
		}

		if (StringUtils.hasNotAllowedSpecialCharacters(key)
				|| StringUtils.hasNotAllowedSpecialCharacters(value)) {
			String property = key + "=" + value;
			String title2 = "Warning: found unallowed characters from \""
					+ StringUtils.notAllowedCharacters + "\""
					+ " in property: " + property;
			report(title2, false);
			return;
		}

		remoteRunner.addProperty(key, value);
	}

	public int showConfirmDialog(String title, String message, int optionType,
			int messageType) {
		if (isSilent()) {
			return 0;
		}
		return remoteRunner.showConfirmDialog(title, message, optionType,
				messageType);
	}

	public void checkSystemObjectStatus(String soName, SOCheckStatus status,
			String errorMessage) {
		if (isSilent()) {
			return;
		}
		remoteRunner.checkSystemObjectStatus(soName, status, errorMessage);
	}

	public void startLevel(String level, EnumReportLevel place)
			throws IOException {
		startLevel(level, place.value());
	}

	public void executionEnded(String scenarioName) {
		// TODO Auto-generated method stub

	}

	public void remoteExit() {
		// TODO Auto-generated method stub

	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		// TODO Auto-generated method stub

	}

	public void remotePause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(Test t) throws Exception {
		remoteRunner.saveState(t);
	}

	@Override
	public void endContainer(JTestContainer container) {
		if (isSilent()) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) o;
				try {
					tl.endContainer(container);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Failed to send end container to extend test listener", ex);
				}
			}
		}

	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startContainer(JTestContainer container) {
		if (isSilent()) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object o = listeners.get(i);
			if (o instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) o;
				try {
					tl.startContainer(container);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Failed to send start container to extend test listener", ex);
				}
			}
		}

	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeAllLevels() throws IOException {
		remoteRunner.closeAllLevels();
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void initReporters() {
		remoteRunner.initReporters();
		
	}

	@Override
	public void flushReporters() {
		remoteRunner.flushReporters();
		
	}

	@Override
	public void setContainerProperties(int ancestorLevel, String key,
			String value) {
		remoteRunner.setContainerProperties(ancestorLevel, key, value);
	}



}
