package com.aqua.sanity;

import java.io.File;
import java.util.Properties;

import utils.HtmlHelper;
import utils.ScenarioUtils;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;
import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.sanity.analyzers.FileExistAnalyzer;

/**
 * A group of tests as part of the jsystem sanity.
 * This class contain tests to test the reporter functionalities.
 * @author guy.arieli
 *
 */
public class ReporterFunctionality extends JSysTestCaseOld {
	private String userChoose = "";
	private int expected =0;
	public int getExpected() {
		return expected;
	}
	/**
	 * expected value can be 0/1/0 computability to userChoose
	 */
	public void setExpected(int expected) {
		this.expected = expected;
	}


	public String getUserChoose() {
		return userChoose;
	}
	/**
	 * user choose can be yes/no/ok
	 */
	public void setUserChoose(String userChoose) {
		this.userChoose = userChoose;
	}


	public ReporterFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	
	/**
	 * Execute a test that generate 2 steps
	 * then analyze in the xml reporter that the 2 steps can be
	 * found.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.6 test report steps functionlity")
	public void testStepMessages() throws Exception{
		jsystem.launch();
		
		report.step("Execute the test with 2 steps");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWith2Steps", "ReporterTests", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("Check test test pass and the 2 step can be found in the xml report");
		jsystem.checkTestPass(1);
		jsystem.checkXmlTestAttribute(1, "steps", "This is the first step");
		jsystem.checkXmlTestAttribute(1, "steps", "This is the second step");
	}
	
	/**
	 * Execute 3 tests then check that 3 directories ware created:
	 * test_1, test_2 and test_3.
	 * also check that index.html exists.
	 * Then init the reporter and check only the folders are deleted
	 * then rerun the tests and check the folder recreated
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.4 Check that report directories are created")
	public void testTestDirectories() throws Exception{
		jsystem.launch();
		
		report.step("Execute 3 tests");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWith2Steps", "ReporterTests",3, true);
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("Check the directories were created");
		File logDir = new File(jsystem.getReportXmlFile()).getParentFile();
		assertTrue("Check that test_1 exist", (new File(logDir,"test_1")).exists());
		assertTrue("Check that test_2 exist", (new File(logDir,"test_2")).exists());
		assertTrue("Check that test_3 exist", (new File(logDir,"test_3")).exists());
		assertTrue("Check that summary.html exist", (new File(logDir,"summary.html")).exists());
		assertTrue("Check that index.html exist", (new File(logDir,"index.html")).exists());
		
		jsystem.initReporters();
		assertFalse("Check that test_1 don't exist", (new File(logDir,"test_1")).exists());
		assertFalse("Check that test_2 don't exist", (new File(logDir,"test_2")).exists());
		assertFalse("Check that test_3 don't exist", (new File(logDir,"test_3")).exists());
		assertTrue("Check that index.html exist", (new File(logDir,"index.html")).exists());

		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("Check the directories were created");
		assertTrue("Check that test_1 exist", (new File(logDir,"test_1")).exists());
		assertTrue("Check that test_2 exist", (new File(logDir,"test_2")).exists());
		assertTrue("Check that test_3 exist", (new File(logDir,"test_3")).exists());
		assertTrue("Check that summary.html exist", (new File(logDir,"summary.html")).exists());
		assertTrue("Check that index.html exist", (new File(logDir,"index.html")).exists());
	}
	/**
	 * Run 3 tests one with error (regular exception),
	 * one with failer (assertion) and one with fail report (no exception).
	 * Both should update the fail cause in the xml report
	 */
	@TestProperties(name = "5.2.8.2.2-4 test different kind of failure types")
	public void testExecuteOfFailers() throws Exception{
		jsystem.launch();
		
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithError", "ReporterTests", true);
		jsystem.addTest("testWithFailer", "ReporterTests", true);
		jsystem.addTest("testWithFailNoException", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		jsystem.checkXmlTestAttribute(1, "failCause", "This is the exception error");
		jsystem.checkXmlTestAttribute(2, "failCause", "should be true");
		jsystem.checkXmlTestAttribute(3, "failCause", "Fail report");
		
		jsystem.checkNumberOfTestsPass(0);
	}
	/**
	 * Check that fail to pass block fail reports, and
	 * that it don't propegate to other tests in the scenario.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.1 check a report that passes")
	public void testFailToPass() throws Exception{
		jsystem.launch();
		
		report.step("execute a test with setFailToPass(true) and fail report");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testFailToPass", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test pass");
		jsystem.checkNumberOfTestsPass(1);
		
		jsystem.initReporters();
		
		report.step("Add aditional test that should fail");
		jsystem.addTest("testWithFailNoException", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkNumberOfTestExecuted(2);
	}
	
	/**
	 * Execute a test that should end with warning
	 * Check that the reporting system identify it as warning
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.1 & 5 check a report that passes and mixed report")
	public void testWithWarningReport() throws Exception{
		jsystem.launch();
		
		report.step("execute a test with warning message");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithWarning", "ReporterTests", true);

		jsystem.play();
		jsystem.waitForRunEnd();
		report.step("check that it end as warning");
		jsystem.checkXmlTestAttribute(1, "failCause", "warning title");
		jsystem.checkXmlTestAttribute(1, "status", "warning"); //"ignore" ==> "warning"
		jsystem.checkNumberOfTestsPass(0);
	}
	
	/**
	 * Check that fail to pass block fail reports, and
	 * that it don't propegate to other tests in the scenario.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.9 check a report with warning messages")
	public void testFailToWarning() throws Exception{
		jsystem.launch();
		
		report.step("execute a test with setFailToWarning(true) and fail report");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testFailToWarning", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test end with warning");
		jsystem.checkXmlTestAttribute(1, "status", "warning"); //"ignure" --> "warning"
		
		jsystem.initReporters();
		
		report.step("Add aditional test that should fail");
		jsystem.addTest("testWithFailNoException", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		jsystem.checkNumberOfTestsPass(0);
		jsystem.checkXmlTestAttribute(1, "status", "warning"); //"ignure" --> "warning"
		jsystem.checkXmlTestAttribute(2, "status", "false");
	}
	
	/**
	 * Check that silent block fail reports, and
	 * that it don't propegate to other tests in the scenario.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.10 check a report with silent messages")
	public void testSetSilent() throws Exception{
		jsystem.launch();
		
		report.step("execute a test with setSilent(true) and fail report");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testSetSilent", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test end successful");
		jsystem.checkNumberOfTestsPass(1);
		
		jsystem.initReporters();
		
		report.step("Add aditional test that should fail");
		jsystem.addTest("testWithFailNoException", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkTestPass(1);
		jsystem.checkXmlTestAttribute(2, "status", "false");
		
	}
	
	/**
	 * Check that the method getCurrentTestFolder works.
	 * Also check that it works after init of the reports.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.5 check a report getCurrentTestFolder method")
	public void testGetCurrentTestFolder() throws Exception{
		jsystem.launch();
		jsystem.initReporters();
		
		report.step("execute test that get the current test folder");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testGetCurrentTestFolder", "ReporterTests", true);
		
		jsystem.play(true);
		sleep(3000);//give the reporter thread time to update xml
		report.step("check that the test end successful and the report step was added");
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkXmlTestAttribute(1, "steps", "log" + File.separator + "current" + File.separator + "test_1");
		
		jsystem.play(true);
		
		sleep(3000);//give the reporter thread time to update xml
		report.step("check that the test end successful and now the current dir is test_2");
		jsystem.checkNumberOfTestsPass(2);
		jsystem.checkXmlTestAttribute(2, "steps", "log" + File.separator + "current" + File.separator + "test_2");
		
		jsystem.initReporters();
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test end successful and now the current dir returned to test_1");
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkXmlTestAttribute(1, "steps", "log" + File.separator + "current" + File.separator + "test_1");
		
	}
	/**
	 * Execute a test that save a file to the report system and
	 * check it is exist in the test folder
	 */
	@TestProperties(name = "5.2.8.2.6 check a file is save in the report log")
	public void testSaveFile() throws Exception{
		jsystem.launch();
		
		report.step("save a file and check it exist");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testSaveFile", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test end successful");
		jsystem.checkNumberOfTestsPass(1);
		
	}
	/**
	 * Test the internal tests feature.
	 * Execute a test that execute 10 internal tests
	 * analyze the xml report to see all the test were
	 * executed successfuly.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.7 check report messages from internal tests")
	public void testInternalTest() throws Exception{
		jsystem.launch();
		
		report.step("Run the internal tests");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testInternalTest", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test end successful");
		/*
		 * Check that 10 tests passed + the base test
		 */
		jsystem.checkNumberOfTestsPass(11);
		for(int i = 0; i < 10; i++){
			jsystem.checkXmlTestAttribute(i+2, "steps", "Step for test: " + (i+1));
		}
	}
	
	/**
	 * Add link to excel file feature.
	 * Execute a test that execute 10 internal tests
	 * analyze the xml report to see all the test were
	 * executed successfuly.
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.8.2.8 add link to excel sheet in the report")
	public void testAddLinkToExcel() throws Exception{
		jsystem.launch();
		
		report.step("Run the internal tests");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testExcle", "ReporterTests", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("check that the test ended successfully");
		/*
		 * Check that 10 tests passed + the base test
		 */
		jsystem.checkNumberOfTestsPass(1);
		
		File excelFile = new File(jsystem.getUserDir() +File.separator + "log"+File.separator+"current" + File.separator +"test_1", "MyExcel.xls");
		report.report("Checking file at " + excelFile.getAbsolutePath());		
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new FileExistAnalyzer(excelFile));
		String runnerFolder = jsystem.getUserDir();
		String logFolder = jsystem.getJSystemProperty(FrameworkOptions.LOG_FOLDER);
		String runnerLogFolder = runnerFolder + File.separator + logFolder;
		report.report("Checking folder "+runnerLogFolder);
		String linkFileName = HtmlHelper.findTestLinkFile("ReporterTests.testExcle", runnerLogFolder);
		report.report("Found Link "+linkFileName);
		File reportWithLink = new File(linkFileName);
		analyzer.setTestAgainstObject(FileUtils.read(reportWithLink));
		analyzer.analyze(new FindText("<a href=\"test_1/MyExcel.xls\" TARGET=\"testFrame\""));
	}

	/**
	 * Tests the copyAndAddLink function of the ReporterHelper.
	 */
	@TestProperties(name = "Test that checks adding link property to the ")
	public void testAddLinkProperty() throws Exception{
		jsystem.launch();
		report.step("Run the internal tests");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAddLinkProperty", "ReporterTests", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		report.step("check that the test end successful");
		jsystem.checkNumberOfTestsPass(1);
		File file = new File(jsystem.getUserDir() +File.separator + 
				             jsystem.getJSystemProperty(FrameworkOptions.LOG_FOLDER) + 
				             File.separator+"current" + 
				             File.separator +"test_1", "MyFile.txt");
		report.report("Checking file at " + file.getAbsolutePath());		
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new FileExistAnalyzer(file));
		Properties props = new Properties();
		props.put("linkToFile","<A href=\"test_1/MyFile.txt\">MyFile.txt</A>");
		props.put("linkToOne","<A href=\"http://www.one.co.il\">www.one.co.il</A>");
		jsystem.checkTestProperties(0, props);
	}

	/**
	 * Tests the copyAndAddLink function of the ReporterHelper.
	 */
	@TestProperties(name = "link to file using the ReporterHelper")
	public void testAddLinkToFileUsingTheReportHelper() throws Exception{
		jsystem.launch();
		report.step("Run the internal tests");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAddFileUsingReporterHelper", "ReporterTests", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		report.step("check that the test end successful");
		jsystem.checkNumberOfTestsPass(1);
		File file = new File(jsystem.getUserDir() +File.separator + "log"+File.separator+"current" + File.separator +"test_1", "MyFile.txt");
		report.report("Checking file at " + file.getAbsolutePath());		
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new FileExistAnalyzer(file));
		
		String runnerFolder = jsystem.getUserDir();
		String logFolder = jsystem.getJSystemProperty(FrameworkOptions.LOG_FOLDER);
		String runnerLogFolder = runnerFolder + File.separator + logFolder;
		report.report("Checking folder "+runnerLogFolder);
		String linkFileName = HtmlHelper.findTestLinkFile("ReporterTests.testAddFileUsingReporterHelper", runnerLogFolder);
		report.report("Found Link "+linkFileName);
		File reportWithLink = new File(linkFileName);
		
		analyzer.setTestAgainstObject(FileUtils.read(reportWithLink));
		analyzer.analyze(new FindText("<a href=\"test_1/MyFile.txt\" TARGET=\"testFrame\""));
	}

	/**
	 * This test check that the Init report clean the main xml file
	 * The test creates a scenarion and run it, this is done
	 * to make sure that the report is not empty.
	 * The test than activates the init report and verify that the
	 * report is empty.
	 */
	@TestProperties(name = "5.2.8.1 test init report")
	public void testInitReport() throws Exception {
		jsystem.launch();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testShouldPass","GenericBasic",10, true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(10);
		jsystem.checkNumberOfTestsPass(10);
		
		try{
		jsystem.initReporters();
		}
		catch (Exception e){
		 throw new Exception(e.getMessage());	
		}
		
		sleep(2000);
		if (jsystem.checkReportIsEmpty()){
			report.step("Init report succeeded ,the log report.0.xml is empty from tests");
		}else{
			report.step("Init report failed");
		}
	}
	public void TestOldZipOperation() throws Exception{
		throw new Exception();
	}
	/**
	 * Execute a test that generate confirm dialog "Continue test?" 
	 * from all possible scenario and check the user options  
	 * 
	 * @params.include userChoose,expected
	 */
	public void testWaitForConfirmDialog() throws Exception {
		String tab = "General";
		jsystem.launch();
		report.step("Run the confirm test");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testConfirmTest", "ReporterTests", true);
		jsystem.setTestParameter(1, tab, "Title", "Confirm Dialog", false);
		jsystem.setTestParameter(1, tab, "PressOn", userChoose, false);
		jsystem.setTestParameter(1, tab, "Expected", Integer.toString(expected), false);
		jsystem.play();
		sleep(5000);
		jsystem.waitForConfirmDialog("Confirm Dialog", expected);
		jsystem.waitForRunEnd();
	}
	
}
