/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import java.io.Serializable;
import java.util.HashMap;

import jsystem.extensions.handlers.UIEventHandler;
import jsystem.extensions.handlers.ValidationHandler;
import jsystem.framework.RunProperties;
import jsystem.framework.ShutdownManager;
import jsystem.framework.analyzer.AnalyzerImpl;
import jsystem.framework.common.CommonResources;
import jsystem.framework.monitor.MonitorsManager;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ValidationError;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.framework.system.SystemObjectManager;

/**
 * SystemTestCase is the best starting point to learn the services available in
 * JSystem. Every test that uses the JSystem framework should extend
 * SystemTestCase. SystemTestCase extends JUnit TestCase, and can be executed
 * from any JUnit runner. The following services available be using this class:
 * 
 * System object - Enable you to manage the interfaces to your system in one
 * easy to use object. You can use the system service to initialize and manage
 * the system objects in your setup.
 * <p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 * public void setUp() throws Exception {
 * 	device = (Device) system.getSystemObject(&quot;device&quot;);
 * }
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Reporting - enable extendable detail reporting service. This service enable
 * to log events from your tests and interfaces. A hierarchal HTML view of the
 * result is available. You can use it to track complex functional/system tests.
 * You can extend it by writing you own reporting layer. The report field can be
 * used to port log messages.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * report.step(&quot;The configuration part of the test&quot;);
 * report.report(&quot;The report title&quot;, true);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Fixture management - enable you to define a tree of fixtures, bind tests to
 * the fixtures and automaticly execute and navigate the fixture tree. To assign
 * fixture to a test you should use the <code>setFixture<code> method.
 * <p> <blockquote><pre>
 * public MyTest() {
 * 	setFixture(MyFixture.class);
 * 	setTearDownFixture(RootFixture.class);
 * }
 * </pre></blockquote>
 * <p>
 * SUT (System Under Test) independent - a layer that enable a test to run on
 * different setups. The <code>system</code> service using the sut service to
 * create the system objects. The test can use the <code>sut</code> service to
 * access the setup independent definitions file.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * sut.getValue(&quot;/sut/device/host/text()&quot;);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * 
 * @see SystemObjectManager
 * @see jsystem.framework.system.SystemObject
 * @see Reporter
 * @see Sut
 * @author Guy Arieli
 */
public abstract class SystemTestCase extends TestCase implements SystemTest, UIEventHandler, ValidationHandler, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Analyze object can be used to run analyzers that don't require
	 * SystemObject
	 */
	public static AnalyzerImpl analyzer = null;

	static {
		ShutdownManager.init();
		analyzer = new AnalyzerImpl();
		analyzer.setTestAgainstObject("");
	}

	private SystemTestCaseImpl testCaseImpl = null;

	/**
	 * Use it to log events from your test.
	 */
	public static Reporter report = ListenerstManager.getInstance();

	/**
	 * Use it to get information about the setup/system you are testing.
	 * 
	 * @deprecated use sut() method
	 */
	public static Sut sut = SutFactory.getInstance().getSutInstance();

	/**
	 * Use it to access the interfaces of your tested system.
	 */
	public SystemObjectManager system = SystemManagerImpl.getInstance();

	/**
	 * The monitor manager object used to control monitor (processes that run
	 * during the test)
	 */
	public MonitorsManager monitors = MonitorsManager.getInstance();

	/**
	 * Properties service that can be used to save properties and share it with
	 * other tests. Properties that are saved in one test can be read in other
	 * tests as long as they are in the same run.
	 */
	public RunProperties runProperties = RunProperties.getInstance();

	/**
	 * No-arg constructor to enable serialization. This method is not intended
	 * to be used by mere mortals without calling setName().
	 */
	public SystemTestCase() {
		super();
		testCaseImpl = new SystemTestCaseImpl();
	}

	/**
	 * Constructs a test case with the given name.
	 */
	public SystemTestCase(String name) {
		super(name);
		testCaseImpl = new SystemTestCaseImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#run(junit.framework.TestResult)
	 */
	public void run(TestResult result) {
		testCaseImpl.testResult = result;
		addToTestResult();
		result.run(this);
	}

	/**
	 * Runs the bare test sequence.
	 * 
	 * @exception Throwable
	 *                if any exception is thrown
	 */
	public void runBare() throws Throwable {
		try { // test setup and main run
			jsystemTestPreExecution(this);
			report.report("setUp execution", null, true, true);
			Throwable exceptionInSetupOrTest = null;
			try {
				setUp();
				runTest();
				if (ListenerstManager.getInstance().getLastTestFailed()) {
					setPass(false);
					testCaseImpl.testResult.addFailure(this, new AssertionFailedError("Fail report was submitted"));
				}
			} catch (Throwable t) {
				exceptionInSetupOrTest = t;
				if (t instanceof AssertionFailedError) {
					testCaseImpl.testResult.addFailure(this, (AssertionFailedError) t);
				} else {
					testCaseImpl.testResult.addError(this, t);
				}
			} finally { // start tearDown
				cancelException();
				report.closeAllLevels();
				report.report(CommonResources.TEARDOWN_STRING, null, true, true);
				try {
					tearDown();
				} catch (Throwable tt) {
					/*
					 * See that the if an exception was thrown from the setup or
					 * test will be display regardless of the exception from the
					 * tearDown
					 */
					if (exceptionInSetupOrTest != null) {
						report.report("Exception was thrown", exceptionInSetupOrTest);
					}
					throw tt;
				}
			}
		} catch (Throwable throwable) {
			cancelException();
			setPass(false);
			testCaseImpl.addError(this, throwable);
		} finally {
			jsystemTestPostExecution(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#sut()
	 */
	public Sut sut() {
		return testCaseImpl.sut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#getFixture()
	 */
	public Class<?> getFixture() {
		return testCaseImpl.getFixture();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#setFixture(java.lang.Class)
	 */
	public void setFixture(Class<?> fixture) {
		testCaseImpl.setFixture(fixture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#getFixtureName()
	 */
	public String getFixtureName() {
		return testCaseImpl.getFixtureName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#setFixtureName(java.lang.String)
	 */
	public void setFixtureName(String fixtureClassName) {
		testCaseImpl.setFixtureName(fixtureClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#getTearDownFixture()
	 */
	public Class<?> getTearDownFixture() {
		return testCaseImpl.getTearDownFixture();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#setTearDownFixture(java.lang.Class)
	 */
	public void setTearDownFixture(Class<?> tearDownFixture) {
		testCaseImpl.setTearDownFixture(tearDownFixture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#setPass(boolean)
	 */
	public void setPass(boolean isPass) {
		testCaseImpl.setPass(isPass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#isPass()
	 */
	public boolean isPass() {
		return testCaseImpl.isPass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#isPassAccordingToFlags()
	 */
	public boolean isPassAccordingToFlags() {
		return testCaseImpl.isPassAccordingToFlags();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.SystemTest#sleep(long)
	 */
	public void sleep(long time) {
		testCaseImpl.sleep(time);
	}

	public TestResult getTestResult() {
		if (testCaseImpl != null) {
			return testCaseImpl.testResult;
		}
		return null;
	}

	public void handleUIEvent(HashMap<String, Parameter> map, String methodName) throws Exception {
	}

	public ValidationError[] validate(HashMap<String, Parameter> map, String methodName) throws Exception {
		return null;
	}

	/**
	 * signal Listeners manager to cancel exception if graceful stop was raised
	 * 
	 */
	private void cancelException() {
		testCaseImpl.cancelException();
	}

	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public String getMethodName() {
		return getName();
	}

	/***************************************************************************************/

	public void addToTestResult() {
		testCaseImpl.addToTestResult();
	}

	public void addExecutedSteps(String step) {
		testCaseImpl.addExecutedSteps(step);
	}

	public void addFailCause(String failCause) {
		testCaseImpl.addFailCause(failCause);
	}

	public void clearDocumentation() {
		testCaseImpl.clearDocumentation();
	}

	public void clearFailCause() {
		testCaseImpl.clearFailCause();
	}

	public void clearSteps() {
		testCaseImpl.clearSteps();
	}

	public String getExecutedSteps() {
		return testCaseImpl.getExecutedSteps();
	}

	public String getFailCause() {
		return testCaseImpl.getFailCause();
	}

	public String getTestDocumentation() {
		return testCaseImpl.getTestDocumentation();
	}

	public int hashCode() {
		return testCaseImpl.hashCode();
	}

	public void setTestDocumentation(String testDocumantation) {
		testCaseImpl.setTestDocumentation(testDocumantation);
	}

	public String toString() {
		return testCaseImpl.toString();
	}

	public String getFullUUID() {
		return testCaseImpl.getFullUUID();
	}

	public void setFullUUID(String fullUUID) {
		testCaseImpl.setFullUUID(fullUUID);
	}

	public void jsystemTestPreExecution(SystemTest test) throws Throwable {
		testCaseImpl.jsystemTestPreExecution(test);
	}

	public void jsystemTestPostExecution(Test test) {
		testCaseImpl.jsystemTestPostExecution(this);
	}

	public void initFlags() {
		testCaseImpl.initFlags();
	}

}
