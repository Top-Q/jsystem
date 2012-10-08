/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.TestFreezeException;
import jsystem.framework.common.CommonResources;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.monitor.MonitorsManager;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.framework.system.SystemObjectManager;

public class SystemTestCaseImpl {

	/**
	 * Analyze object can be used to run analyzers that don't require
	 * SystemObject
	 */
	protected static Logger log = Logger.getLogger(SystemTestCase4.class.getName());
	private Class<?> fixture = RootFixture.class;
	private Class<?> tearDownFixture = null;
	/**
	 * Used to hold the result in the junit format.
	 */
	protected TestResult testResult = null;
	protected boolean isPass = true;
	protected String testDocumantation = "";
	protected StringBuffer executedSteps = null;
	protected StringBuffer failCause = null;
	/**
	 * Use it to log events from your test.
	 */
	public static Reporter report = ListenerstManager.getInstance();
	/**
	 * Use it to get information about the setup/system you are testing.
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
	 * The Full unique ID of the test (includes id of all scenarios on path from root)
	 */
	private String fullUUID = "";
	
	private boolean isKnownIssue=false, isNegativeTest=false;
	private boolean flagsWereInit = false;
	
	public SystemTestCaseImpl() {
		
	}
	
	public Sut sut() {
		return SutFactory.getInstance().getSutInstance();
	}

	public Class<?> getFixture() {
		return fixture;
	}

	public void setFixture(Class<?> fixture) {
		this.fixture = fixture;
		if (getTearDownFixture() == null) {
			setTearDownFixture(fixture);
		}
	}

	public String getFixtureName() {
		return this.fixture.getClass().getName();
	}

	public void setFixtureName(String fixtureClassName) {
		if (FixtureManager.getInstance().getFixture(fixtureClassName) != null) {
			setFixture(FixtureManager.getInstance().getFixture(fixtureClassName).getClass());
		}
	}

	public Class<?> getTearDownFixture() {
		return tearDownFixture;
	}

	public void setTearDownFixture(Class<?> tearDownFixture) {
		this.tearDownFixture = tearDownFixture;
	}

	public void addToTestResult() {
		testResult.removeListener(ListenerstManager.getInstance());
		testResult.addListener(ListenerstManager.getInstance());
	}

	/**
	 * pass is set only in two places:
	 * <li>when test initializes it is set to true
	 * <li>when test reports failure it is set to false
	 * <br><br>
	 * Note that in case test pass the reporter will not set this flag. 
	 *  
	 * @param isPass
	 */
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	/* (non-Javadoc)
	 * @see junit.framework.SystemTest#isPass
	 */
	public boolean isPass() {
		boolean testResultPass = testResult == null? true : (testResult.fErrors.size() == 0 && testResult.wasSuccessful());
		boolean pass = isPass && testResultPass;
		return pass;
	}

	/* (non-Javadoc)
	 * @see junit.framework.SystemTest#isPassAccordingToFlags()
	 */
	public boolean isPassAccordingToFlags() {
		boolean pass = isPass();
		if (!flagsWereInit){
			initFlags();
			setPass(pass);
		}
		
		if (isNegativeTest){
			pass = !pass;
		}
		return pass;
	}

	public void sleep(long time) {
		report.report("Sleep " + (time / 1000) + " sec.");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			return;
		}
	}
	
	public String getExecutedSteps() {
		if (executedSteps == null) {
			return "";
		}
		return executedSteps.toString();
	}

	public void addExecutedSteps(String step) {
		if (executedSteps == null) {
			executedSteps = new StringBuffer();
		}
		try{
			executedSteps.append(step);
			executedSteps.append("\n");
		}catch (OutOfMemoryError e) {
			log.log(Level.SEVERE, "Steps buffer was Exceeded. this is probably done due to misusage of" +
					" the step mechanism.\n" +
					"for Bold reporting please use: report.report(\"Your title\",ReportAttribute.BOLD); instead ");
		}
	
	}

	public String getFailCause() {
		if (failCause == null) {
			return "";
		}
		return failCause.toString();
	}

	public void addFailCause(String failCause) {
		if (this.failCause == null) {
			this.failCause = new StringBuffer();
		}
		this.failCause.append(failCause);
		this.failCause.append("\n");
	}

	public void clearFailCause() {
		failCause = null;
	}

	public void clearDocumentation() {
		testDocumantation = null;
	}

	public void clearSteps() {
		executedSteps = null;
	}

	public String getTestDocumentation() {
		return testDocumantation;
	}

	public void setTestDocumentation(String testDocumantation) {
		this.testDocumantation = testDocumantation;
	}

	protected void handleFreezeException(TestFreezeException ex) {
	
		report.report("TestFreezeException: " + ex.getMessage(), ex);
		report.showConfirmDialog("Freeze Exception",
				"A TestFreezeException was thrown\nmessage: "
						+ ex.getMessage() + "\nPress OK to continue",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
	
	}

	/**
	 * signal Listeners manager to cancel exception if graceful stop was raised
	 *
	 */
	protected void cancelException() {
		if (report instanceof ListenerstManager){
			((ListenerstManager)report).cancelExceptionThrowing();
		}
	}

	/**
	 * checks if the graceful stop flag was raised (stop was pressed)
	 */
	protected boolean isGracefulStop() {
		if (report instanceof ListenerstManager){
			return ((ListenerstManager)report).isGracefulStop();
		}
		return false;
	}

	/**
	 * the test Full Unique ID is used to identify a test instance
	 * @return	the full unique id path from root to test
	 */
	public String getFullUUID() {
		// check if value was already initiated
		if (fullUUID.equals("")){
			// get parent scenario full unique id first
			String parentFullUUID = System.getProperty(RunningProperties.UUID_PARENT_TAG);
			if (parentFullUUID == null){
				return null;
			}
			while (parentFullUUID.startsWith(".")){
				parentFullUUID = parentFullUUID.substring(1);
			}
			// get test unique id
			String uuid = System.getProperty(RunningProperties.UUID_TAG);
			parentFullUUID = parentFullUUID.equals("")? uuid : parentFullUUID+"."+uuid;
			this.fullUUID = parentFullUUID;
		}
		return fullUUID;
	}

	public void setFullUUID(String fullUUID) {
		this.fullUUID = fullUUID;
	}
	
	public void initFlags() {

		// if test is ran not from the runner\ANT (eclipse for example), the flags are currently not supported
		if (!JSystemProperties.getInstance().isExecutedFromIDE()) {
			Properties properties = ScenarioHelpers.getAllTestPropertiesFromAllScenarios(getFullUUID(), false);
			
			String MarkAsKnownIssueStr = properties.getProperty(RunningProperties.MARKED_AS_KNOWN_ISSUE);
			if(MarkAsKnownIssueStr != null){
				isKnownIssue = Boolean.parseBoolean(MarkAsKnownIssueStr);
			}
			
			String MarkAsNegativeTestStr = properties.getProperty(RunningProperties.MARKED_AS_NEGATIVE_TEST);
			if(MarkAsNegativeTestStr != null){
				isNegativeTest = Boolean.parseBoolean(MarkAsNegativeTestStr);
			}
		}
		flagsWereInit = true;
		setPass(true);
		
	}
		
	public void jsystemTestPreExecution(SystemTest test) throws Throwable{

		testDocumantation = "";
		executedSteps = null;
		failCause = null;
		
		initFlags();
		
		/*
		 * Init the test parameters see ParametersManager for for information
		 */
		try {
			ParametersManager.initTestParameters(test);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to init parameters", e);
		}
		log.info("Running test: " + test.getName());
		try { // setup fixtures
			FixtureManager.getInstance().goTo(getFixture().getName());
		} catch (Throwable throwable) {
			report.report("Failed in pre execution",throwable);
			cancelException();
			setPass(false);
			throw throwable;
		}
	}
	
	public void jsystemTestPostExecution(Test test) {
		if (test instanceof SystemTest) {
			/*
			 * Retrieve the test output parameters see ParametersManager for for
			 * information
			 */
			try {

				ParametersManager.retrieveTestParameters((SystemTest)test);

			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to retrieve parameters", e);
			}
		}

		String exceptionMessage ="No Error message";
		
		boolean pass = isPass && testResult.wasSuccessful();
		setPass(pass);
		if (pass && isNegativeTest){
			pass = false;
			exceptionMessage = "Negative test - should have failed";
		}
		if (isKnownIssue && !pass){
			pass = true;
		}
		
		cancelException();
		if (!pass) {
			if (testResult.errorCount() > 0) {
				exceptionMessage = testResult.errors().nextElement().thrownException().getMessage();
			} else if (testResult.failureCount() > 0){
				exceptionMessage = testResult.failures().nextElement().thrownException().getMessage();
			}
			String show = null;
			show = JSystemProperties.getInstance().getPreference(
					FrameworkOptions.FREEZE_ON_FAIL);
			if ("true".equals(show)){
				ListenerstManager.getInstance()
						.showConfirmDialog(
								CommonResources.FREEZE_ON_FAIL_TITLE,
								"A TestFreezeException was thrown\nmessage: "
										+ exceptionMessage
										+ "\nPress OK to continue, Cancel to stop execution.",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.ERROR_MESSAGE);
			}

			try {
				String tearDownName = null;
				Class<?> c = getTearDownFixture();
				if (c != null) {
					tearDownName = c.getName();
				}
				FixtureManager.getInstance().failTo(tearDownName);
			} catch (Throwable tt) {
				log.log(Level.WARNING, "tearDown fixture failed", tt);
				testResult.addError(test, tt);
			}
		}
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
	
	public TestResult getTestResult(){
		return testResult;
	}
	
	public void addError(Test test, Throwable t){
		testResult.addError(test, t);
	}
}