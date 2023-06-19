/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.difido.HtmlReporter;
import jsystem.extensions.report.html.CssUtils.CssType;
import jsystem.extensions.report.html.ExtendLevelTestReporter;
import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.extensions.report.html.HtmlTestReporter;
import jsystem.extensions.report.html.LevelHtmlTestReporter;
import jsystem.extensions.report.html.RepeatTestIndex;
import jsystem.extensions.report.junit.JUnitReporter;
import jsystem.extensions.report.xml.XmlReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.common.CommonResources;
import jsystem.framework.common.JSystemInnerTests;
import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureListener;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioAsTest;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.UpgradeAndBackwardCompatibility;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.sut.SutListener;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.framework.system.TName;
import jsystem.framework.system.TestNameServer;
import jsystem.runner.ErrorLevel;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.DateUtils;
import jsystem.utils.StackTraceUtil;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.NamedTest;
import junit.framework.SystemTest;
import junit.framework.SystemTestCase;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * The RunnerListenersManager is the reporter implementation on the Reporter VM.<br>
 * When running from the JRunner, it is on the JRunner VM.<br>
 * When running from Ant\IDE, it is on the Test VM.
 * 
 */
public class RunnerListenersManager extends DefaultReporterImpl implements JSystemListeners {

	public static final String PARAMETERS_END = "Parameters end.";

	public static final String PARAMETERS_START = "Parameters:";

	private static final String RUNNER_REPORTERS_STATE_BIN = "runnerReportersState.bin";

	private static Logger log = Logger.getLogger(RunnerListenersManager.class.getName());

	private static RunnerListenersManager manager = null;

	ArrayList<Object> listeners = new ArrayList<Object>();

	boolean silent = false;

	int inLoop = 0;

	boolean propertiesTimeStampFlag;

	long startTime = 0;

	long endTime = 0;

	public static boolean lastTestFail = false;

	// This two flags are will be queried in system exit and will influence the
	// runner error level.
	
	//Will be set to true if in some point some test failed. 
	public static boolean hadFailure = false;
	
	//Will be set to true if in some point one test had warning.
	public static boolean hadWarning = false;

	boolean addTimeStamp = true;

	long testsCount = 0;

	public Vector<Test> runningTests;

	private RepeatTestIndex testIndex;

	private String testClassName = null;

	boolean inTest = false;

	boolean blockReporters = false;

	private EventParser parser;

	private boolean inScenarioAsTest = false;

	private String lastTestLevelName = null;

	private Test lastTest = null;

	private long lastGC = System.currentTimeMillis();

	private boolean testMarkedAsKnownIssue = false;
	private boolean scenarioMarkedAsKnownIssue = false;

	private boolean testMarkedNegativeTest = false;
	private boolean scenarioMarkedAsNegativeTest = false;

	private long lastFlashReportTime = 0;

	/**
	 * Flag for signaling an error occurred, added to support NegativeTest
	 * issue, without affecting Reporter signatures
	 */
	private boolean isError = false;

	private boolean showMemory = false;

	public void addListener(Object listener) {
		removeListener(listener);
		listeners.add(listener);
	}

	public void removeListener(Object listener) {
		listeners.remove(listener);
	}

	public static JSystemListeners getInstance() {
		if (manager == null) {
			manager = new RunnerListenersManager();
			deSerializeRunnerListenersManager();
		}
		return manager;
	}

	/**
	 * Loads reporters state if signaled by the user.
	 * 
	 * @see RunnerStatePersistencyManager
	 */
	private static void deSerializeRunnerListenersManager() {
		if (!RunnerStatePersistencyManager.getInstance().getLoadReporters()) {
			return;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(RUNNER_REPORTERS_STATE_BIN);
			manager.deSerializeReporters(fis);
		} catch (Exception e) {
			throw new RuntimeException("Failed deserializing reporters. " + e.getMessage());
		} finally {
			RunnerStatePersistencyManager.getInstance().setLoadReporters(false);
			try {
				fis.close();
			} catch (Exception e) {
			}
			;
		}
	}

	public static boolean isInit() {
		return (manager != null);
	}

	private RunnerListenersManager() {
		testIndex = new RepeatTestIndex();
		String addTimeStampStatus = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_ADD_TIME);
		propertiesTimeStampFlag = addTimeStampStatus == null || addTimeStampStatus.toLowerCase().equals("true");
		String reporters = JSystemProperties.getInstance().getPreference(FrameworkOptions.REPORTERS_CLASSES);
		if (reporters == null) {
			reporters = HtmlReporter.class.getName() + ";" + SystemOutTestReporter.class.getName() + ";"
					+ XmlReporter.class.getName() + ";" + JUnitReporter.class.getName();
			JSystemProperties.getInstance().setPreference(FrameworkOptions.REPORTERS_CLASSES, reporters);
		}
		StringTokenizer st = new StringTokenizer(reporters, ";");

		while (st.hasMoreTokens()) {
			String reporterClassName = st.nextToken();
			try {
				loadReporter(reporterClassName);
			} catch (Exception e) {
				log.log(Level.WARNING, "fail to load reporter: " + reporterClassName, e);
				report("load reporter exception", StringUtils.getStackTrace(e), true);
			}
		}
		runningTests = new Vector<Test>();
		parser = new EventParser(this);
	}

	/**
	 * Saves reporters state
	 * 
	 * @see RunnerStatePersistencyManager
	 */
	@Override
	public void saveState(Test test) throws Exception {
		endTest(test);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(RUNNER_REPORTERS_STATE_BIN);
			serializeReporters(fos);
			RunnerStatePersistencyManager.getInstance().setRunOnStart(true);
			RunnerStatePersistencyManager.getInstance().setLoadReporters(true);
			step("Agent is about to restart");
		} catch (Exception e) {
			log.warning("Failed saving reporters state" + e.getMessage());
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
			;
		}
	}

	/**
	 * Saves reporters state
	 * 
	 * @see RunnerStatePersistencyManager
	 */
	private void serializeReporters(OutputStream stream) throws Exception {
		ObjectOutputStream objectStream = new ObjectOutputStream(stream);
		objectStream.writeObject(UpgradeAndBackwardCompatibility.currentVersion());
		objectStream.writeObject(isSilent());
		objectStream.writeObject(startTime);
		objectStream.writeObject(lastTestFail);
		objectStream.writeObject(addTimeStamp);
		objectStream.writeObject(testIndex);
		objectStream.writeObject(testClassName);
		objectStream.writeObject(blockReporters);
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof Externalizable) {
				objectStream.writeObject(currentObject);
			}
		}
		objectStream.flush();
	}

	/**
	 * Loads reporters state
	 * 
	 * @see RunnerStatePersistencyManager
	 */
	private void deSerializeReporters(InputStream stream) throws Exception {
		ObjectInputStream objectStream = new ObjectInputStream(stream);
		String version = (String) objectStream.readObject();
		if (!UpgradeAndBackwardCompatibility.currentVersion().equals(version)) {
			throw new Exception("Incompatible reporters serialization. Current runner version "
					+ UpgradeAndBackwardCompatibility.currentVersion() + " deserialized reporters version: " + version);
		}
		setSilent((Boolean) objectStream.readObject());
		startTime = (Long) objectStream.readObject();
		lastTestFail = (Boolean) objectStream.readObject();
		addTimeStamp = (Boolean) objectStream.readObject();
		testIndex = (RepeatTestIndex) objectStream.readObject();
		testClassName = (String) objectStream.readObject();
		inTest = false;
		blockReporters = (Boolean) objectStream.readObject();
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof Externalizable) {
				listeners.remove(i);
				listeners.add(i, objectStream.readObject());
			}
		}
	}

	/**
	 * 
	 */
	private void loadReporter(String className) throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		Class<?> reporterClass = LoadersManager.getInstance().getLoader().loadClass(className);
		Object currentObject = reporterClass.newInstance();
		addListener(currentObject);
	}

	public synchronized void addError(Test test, Throwable t) {
		setLastTestFail(true);
		setFailToPass(false);
		setFailToWarning(false);
		try {
			isError = true;
			if (!inKnownIssueState()) {
				report("Fail: " + t.getMessage(), t);
				for (int i = 0; i < listeners.size(); i++) {
					Object currentObject = listeners.get(i);
					if (currentObject instanceof TestListener) {
						TestListener tl = (TestListener) currentObject;
						try {
							tl.addError(test, t);
						} catch (Throwable ex) {
							log.log(Level.SEVERE, "Fail to add error to testlistener", ex);
						}
					}
				}
			} else {
				report("Fail: " + t.getMessage(), StringUtils.getStackTrace(t), Reporter.WARNING);
			}
			reportOnStack(StringUtils.getStackTrace(t), "error");
		} finally {
			isError = false;
		}
	}

	public synchronized void addError(Test test, String message, String stack) {
		setLastTestFail(true);
		setFailToPass(false);
		setFailToWarning(false);
		try {
			isError = true;
			if (!inKnownIssueState()) {
				report("Fail: " + message, stack, !getLastTestFailed());
				for (int i = 0; i < listeners.size(); i++) {
					Object currentObject = listeners.get(i);
					if (currentObject instanceof TestListener) {
						TestListener tl = (TestListener) currentObject;
						try {
							tl.addError(test, new Exception(message));
						} catch (Throwable ex) {
							log.log(Level.SEVERE, "Fail to add error to testlistener", ex);
						}
					}
				}
			} else {
				report("Fail: " + message, stack, Reporter.WARNING);
			}
			reportOnStack(stack, "error");
		} finally {
			isError = false;
		}
	}

	/**
	 * Take a stack trace of the exception error and extract the test line error
	 * and the system object line error. When used from eclipse will enable link
	 * to the code of the test and system object.
	 * 
	 * @param stack
	 *            a string contain the stack trace
	 * @param type
	 *            the type of the exception: fail/error
	 */
	private void reportOnStack(String stack, String type) {
		String testLine = StackTraceUtil.findTheFirstOfType(stack, SystemTestCase.class);
		if (testLine != null) {
			System.err.println("Test " + type + " line: " + testLine);
		}
		String soLine = StackTraceUtil.findTheFirstOfType(stack, SystemObjectImpl.class);
		if (soLine != null) {
			System.err.println("SystemObject " + type + " line: " + soLine);
		}

	}

	public synchronized void addFailure(Test test, AssertionFailedError t) {
		boolean negativeState = false;
		if(inScenarioAsTest){
			negativeState = inNegativeTestState();
		}
		else{
			negativeState = testMarkedNegativeTest;
		}
		setLastTestFail(!inKnownIssueState() && !negativeState);
		setFailToPass(false);
		setFailToWarning(false);
		if (!(t instanceof AnalyzerException)) {
			// bug fix thanks to Jack Kuan from wuerth-phoenix
			String title = t.getMessage();
			if (title == null) {
				title = t.getClass().getName();
			}
			if (negativeState) {
				report(title, StringUtils.getStackTrace(t), Reporter.PASS);
			} else if (inKnownIssueState()) {
				report(title, StringUtils.getStackTrace(t), Reporter.WARNING);
			} else {
				report(title, t);
			}
		}
		if (!inKnownIssueState() && !negativeState) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof TestListener) {
					TestListener tl = (TestListener) currentObject;
					try {
						tl.addFailure(test, t);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to add failure to testlistener", ex);
					}

				}
			}
		}
		reportOnStack(StringUtils.getStackTrace(t), "failure");

	}

	public synchronized void addFailure(Test test, String message, String stack, boolean analyzerException) {
		boolean negativeState = false;
		if(inScenarioAsTest){
			negativeState = inNegativeTestState();
		}
		else{
			negativeState = testMarkedNegativeTest;
		}
		setLastTestFail(!inKnownIssueState() && !negativeState);
		setFailToPass(false);
		setFailToWarning(false);
		if (!analyzerException) {
			// bug fix thanks to Jack Kuan from wuerth-phoenix
			String title = message;
			if (title == null) {
				title = "Failure";
			}
			if (negativeState) {
				report(title, stack, Reporter.PASS);
			} else if (inKnownIssueState()) {
				report(title, stack, Reporter.WARNING);
			} else {
				report(title, stack, false);
			}
		}
		if (!inKnownIssueState() && !negativeState) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof TestListener) {
					TestListener tl = (TestListener) currentObject;
					try {
						tl.addFailure(test, new AssertionFailedError(message));
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to add failure to testlistener", ex);
					}

				}
			}
		}
		reportOnStack(stack, "failure");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsystem.framework.report.ExtendTestListener#addWarning(junit.framework
	 * .Test)
	 */
	public synchronized void addWarning(Test test) {
		hadWarning = true;
		if (isSilent()) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) currentObject;
				try {
					tl.addWarning(test);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to add warning to testlistener", ex);
				}
			}
		}

	}

	/**
	 * Right split - under top rectangle adding the starting and ending time of
	 * the test
	 */
	private void addEndTestInfo() {
		addEndTestInfo(0);
	}

	/**
	 * Right split - under top rectangle adding the starting and ending time of
	 * the test
	 */
	private void addEndTestInfo(long endTime) {
		if (endTime <= 0) {
			endTime = System.currentTimeMillis();
		}
		boolean addTimeStampOld = addTimeStamp;
		addTimeStamp = false;
		report("Start time: " + DateUtils.getDate(startTime));
		report("End time  : " + DateUtils.getDate(endTime));
		long runningTime = (endTime - startTime);
		if (runningTime == 0) {
			runningTime = 1;
		}
		report("Test running time: " + (runningTime / 1000) + " sec.");

		if (showMemory) {
			report("Total Memory: " + Runtime.getRuntime().totalMemory());
			report("Max Memory:   " + Runtime.getRuntime().maxMemory());
			report("Free Memory:  " + Runtime.getRuntime().freeMemory());
		}

		addTimeStamp = addTimeStampOld;

	}

	public synchronized void endTest(Test test) {
		if (test == null) {
			test = currentTest;
		}
		runningTests.removeElement(test);
		if (!(currentTest instanceof InternalTest) && !blockReporters && !inScenarioAsTest) {
			addEndTestInfo();
		}
		
		if (inScenarioAsTest) {
			try {
				stopLevel();
				updateNegativeTest(test);
				fireStatusManager(null, currentTest, false);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Fail to stopLevel", e);
			}
		} else {
			fireEndTest(test, blockReporters);
			updateCurrentTest(null);
			inTest = false;
			if (System.currentTimeMillis() - lastGC > 60000) {
				System.gc();
				lastGC = System.currentTimeMillis();
			}
		}
		if (!blockReporters) {
			endTime = System.currentTimeMillis();
		}

		if ("true".equals(JSystemProperties.getInstance().getPreferenceOrDefault(
				FrameworkOptions.SAVE_REPORTERS_ON_RUN_END))) {
			int interval = Integer.parseInt(JSystemProperties.getInstance().getPreferenceOrDefault(
					FrameworkOptions.SAVE_REPORTERS_INTERVAL));
			if (interval > -1 && (System.currentTimeMillis() - lastFlashReportTime) > (interval * 1000)) {
				flushReporters();
				lastFlashReportTime = System.currentTimeMillis();
			}
		}

	}

	private void fireStatusManager(TestInfo ti, Test test, boolean start) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof TestStatusListener) {
				try {
					if (start) {
						if (currentObject instanceof ExtendTestListener) {
							((ExtendTestListener) currentObject).startTest(ti);
						} else {
							((TestListener) currentObject).startTest(test);
						}
					} else {
						((TestListener) currentObject).endTest(test);
					}
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to signal status manager", ex);
				}
			}
		}
	}

	/**
	 * If test is marked as Negative, update status according to test results
	 * 
	 * @param test
	 */
	private void updateNegativeTest(Test test) {
		if (test == null) {
			return;
		}
		if (testMarkedNegativeTest) {
			// Test is marked as negative test
			RunnerTest runnerTest = ScenarioHelpers.getRunnerTest(test);
			if (runnerTest.isPassAssumingFlags()) {
				// Test Failed as expected - report success
				step("Negative test - Test failed as expected");
				if (!runnerTest.isWarning()) {
					// warning should not change to pass (support for known
					// issue tests)
					runnerTest.setStatus(RunnerTest.STAT_SUCCESS);
				}
			} else if (!runnerTest.isError()) {
				// Test passed but should have failed - report error
				report(CommonResources.NEGATIVE_TEST_STRING, false);
				runnerTest.setStatus(RunnerTest.STAT_FAIL);
			}
		}
	}

	private void fireEndTest(Test test, boolean maskReporters) {
		updateNegativeTest(test);
		// First notify non reporters
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof TestListener && !(currentObject instanceof TestReporter)) {
				TestListener tl = (TestListener) currentObject;
				try {
					tl.endTest(test);
				} catch (Throwable ex) {
					if (test == null) {
						continue;
					}
					log.log(Level.SEVERE, "Fail to add endTest", ex);
				}
			}
		}
		if (inTest) {
			// then notify reporters
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof TestListener && (currentObject instanceof TestReporter) && !maskReporters) {
					TestListener tl = (TestListener) currentObject;
					try {
						tl.endTest(test);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to add endTest", ex);
					}
				}
			}
		}
	}

	public void startTest(TestInfo testInfo) {
		// not implemented use startTest(Test) ...
	}

	public synchronized void startTest(Test test) {
		failToPass = false;
		failToWarning = false;

		/*
		 * See that no reports are buffered
		 */
		stopBufferingReports();
		clearReportsBuffer();

		if (inTest && !inScenarioAsTest) {
			endTest(currentTest);
		}
		if (test instanceof SystemTest) {
			((SystemTest) test).clearDocumentation();
			((SystemTest) test).clearFailCause();
			((SystemTest) test).clearSteps();
			((SystemTest) test).initFlags();
		
		}
		
		// if test is ran not from the runner\ANT (eclipse for example), the
		// flags are currently not supported
		if (!JSystemProperties.getInstance().isExecutedFromIDE() && test instanceof NamedTest) {
			Properties properties = ScenarioHelpers.getAllTestPropertiesFromAllScenarios(
					((NamedTest) test).getFullUUID(), false);
			String testMarkedAsKnownIssueStr = properties.getProperty(RunningProperties.MARKED_AS_KNOWN_ISSUE);
			String testMarkedAsNegativeTestStr = properties.getProperty(RunningProperties.MARKED_AS_NEGATIVE_TEST);
			testMarkedAsKnownIssue = testMarkedAsKnownIssueStr != null
					&& Boolean.parseBoolean(testMarkedAsKnownIssueStr);
			testMarkedNegativeTest = testMarkedAsNegativeTestStr != null
					&& Boolean.parseBoolean(testMarkedAsNegativeTestStr);
		}
		runningTests.addElement(test);
		testsCount++;
		setSilent(false);
		addTimeStamp = propertiesTimeStampFlag;
		setLastTestFail(false);
		TName tname = TestNameServer.getInstance().getTestName(test);
		int count = testIndex.getTestIndex(tname.getFullName());
		lastTest = test;
		if (!inScenarioAsTest) {
			updateCurrentTest(test);
		}
		RunnerTest rt = null;
		/*
		 * Init the meaningful name from the RunnerTest will be done only when
		 * executed from the runner\ANT
		 */
		String meaningfulName = null;
		String uuid = "";
		if (!JSystemProperties.getInstance().isExecutedFromIDE()) {
			//We probably run from ANT
			rt = ScenariosManager.getInstance().getCurrentScenario().findRunnerTest(test);
			parser.startTest(rt);
			if (rt != null) {
				meaningfulName = rt.getMeaningfulName();
				uuid = rt.getFullUUID();
			}
		} 
		else if (test instanceof NamedTest) {
			//We run from IDE
			uuid = ((NamedTest) test).getFullUUID();
			if (!StringUtils.isEmpty(uuid)) {
				//I think that we never get to this block because when we run from IDE we never get UUID.
				try {
					rt = (RunnerTest) ScenarioHelpers.getTestById(ScenariosManager.getInstance().getCurrentScenario(),
							uuid);
					if (rt != null) {
						parser.startTest(rt);
						meaningfulName = rt.getMeaningfulName();
					}
				} catch (Exception e) {
					log.log(Level.FINE, "Failed getting RunnerTest", e);
				}
			}
		}

		TestInfo ti = new TestInfo();
		ti.className = tname.getClassName();
		ti.methodName = tname.getMethodName();
		ti.meaningfulName = meaningfulName;
		ti.comment = tname.getComment();
		ti.parameters = tname.getParamsString();
		ti.count = count;
		ti.fullUuid = uuid;
		if (rt != null) {
			ti.isHiddenInHTML = rt.isHiddenInHTML();
		}
		if (inScenarioAsTest) {
			try {
				String name = tname.getClassName() + "-" + tname.getMethodName();
				if (!StringUtils.isEmpty(ti.meaningfulName)) {
					name = ti.meaningfulName;
				}
				startLevel(name, Reporter.MainFrame);
				lastTestLevelName = name;

				((ScenarioAsTest) currentTest).setCurrentRunnerTest(test);
				fireStatusManager(ti, currentTest, true);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Fail to startLevel", e);
			}
		} else {
			fireTestStart(ti, test, false);
			if (!blockReporters) {
				startTime = System.currentTimeMillis();
			}
		}
		boolean disableCode = "false".equals(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TEST_CODE_ENABLE));
		addTestInfo(tname, meaningfulName, test, disableCode, null, null, rt);
		testClassName = test.getClass().getName();

		inTest = true;
	}

	private void fireTestStart(TestInfo testInfo, Test test, boolean maskReporters) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);

			if (currentObject instanceof ExtendTestListener) {
				if (!blockReporters) {
					ExtendTestListener tl = (ExtendTestListener) currentObject;
					try {
						tl.startTest(testInfo);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to startTest", ex);
					}
				}
			} else {
				if (currentObject instanceof TestListener) {
					if (currentObject instanceof TestReporter && maskReporters) {
						continue;
					}
					TestListener tl = (TestListener) currentObject;
					try {
						if (test != null) {
							tl.startTest(test);
						}
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to startTest", ex);
					}
				}
			}
		}
	}

	/**
	 * right Split - top rectangle data , documentations, parameters
	 * 
	 * @param tname
	 *            name tab
	 * @param test
	 *            the junit test
	 * @param disableCode
	 * @param cDoc
	 *            class doc
	 * @param tDoc
	 *            test doc
	 */
	private void addTestInfo(TName tname, String meaningfulName, Test test, boolean disableCode, String cDoc,
			String tDoc, JTest testPresentationInRunner) {
		boolean addTimeStampOld = addTimeStamp;
		addTimeStamp = false;
		report(CssType.TEST_INFO_TABLE.getCssStart(), ReportAttribute.HTML);
		startSection();
		String name = tname.getBasicName();
		report("Test: " + name);
		if (meaningfulName != null) {
			name = meaningfulName;
		}
		report("(" + testsCount + ")Steps in test: " + name + " :");
		String params = tname.getParamsString();
		report(CssType.PARAMETERS.getCssStart(), ReportAttribute.HTML);
		if (params != null) {
			if ("true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_LOG_PARAMS_IN_LEVEL))) {
				try {
					startLevel("Test paremeters", 0);
				} catch (Exception e) {
				}
				;
			}
			report(PARAMETERS_START, null, true, true);
			try {
				/*
				 * separating parameters to array and printing each in a new
				 * line
				 */
				String[] seperatedParams = ParameterUtils.stringToPropertiesArray(params);
				for (int i = 0; i < seperatedParams.length; i++) {
					report(seperatedParams[i]);
				}
			} catch (Exception e) {
				report("Failed parsing test parameters. " + e.getMessage());
			} finally {
				if ("true".equals(JSystemProperties.getInstance().getPreference(
						FrameworkOptions.HTML_LOG_PARAMS_IN_LEVEL))) {
					try {
						stopLevel();
					} catch (Exception e) {
					}
					;
				}
				report(PARAMETERS_END, null, true, true);
				report(CssType.getCssClosingTag(), ReportAttribute.HTML);
			}
		}
		// Inner JSystem tests should not display source code in HTML
		if (!disableCode && !JSystemInnerTests.isInnerTest(tname.getClassName(), tname.getMethodName())) {
			/*
			 * Add the test code to the report
			 */
			String code;
			try {
				code = HtmlCodeWriter.getInstance().getCode(tname.getClassName());
				code = code.replaceAll(tname.getMethodName(), "<b>" + tname.getMethodName() + "</b>");
				reportHtml("test code", code, true);
			} catch (FileNotFoundException e) {
				log.log(Level.WARNING, "Fail to load test code because source file is missing. " + e.getMessage());
			} catch (ClassNotFoundException e) {
				reportHtml(
						"Can't display test code because java2html.jar is missing.",
						"If you wish to view code, please install java2html.jar. For instructions go to <a href=\"http://trac.jsystemtest.org/wiki/DetailedOSProjectsList\">JSystem Trac</a>",
						true);
				log.log(Level.WARNING, "Fail to load test code because java2html jar is missing. " + e.getMessage());
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to load test code. " + e.getMessage());
			}
		}

		/*
		 * Add a link to the setup if exists
		 */
		String link = SutFactory.getInstance().getSutInstance().getSetupLink();

		if (link != null) {
			addLink("sut: " + SutFactory.getInstance().getSutInstance().getSetupName(), link);
		}
		String classDoc = null;
		String testDoc = null;
		if (cDoc != null) {
			classDoc = cDoc;
		} else {
			try {
				classDoc = HtmlCodeWriter.getInstance().getClassJavaDoc(tname.getClassName());
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to process document", e);
			}
		}
		if (tDoc != null) {
			testDoc = tDoc;
		} else {
			try {
				testDoc = HtmlCodeWriter.getInstance().getMethodJavaDoc(tname.getClassName(), tname.getMethodName());
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to process document", e);
			}
		}
		if (classDoc != null && (!classDoc.equals(""))) {
			String[] lines = classDoc.split("[\\r\\n]+");
			report(CssType.CLASS_DOCUMENTATION.getCssStart(), ReportAttribute.HTML);
			report("Class documentation", null, true, true);
			for (int i = 0; i < lines.length; i++) {
				report(lines[i]);
			}
			report(CssType.getCssClosingTag(), ReportAttribute.HTML);
		}
		if (testDoc != null && (!testDoc.equals(""))) {
			String[] lines = testDoc.split("[\\r\\n]+");
			report(CssType.TEST_DOCUMENTATION.getCssStart(), ReportAttribute.HTML);
			report("Test documentation", null, true, true);
			for (int i = 0; i < lines.length; i++) {
				report(lines[i]);
			}
			report(CssType.getCssClosingTag(), ReportAttribute.HTML);
			// set the test javadoc into the test object
			if (test != null && test instanceof SystemTest) {
				((SystemTest) test).setTestDocumentation(testDoc);
			}

		}

		tname = TestNameServer.getInstance().getTestName(test);
		String userDoc = tname.getUserDocumentation();
		if (userDoc != null) {
			String[] lines = userDoc.split("[\\r\\n]+");
			report(CssType.USER_DOCUMENTATION.getCssStart(), ReportAttribute.HTML);
			report("User documentation", null, true, true);
			for (int i = 0; i < lines.length; i++) {
				report(lines[i]);
			}
			report(CssType.getCssClosingTag(), ReportAttribute.HTML);
		}

		if (testPresentationInRunner != null) {
			report(CssType.BREAD_CRUMBS.getCssStart(), ReportAttribute.HTML);
			report(ScenarioHelpers.getTestHierarchyInPresentableFormat(testPresentationInRunner)
					+ CssType.getCssClosingTag(), ReportAttribute.HTML);
		}

		endSection();
		report(CssType.getCssClosingTag(), ReportAttribute.HTML);
		addTimeStamp = addTimeStampOld;

	}

	public synchronized void aboutToChangeTo(Fixture fixture) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof FixtureListener) {
				FixtureListener fl = (FixtureListener) currentObject;
				try {
					fl.aboutToChangeTo(fixture);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to file aboutToChangeTo", ex);
				}
			}
		}
	}

	public synchronized void fixtureChanged(Fixture fixture) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof FixtureListener) {
				FixtureListener fl = (FixtureListener) currentObject;
				try {
					fl.fixtureChanged(fixture);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to file fixtureChanged", ex);
				}
			}
		}
	}

	public synchronized void startFixturring() {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof FixtureListener) {
				FixtureListener fl = (FixtureListener) currentObject;
				try {
					fl.startFixturring();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to file startFixturring", ex);
				}
			}
		}
	}

	public synchronized void endFixturring() {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof FixtureListener) {
				FixtureListener fl = (FixtureListener) currentObject;
				try {
					fl.endFixturring();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to file endFixturring", ex);
				}
			}
		}
	}

	public ArrayList<TestReporter> getAllReporters() {
		ArrayList<TestReporter> array = new ArrayList<TestReporter>();
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof TestReporter) {
				array.add((TestReporter) currentObject);
			}
		}
		return array;
	}

	@Override
	public void initReporters() {
		log.fine("RunnerListenersManager - initReporters ");
		parser.init();
		testIndex = new RepeatTestIndex();
		ArrayList<TestReporter> array = getAllReporters();
		for (int i = 0; i < array.size(); i++) {
			log.fine("RunnerListenersManager - initiating " + array.get(i));
			array.get(i).init();
		}
		testsCount = 0;
		RunnerStatePersistencyManager.getInstance().setLoadReporters(false);
	}

	public void setSilent(boolean status) {
		this.silent = status;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setTimeStamp(boolean enable) {
		addTimeStamp = enable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendReporter#reportHtml(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public synchronized void startSection() {
		if (isSilent()) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).startSection();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to startSection", ex);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendReporter#reportHtml(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public synchronized void endSection() {
		if (isSilent()) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).endSection();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to endSection", ex);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendReporter#saveFile(java.lang.String,
	 * java.io.InputStream)
	 */
	public synchronized void saveFile(String fileName, byte[] content) {
		if (isSilent()) {
			return;
		}
		try {

			File file = new File(getCurrentTestFolder(), fileName);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content);
			out.close();
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to save file", e);
		}
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
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).setData(data);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to setData", ex);
				}
			}
		}
	}

	public synchronized void sutChanged(String sutName) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof SutListener) {
				try {
					((SutListener) currentObject).sutChanged(sutName);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to sutChange", ex);
				}
			}
		}
	}

	public synchronized void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ScenarioListener) {
				try {
					((ScenarioListener) currentObject).scenarioChanged(current, changeType);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to scenarioChanged", ex);
				}
			}
		}
	}

	public synchronized void scenarioDirectoryChanged(File directory) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ScenarioListener) {
				try {
					((ScenarioListener) currentObject).scenarioDirectoryChanged(directory);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to scenarioDirectoryChanged", ex);
				}
			}
		}
	}

	public synchronized void startReport(String name, String parameters, String classDoc, String testDoc) {
		if (isSilent()) {
			return;
		}
		if (inTest) {
			endReport();
		}
		SystemTestCase stc = new InternalTest();
		stc.setName(name);
		setLastTestFail(false);
		stc.setPass(true);
		updateCurrentTest(stc);
		testsCount++;
		setSilent(false);

		TName tname = TestNameServer.getInstance().getTestName(stc);
		tname.setClassName(testClassName);
		tname.setParamsString(parameters);
		int count = testIndex.getTestIndex(tname.getFullName());

		TestInfo ti = new TestInfo();
		ti.className = tname.getClassName();
		ti.methodName = name;
		ti.meaningfulName = null;
		ti.comment = tname.getComment();
		ti.parameters = parameters;
		ti.count = count;
		ti.fullUuid = "";
		ti.classDoc = classDoc;
		ti.testDoc = testDoc;

		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if ((currentObject instanceof ExtendTestListener) && (currentObject instanceof TestReporter)) {
				((ExtendTestListener) currentObject).startTest(ti);
			}
		}

		addTestInfo(tname, null, stc, true, classDoc, testDoc, null);
		inTest = true;
		startTime = System.currentTimeMillis();

	}

	public synchronized void endReport(String steps, String failCause) {
		if (isSilent()) {
			return;
		}
		addEndTestInfo();
		if (steps != null) {
			((SystemTest) currentTest).addExecutedSteps(steps);
		}
		if (failCause != null) {
			((SystemTest) currentTest).addFailCause(failCause);
		}
		if (inTest) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if ((currentObject instanceof ExtendTestListener) && (currentObject instanceof TestReporter)) {
					try {
						((ExtendTestListener) currentObject).endTest(currentTest);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to endReport", ex);
					}
				}
			}
		}
		inTest = false;
	}

	public synchronized void endRun() {
		blockReporters = false;
		parser.closeAllContainers();
		ArrayList<Object> ll = new ArrayList<Object>(listeners);
		for (int i = 0; i < ll.size(); i++) {
			Object currentObject = ll.get(i);
			if (currentObject instanceof ExtendTestListener) {
				try {
					((ExtendTestListener) currentObject).endRun();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to endRun", ex);
				}
			}
		}
		flushReporters();
		if (JSystemProperties.getInstance().isExitOnRunEnd()) {
			System.exit(0);
		}
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
		if (inScenarioAsTest && place == Reporter.MainFrame) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendLevelTestReporter) {
					try {
						if(inLoop==0){
							((ExtendLevelTestReporter) currentObject).closeLevelsUpTo(lastTestLevelName, false);
						}
						((ExtendLevelTestReporter) currentObject).startLevel(level, Reporter.CurrentPlace);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				}
			}
		} else {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendLevelTestReporter) {
					try {
						((ExtendLevelTestReporter) currentObject).startLevel(level, place);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				}
			}
		}

	}

	public void stopLevel() throws IOException {
		if (isSilent()) {
			return;
		}
		if (buffering) {
			if (reportsBuffer == null) {
				reportsBuffer = new ArrayList<ReportElement>();
			}
			ReportElement currentReportElement = new ReportElement();
			currentReportElement.setOriginator(Thread.currentThread().getName());
			currentReportElement.setTime(System.currentTimeMillis());
			currentReportElement.setStopLevel(true);
			reportsBuffer.add(currentReportElement);
			return;
		}
		for (int index = 0; index < listeners.size(); index++) {
			Object currentObject = listeners.get(index);
			if (currentObject instanceof ExtendLevelTestReporter) {
				try {
					((ExtendLevelTestReporter) currentObject).stopLevel();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to report", ex);
				}
			}
		}
	}

	public synchronized void report(String title, String message, int status, boolean bold, boolean html, boolean step,
			boolean link, long time) {
		if (isSilent()) {
			return;
		}

		if (buffering && !step) {
			if (reportsBuffer == null) {
				reportsBuffer = new ArrayList<ReportElement>();
			}
			ReportElement newReportElementInstance = new ReportElement();
			newReportElementInstance.setTitle(title);
			newReportElementInstance.setMessage(message);
			newReportElementInstance.setStatus(status);
			newReportElementInstance.setBold(bold);
			newReportElementInstance.setHtml(html);
			newReportElementInstance.setStep(step);
			newReportElementInstance.setLink(link);
			newReportElementInstance.setOriginator(Thread.currentThread().getName());
			newReportElementInstance.setTime(time);
			reportsBuffer.add(newReportElementInstance);
			if (!printBufferdReportsInRunTime) {
				return;
			}
		}

		if (failToPass) {
			status = Reporter.PASS;
		}
		if (status == Reporter.FAIL && failToWarning) {
			status = Reporter.WARNING;
		}

		if(inScenarioAsTest){
			if (inNegativeTestState() && !title.equals(CommonResources.NEGATIVE_TEST_STRING) && status == Reporter.FAIL) {
				if (currentTest != null) {
					ScenarioHelpers.getRunnerTest(currentTest).setFailureOccurred(true);
				}
				if (!isError) {
					status = Reporter.PASS;
				}
			}
		}
		else{
			if (testMarkedNegativeTest && !title.equals(CommonResources.NEGATIVE_TEST_STRING) && status == Reporter.FAIL) {
				if (currentTest != null) {
					ScenarioHelpers.getRunnerTest(currentTest).setFailureOccurred(true);
				}
				if (!isError) {
					status = Reporter.PASS;
				}
			}
		}

		if (inKnownIssueState() && status == Reporter.FAIL) {
			status = Reporter.WARNING;
		}

		if (status == Reporter.FAIL) {
			setLastTestFail(true);
			if (systemTest != null) {
				systemTest.addFailCause(title);
				systemTest.setPass(false);
			}
		}
		if (message != null && message.equals("null")) {
			message = null;
		}
		if (status == Reporter.WARNING) {
			if (currentTest != null) {
				addWarning(currentTest);
			}
			if (systemTest != null) {
				systemTest.addFailCause(title);
			}
		}
		if (link) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendTestReporter) {
					try {
						((ExtendTestReporter) currentObject).report(title, message, status, false, false, true);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to addLink", ex);
					}
				}
			}
		} else if (html) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendTestReporter) {
					try {
						((ExtendTestReporter) currentObject).report(title, message, status, false, true, false);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to reportHtml", ex);
					}
				}
			}
		} else if (step) {
			report(title, null, Reporter.PASS, true, false, false, false, time);
			if (currentTest != null && currentTest instanceof SystemTest) {
				((SystemTest) currentTest).addExecutedSteps(title);
			}
		} else {
			String timeStamp = "";
			if (addTimeStamp) {
				if (date != null) {
					timeStamp = date + ": ";
				} else if (time > 0) {
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					timeStamp = DateUtils.getDate(time, sdf) + ": ";
				}
				title = timeStamp + title;

			}
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendTestReporter) {
					if (currentObject instanceof HtmlTestReporter) { // added to
																		// support
																		// CSS
																		// on
																		// time
																		// stamps
						((HtmlTestReporter) currentObject).setTimeStampToReplace(timeStamp);
					}
					try {
						((ExtendTestReporter) currentObject).report(title, message, status, bold, false, false);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				} else if (currentObject instanceof TestReporter) {
					try {
						((TestReporter) currentObject).report(title, message, status, bold);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				}
			}
		}
	}

	public void blockReporters(boolean block) {
		this.blockReporters = block;
	}

	public boolean getLastTestFailed() {
		return lastTestFail;
	}

	public boolean isPause() {
		return false;
	}

	public void addProperty(String key, String value) {
		String property = key + "=" + value;
		String title = "Added Property: " + property;

		if (StringUtils.hasNotAllowedSpecialCharacters(key) || StringUtils.hasNotAllowedSpecialCharacters(value)) {
			String title2 = "Warning: found unallowed characters from \"" + StringUtils.notAllowedCharacters + "\""
					+ " in property: " + property;
			report(title2, false);
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).addProperty(key, value);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to report", ex);
				}
			} else if (currentObject instanceof TestReporter) {
				try {
					((TestReporter) currentObject).report(title, null, true, false);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to report", ex);
				}
			}

		}

	}

	public int showConfirmDialog(String title, String message, int optionType, int messageType) {
		int result = 0;
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof InteractiveReporter) {
				InteractiveReporter fl = (InteractiveReporter) currentObject;
				try {
					result = fl.showConfirmDialog(title, message, optionType, messageType);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to run executionEnded", ex);
				}
			}
		}
		return result;
	}

	public void startLevel(String level, EnumReportLevel place) throws IOException {
		startLevel(level, place.value());
	}

	public void executionEnded(String scenarioName) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExecutionListener) {
				ExecutionListener fl = (ExecutionListener) currentObject;
				try {
					fl.executionEnded(scenarioName);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to run executionEnded", ex);
				}
			}
		}
	}

	public void remoteExit() {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExecutionListener) {
				ExecutionListener fl = (ExecutionListener) currentObject;
				try {
					fl.remoteExit();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to run remote exit", ex);
				}
			}
		}
	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExecutionListener) {
				ExecutionListener fl = (ExecutionListener) currentObject;
				try {
					fl.errorOccured(title, message, level);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to run errorOccured", ex);
				}
			}
		}
	}

	public void remotePause() {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExecutionListener) {
				ExecutionListener fl = (ExecutionListener) currentObject;
				try {
					fl.remotePause();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to run remote pause", ex);
				}
			}
		}
	}

	private void setLastTestFail(boolean status) {
		lastTestFail = status;
		if (status) {
			hadFailure = true;
		}
	}

	private boolean inKnownIssueState() {
		return (inScenarioAsTest && scenarioMarkedAsKnownIssue) || (!inScenarioAsTest && testMarkedAsKnownIssue);
	}

//	private boolean inNegativeTestState() {
//		return (inScenarioAsTest && scenarioMarkedAsNegativeTest) || (!inScenarioAsTest && testMarkedNegativeTest);
//	}
	
	private boolean inNegativeTestState() {
		return (scenarioMarkedAsNegativeTest) || (testMarkedNegativeTest);
	}

	@Override
	public void startContainer(JTestContainer container) {
		if (container instanceof Scenario && ScenarioHelpers.isScenarioAsTestAndNotRoot(container)) {
			inScenarioAsTest = true;
			// The following code should be enabled if saving all default
			// scenarios flags in all father scenarios will be stopped as needed
			// Scenario scenario = (Scenario)
			// ScenariosManager.getInstance().getCurrentScenario().getTestByFullId(container.getFullUUID());
			// Properties properties =
			// ScenarioHelpers.getAllTestPropertiesUpTo(scenario, null, false);
			// String scenarioMarkedAsKnownIssueStr =
			// properties.getProperty(RunningProperties.MARKED_AS_KNOWN_ISSUE);
			// String scenarioMarkedAsNegativeTestStr =
			// properties.getProperty(RunningProperties.MARKED_AS_NEGATIVE_TEST);
			//
			// if(scenarioMarkedAsKnownIssueStr != null){
			// scenarioMarkedAsKnownIssue =
			// Boolean.parseBoolean(scenarioMarkedAsKnownIssueStr);
			// }
			//
			// if(scenarioMarkedAsNegativeTestStr != null){
			// scenarioMarkedAsNegativeTest =
			// Boolean.parseBoolean(scenarioMarkedAsNegativeTestStr);
			// }

			scenarioMarkedAsKnownIssue = ScenarioHelpers.isMarkedAsKnownIssue(container.getFullUUID(), ScenariosManager
					.getInstance().getCurrentScenario().getName());
			scenarioMarkedAsNegativeTest = ScenarioHelpers.isMarkedAsNegativeTest(container.getFullUUID(),
					ScenariosManager.getInstance().getCurrentScenario().getName());

			String scenarioAsClassName = container.getName().replace('/', '.').replace('\\', '.');
			TestInfo ti = new TestInfo();
			ti.className = scenarioAsClassName;
			ti.basicName = StringUtils.getClassName(scenarioAsClassName);
			ti.methodName = null;
			ti.meaningfulName = null;
			ti.comment = container.getComment();
			ti.classDoc = container.getDocumentation();
			ti.fullUuid = container.getFullUUID();
			ti.count = testIndex.getTestIndex(scenarioAsClassName);
			ti.isHiddenInHTML = container.isHiddenInHTML();

			Test currentRunnerTest = lastTest;
			

			updateCurrentTest(new ScenarioAsTest());
			((SystemTest) currentTest).setName(scenarioAsClassName);
			((ScenarioAsTest) currentTest).setCurrentRunnerTest(currentRunnerTest);

			fireTestStart(ti, currentTest, blockReporters);
			boolean addTimeStampOld = addTimeStamp;
			addTimeStamp = false;
			report("Execute scenario test: " + scenarioAsClassName);
			addTimeStamp = addTimeStampOld;

			startTime = System.currentTimeMillis();

		} else if (!inScenarioAsTest) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendTestListener) {
					ExtendTestListener tl = (ExtendTestListener) currentObject;
					try {
						tl.startContainer(container);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to send startContainer notification to testlistener", ex);
					}
				}
			}
		}
	}

	@Override
	public void endContainer(JTestContainer container) {
		if (container instanceof Scenario && ((Scenario) container).isScenarioAsTest()) {
			inScenarioAsTest = false;
			lastTestLevelName = null;
			addEndTestInfo(endTime);
			fireEndTest(currentTest, blockReporters);
			inTest = false;
			updateCurrentTest(null);
			if (System.currentTimeMillis() - lastGC > 60000) {
				System.gc();
				lastGC = System.currentTimeMillis();
			}
		} else if (!inScenarioAsTest) {
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof ExtendTestListener) {
					ExtendTestListener tl = (ExtendTestListener) currentObject;
					try {
						tl.endContainer(container);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to send endContainer notification to testlistener", ex);
					}
				}
			}
		}

		setLastTestFail(false);
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		if(inScenarioAsTest) {
			inLoop++;
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof LevelHtmlTestReporter) {
					try {
						((ExtendLevelTestReporter) currentObject).startLevel("Loop Number: " + count + ", " + loop.getLoopParamName() +"="+ loop.getLoopValue(count), Reporter.CurrentPlace);
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				}
			}
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) currentObject;
				try {
					tl.startLoop(loop, count);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to send startLoop notification to testlistener", ex);
				}
			}
		}
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		if(inScenarioAsTest) {
			inLoop--;
			for (int i = 0; i < listeners.size(); i++) {
				Object currentObject = listeners.get(i);
				if (currentObject instanceof LevelHtmlTestReporter) {
					try {
						((ExtendLevelTestReporter) currentObject).stopLevel();
					} catch (Throwable ex) {
						log.log(Level.SEVERE, "Fail to report", ex);
					}
				}
			}
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestListener) {
				ExtendTestListener tl = (ExtendTestListener) currentObject;
				try {
					tl.endLoop(loop, count);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to send endLoop notification to test listener", ex);
				}
			}
		}
	}

	@Override
	public void closeAllLevels() throws IOException {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendLevelTestReporter) {
				try {
					if (lastTestLevelName == null && inLoop==0) {
						((ExtendLevelTestReporter) currentObject).closeAllLevels();
					} else if(inLoop==0){
						((ExtendLevelTestReporter) currentObject).closeLevelsUpTo(lastTestLevelName, false);
					}
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to close all levels", ex);
				}
			}
		}
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ScenarioListener) {
				try {
					((ScenarioListener) currentObject).scenarioDirtyStateChanged(s, isDirty);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to scenarioDirtyStateChanged", ex);
				}
			}
		}
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues, Parameter[] newValues) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ScenarioListener) {
				try {
					((ScenarioListener) currentObject).testParametersChanged(testIIUUD, oldValues, newValues);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to testParametersChanged", ex);
				}
			}
		}

	}

	@Override
	public void flushReporters() {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).flush();
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to flush reporter", ex);
				}
			}
		}
	}

	@Override
	public void setContainerProperties(int ancestorLevel, String key, String value) {
		for (int i = 0; i < listeners.size(); i++) {
			Object currentObject = listeners.get(i);
			if (currentObject instanceof ExtendTestReporter) {
				try {
					((ExtendTestReporter) currentObject).setContainerProperties(ancestorLevel, key, value);
				} catch (Throwable ex) {
					log.log(Level.SEVERE, "Fail to report", ex);
				}
			}
		}
	}
}

class InternalTest extends SystemTestCase {
	// used as the base for all internal tests
}
