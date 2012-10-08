package com.aqua.sanity;

import java.io.File;
import java.util.Random;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import utils.ScenarioModelUtils;
import utils.ScenarioUtils;
import analyzers.StringCompareAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystem;

/**
 * General test as describe in 'TAS Sanity - TRD' document This class covers
 * section 4.2.1 General and 4.2.2 Scenario management.
 * 
 * @author guy.arieli
 * 
 */
public class ScenarioManagement extends JSysTestCaseOld {

	String scenarioName = "scenario1";

	int numberOfTests = 1;

	private int numOfTestsToAdd = 200;

	public ScenarioManagement() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	/**
	 * 1. Launch the jsystem 2. Add a test to the default scenario 3. Execute
	 * the test 4. Analyze to see it pass
	 * 
	 * @throws Exception
	 */
	public void testBasicTestExecution() throws Exception {
		applicationClient.launch();
		report.step("clean the default scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioClient.getCurrentScenario());
		report.step("add a test");
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		applicationClient.play();// blocking play
		report.step("analyze the number of tests run");
		analyzer.setTestAgainstObject(1);
		int numOfTestsInScenario = scenarioClient.getNumOfMappedTestsInScenario();
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, numOfTestsInScenario, 0));
	}

	/**
	 * 
	 * @param sign
	 * @throws Exception
	 */
	public void createScenarioWithSpecialSign(String sign) throws Exception {
		report.step("create a scenario which its name contain the sign: " + sign);
		jsystem.createScenario("scenarioWith" + sign);
	}

	public void checkScenarioWithSignWasCreated(String sign) throws Exception {
		if (!jsystem.checkScenarioExist("scenarioWith" + sign)) {
			report.report("Cannot create Scenario which contain the sign " + sign + " in its name", false);
		}
	}

	/**
	 * Create scenario close and open jsystem and check that the name of the
	 * scenario is the same like it was when we closed the jsystem 1. Creat
	 * scenario (open.close.scenario). 3. close jsystem. 4.open jsystem. 5.
	 * Check that the jsystem open with the "open.close.scenario".
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "test runner is open with scenario from last run")
	public void testCreateScenarioCloseOpenJsytem() throws Exception {
		int expectednumOfTests = 2;
		String expectedScenarioName = "";
		report.step("launch the system and create a scenario named " + scenarioName);
		applicationClient.launch();
		setScenarioName("open.close.jsystem.scenario");
		expectedScenarioName = this.scenarioName;
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		// check scenario was really created in file system
		if (!jsystem.checkScenarioExist(scenarioName)) {// CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
		report.step("add test to scenario");
		scenarioClient.addTest("testShouldPass", "GenericBasic", expectednumOfTests);
		report.report("Creating scenario with " + expectednumOfTests + " tests succeeded");
		applicationClient.saveScenario();
		// close the running runner
		report.report("killing the evironment");
		envController.kill();
		// create new environment
		report.report("creating new evironment");

		super.setUp();
		report.report("launch the new jsystem environment");
		applicationClient.launch();

		// get the current active scenario and check if his the expected one
		String currentScenario = scenarioClient.getCurrentScenario();
		report.report("current scenario is: " + currentScenario);
		// truncate the scenario name
		currentScenario = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(currentScenario);
		report.report("current scenario relative to scenariosFolder is: " + currentScenario);

		report.report("current scenario is: " + currentScenario);
		report.report("check that current Scenario name equals the expected one");
		String currentString = currentScenario;
		analyzer.setTestAgainstObject(expectedScenarioName);
		analyzer.analyze(new StringCompareAnalyzer(currentString));

		// check if the active scenario exists with expected number Of Tests
		int numOfTest = jsystem.getNumOfTestsInScenario();
		report.report("checking num of tests under scenario is as expected");
		analyzer.setTestAgainstObject(expectednumOfTests);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, numOfTest, 0));
	}

	/**
	 * 1. Create scenario with special sign, using
	 * "createScenarioWithSpecialSign()" method 2. Check that the created
	 * scenario is exist, using "checkScenarioWithSignWasCreated()" method 3.
	 * return sections 1 and 2 with different special signs
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.1 Create scenario that it's name contains special characters")
	public void testCreateScenarioWithSpecialChar() throws Exception {
		jsystem.launch();
		createScenarioWithSpecialSign("!");
		checkScenarioWithSignWasCreated("!");
		createScenarioWithSpecialSign("@");
		checkScenarioWithSignWasCreated("@");
		createScenarioWithSpecialSign("#");
		checkScenarioWithSignWasCreated("#");
		createScenarioWithSpecialSign("$");
		checkScenarioWithSignWasCreated("$");
		createScenarioWithSpecialSign("%");
		checkScenarioWithSignWasCreated("%");
		createScenarioWithSpecialSign("^");
		checkScenarioWithSignWasCreated("^");
		createScenarioWithSpecialSign("&");
		checkScenarioWithSignWasCreated("&");
		createScenarioWithSpecialSign("()");
		checkScenarioWithSignWasCreated("()");
		createScenarioWithSpecialSign("_");
		checkScenarioWithSignWasCreated("_");
		createScenarioWithSpecialSign("-");
		checkScenarioWithSignWasCreated("-");
		createScenarioWithSpecialSign("+");
		checkScenarioWithSignWasCreated("+");
		createScenarioWithSpecialSign("=");
		checkScenarioWithSignWasCreated("=");
		createScenarioWithSpecialSign("'");
		checkScenarioWithSignWasCreated("'");
		createScenarioWithSpecialSign(";");
		checkScenarioWithSignWasCreated(";");

		/*
		 * DO NOT REMOVE!!!!! FAILS createScenarioWithSpecialSign("|");
		 * checkScenarioWithSignWasCreated("|");
		 * createScenarioWithSpecialSign("\\");
		 * checkScenarioWithSignWasCreated("\\");
		 * createScenarioWithSpecialSign("\"");
		 * checkScenarioWithSignWasCreated("\"");
		 * createScenarioWithSpecialSign("<>");
		 * checkScenarioWithSignWasCreated("<>");
		 * createScenarioWithSpecialSign("?");
		 * checkScenarioWithSignWasCreated("?");
		 * createScenarioWithSpecialSign("*");
		 * checkScenarioWithSignWasCreated("*");
		 */
	}

	/**
	 * 1. create a scenario named: my.new.scenario add a test and run it. 2.
	 * check that the test ended successful 3. return to defult scenario add a
	 * test and run it. 4. check that the test ended successful 5. return to
	 * my.new.scenario scenario and run it. 6. check that the test ended
	 * successful
	 * 
	 * @throws Exception
	 * @params.exclude
	 */
	@TestProperties(name = "5.2.3.1.2 Create scenario that it's name contains dots")
	public void testScenarioWithDotInName() throws Exception {
		applicationClient.launch();
		scenarioName = "my.new.scenario";
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		if (!jsystem.checkScenarioExist(scenarioName)) { // CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
	}

	/**
	 * 1. create a scenario named: my new scenario add a test and run it. 2.
	 * check that the test ended successful 3. return to defult scenario add a
	 * test and run it. 4. check that the test ended successful 5. return to my
	 * new scenario scenario and run it. 6. check that the test ended successful
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.3 Create scenario that it's name contains spaces")
	public void testScenarioWithSpaces() throws Exception {
		applicationClient.launch();
		scenarioName = "my new scenario";
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		if (!jsystem.checkScenarioExist(scenarioName)) {// CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
		// testScenarioExecute();
	}

	/**
	 * 1. create a scenario named: "123456" (with digits), add a test and run
	 * it. 2. check that the test ended successful 3. return to defult scenario
	 * add a test and run it. 4. check that the test ended successful 5. return
	 * to my new scenario scenario and run it. 6. check that the test ended
	 * successful
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.4 Create scenario that it's name contains digits")
	public void testScenarioWithDigits() throws Exception {
		applicationClient.launch();
		scenarioName = "123456";
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		if (!jsystem.checkScenarioExist(scenarioName)) {// CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
	}

	/**
	 * 1. create a scenario named: "�����" (with digits), add a test
	 * and run it. 2. check that the test ended successful 3. return to defult
	 * scenario add a test and run it. 4. check that the test ended successful
	 * 5. return to my new scenario scenario and run it. 6. check that the test
	 * ended successful
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.5 Create scenario that it's name in Hebrew")
	public void testScenarioWithHebrew() throws Exception {
		applicationClient.launch();
		scenarioName = "�����";
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		if (!jsystem.checkScenarioExist(scenarioName)) {// CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
	}

	/**
	 * Lunch Runner Add a Scenarion Do not add test to the Scenarion Close the
	 * Runner
	 * 
	 * @param ScenarioName
	 * @params.exclude numberOfTests
	 */
	@TestProperties(name = "5.2.3.1.6 Create scenario with no tests")
	public void testAddScenarioWithNoTests() throws Exception {
		report.report("testAddScenarioWithNoTests is starting");
		applicationClient.launch();
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);
		if (!jsystem.checkScenarioExist(scenarioName)) {// CHANGE?
			report.report("Cannot create Scenario with name: " + scenarioName, false);
		}
	}

	/**
	 * 1. Create a Scenario 2. Add tests to it according to the number defined
	 * in the numberOfTests param 3. Check that all tests have been successfully
	 * added 4. Clean the scenario.
	 */
	@TestProperties(name = "5.2.3.1.7 Create scenario with few tests")
	public void testCreateScenarionWithNumberOfTests() throws Exception {

		report.report("testCreateScenarionWithNumberOfTest " + scenarioName + " " + numberOfTests);
		applicationClient.launch();

		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioName);

		report.step("Add tests to it according to the number defined in the numberOfTests param");
		for (int i = 0; i < numberOfTests; i++)
			scenarioClient.addTest("testShouldPass", "GenericBasic");
		report.step("save the scenario");
		applicationClient.saveScenario();
		report.step("Checck that the scenario contains all tests");

		jsystem.checkNumberOfTestsExistInScenario(scenarioName, numberOfTests);// CANGE?

	}

	/**
	 * 1. Create scenario<br>
	 * 2. Clean old tests from this scenario 3.<br>
	 * Add 200 random tests <br>
	 * 4. Check that all the tests (200) are exist
	 * 
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.8 Create scenario with a massive number of tests ${numOfTests}")
	public void testLongScenario() throws Exception {
		applicationClient.launch();
		report.step("Clean old tests from this scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "longScenario");
		report.step("Add 200 new tests");
		report.step("Add " + numOfTestsToAdd + " Tests");
		scenarioClient.addTest("testShouldPass", "GenericBasic", numOfTestsToAdd);
		applicationClient.saveScenario();
		report.step("Checck that the scenario contains " + numberOfTests + " tests");
		jsystem.checkNumberOfTestsExistInScenario("longScenario", numOfTestsToAdd);
	}

	/**
	 * 1. Create root-scenario with 2 tests 2. Create sub-scenario1 with 2 tests
	 * ....Create sub-scenario2 with 2 tests ....Create sub-scenario1.1 with 2
	 * tests ....Create sub-scenario2.1 with 2 tests 3. Add sub-scenario1.1 to
	 * sub-scenario1 twice ....Check that sub-scenario1 contains 2 sub-scenarios
	 * and 6 tests 4. Add sub-scenario2.1 to sub-scenario2 twice ....Check that
	 * sub-scenario2 contains 2 sub-scenarios and 6 tests 5. Add sub-scenario1
	 * and sub-scenario2 to root-scenari 6. Check that root-scenario1 contains 2
	 * sub-scenarios and 14 tests
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.1.9 Create scenario that contain other scenarios of different kinds (Master Scenario)")
	public void testScenarioInScenario() throws Exception {
		applicationClient.launch();

		report.step("Create root-scenario with 2 tests");
		final String ROOT_SCENARIO_NAME = "scenarioRoot";
		ScenarioUtils.createAndCleanScenario(scenarioClient, ROOT_SCENARIO_NAME);
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		scenarioClient.addTest("testShouldPass", "GenericBasic");

		report.step("Create sub-scenario1 with 2 tests");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "sub-scenario1");
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		scenarioClient.addTest("testShouldPass", "GenericBasic");

		report.step("Create sub-scenario2 with 2 tests");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "sub-scenario2");
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		scenarioClient.addTest("testShouldPass", "GenericBasic");

		report.step("Create sub-scenario1.1 with 2 tests");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "scenario1.1");
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		scenarioClient.addTest("testShouldPass", "GenericBasic");

		report.step("Create sub-scenario2.1 with 2 tests");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "scenario2.1");
		scenarioClient.addTest("testShouldPass", "GenericBasic");
		scenarioClient.addTest("testShouldPass", "GenericBasic");

		report.step("Add sub-scenario1.1 to sub-scenario1 twice");
		scenarioClient.selectScenario("sub-scenario1");
		scenarioClient.addTest("scenario1.1", JSystem.SCENARIO);
		scenarioClient.addTest("scenario1.1", JSystem.SCENARIO);

		applicationClient.saveScenario();
		report.step("Check that sub-scenario1 contains 2 sub-scenarios and 6 (2+2*2) tests");
		jsystem.checkNumberOfSubScenariosExistInScenario("sub-scenario1", 2);
		jsystem.checkNumberOfTestsExistInScenario("sub-scenario1", 6);

		report.step("Add sub-scenario2.1 to sub-scenario2 twice");
		scenarioClient.selectScenario("sub-scenario2");
		scenarioClient.addTest("scenario2.1", JSystem.SCENARIO);
		scenarioClient.addTest("scenario2.1", JSystem.SCENARIO);

		applicationClient.saveScenario();
		report.step("Check that sub-scenario2 contains 2 sub-scenarios and 6 (2+2*2) tests");
		jsystem.checkNumberOfSubScenariosExistInScenario("sub-scenario2", 2);
		jsystem.checkNumberOfTestsExistInScenario("sub-scenario2", 6);

		report.step("Add sub-scenario1 and sub-scenario2 to root-scenari");
		scenarioClient.selectScenario(ROOT_SCENARIO_NAME);
		scenarioClient.addTest("sub-scenario1", JSystem.SCENARIO);
		scenarioClient.addTest("sub-scenario2", JSystem.SCENARIO);

		applicationClient.saveScenario();
		report.step("Check that root-scenario contains 2 sub-scenarios and 14 (2+6*2) tests");
		jsystem.checkNumberOfSubScenariosExistInScenario(ROOT_SCENARIO_NAME, 2);
		jsystem.checkNumberOfTestsExistInScenario(ROOT_SCENARIO_NAME, 14);
	}

	/**
	 * Create New Scenarion Add a test to it Randomly Execute the test
	 * 
	 * @throws Exception
	 * @params.exclude numberOfTests
	 */
	public void testCreateScenarioAddTestAndExecute() throws Exception {
		report.report("testCreateScenarioAddTestAndExecute with Scenario " + scenarioName);
		jsystem.launch();
		jsystem.createScenario(scenarioName);
		jsystem.addTest(JSystem.RANDOM, JSystem.RANDOM, true);
		jsystem.play();// non blocking play
		jsystem.waitForRunEnd();
	}

	/**
	 * 1. Launch the jsystem 2. Create new scenario 3. Add a test to the
	 * scenario (30 times) 4. execute the scenario 4. Analyze to see it pass
	 * 
	 * @throws Exception
	 * @params.exclude numberOfTests
	 */
	public void testCreateAndRunNewScenario() throws Exception {
		jsystem.launch();
		jsystem.createScenario("myNewScenario");
		jsystem.addTest("testShouldPass", "GenericBasic", 30, true);
		jsystem.play(); // non blocking play
		jsystem.waitForRunEnd();

		for (int i = 0; i < 30; i++) {
			jsystem.checkTestPass(i + 1);
		}
	}

	/**
	 * 
	 */
	public void testScenarioExecute() throws Exception {
		jsystem.launch();

		report.step("create a scenario named: " + scenarioName + " add a test and run it");
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkTestPass(1);

		report.step("return to the defult add a test and run it");
		ScenarioUtils.createAndCleanScenario(jsystem, "default");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkTestPass(2);

		report.step("back to the " + scenarioName + " and run it");
		jsystem.selectSenario(scenarioName);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkTestPass(3);
	}

	/**
	 * 1. Create scenario 2. Add 3 tests: "test1", "test2" and "test3" 3. Check
	 * that the scenario contains 3 tests
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.2 Assign some test to the scenario.")
	public void testScenarioAddTests() throws Exception {
		jsystem.launch();

		report.step("Clean old tests from this scenario");
		this.RemoveScenarioIfExist("scenarioAddTests");

		report.step("Create a scenario");
		jsystem.createScenario("scenarioAddTests");

		report.step("Add 3 tests:");
		jsystem.addTest("test1", "GenericBasic", true);
		jsystem.addTest("test2", "GenericBasic", true);
		jsystem.addTest("test3", "GenericBasic", true);

		jsystem.saveScenario();
		report.step("Check that the scenario: \"scenarioAddTests\" contains 3 tests");
		jsystem.checkNumberOfTestsExistInScenario("scenarioAddTests", 3);
	}

	/**
	 * 1. Create a scenario with 3 tests 1 and 3 should pass and 2 should fail.
	 * 2. Check that the scenario contains 3 tests 3. delete test 2. 4. Check
	 * that the scenario contains now 2 tests 5. Execute the scenaio. 6. See
	 * that all the tests passed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.3 Remove test from the scenario.")
	public void testScenarioTestDelete() throws Exception {

		jsystem.launch();

		report.step("Clean old tests from this scenario");
		this.RemoveScenarioIfExist("scenarioToDelete");

		report.step("Create a scenario with 3 tests 1 and 3 should pass and 2 should fail");
		jsystem.createScenario("scenarioToDelete");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);

		jsystem.saveScenario();
		report.step("Check that the scenario: \"scenarioToDelete\" contains 3 tests");
		jsystem.checkNumberOfTestsExistInScenario("scenarioToDelete", 3);

		report.step("delete test 2");
		jsystem.deleteTest(2);

		jsystem.saveScenario();
		report.step("Check that the scenario: \"scenarioToDelete\" contains 2 tests");
		jsystem.checkNumberOfTestsExistInScenario("scenarioToDelete", 2);

		report.step("Execute the scenario and check that all the tests pass");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);
	}

	public void testSelectSubScenario() throws Exception {
		jsystem.launch();

		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";

		report.step("Create a scenario with 2 tests in it");

		jsystem.createScenario(rootScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		report.step("Create second scenario with 2 tests in it");

		jsystem.createScenario(subScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.selectSenario(rootScenarioName);
		sleep(5000);

		report.step("add second scenario to the first one");

		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);

		report.step("Select second scenario as current scenario");

		jsystem.selectSenario(subScenarioName);
		sleep(10000);
	}

	/**
	 * adding sub scenario into ,main scenario , sub scenario have 2 tests in
	 * it, move up the second test and check that it`s the first now.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.4.a Change the tests location (up) within the scenario using the Navigation buttons")
	public void testMoveTestUpFromSubScenario() throws Exception {

		jsystem.launch();

		String rootScenarioName = "RootScneario";
		String subScenarioName = "SubScneario";

		report.step("Create a scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.saveScenario();

		report.step("Create second scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.saveScenario();

		jsystem.selectSenario(rootScenarioName);

		report.step("add second scenario twice to the first one");

		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);

		jsystem.saveScenario();
		report.step("move up second test in the sub scenario");

		jsystem.moveTestUp(5);
		jsystem.saveScenario();
		report.step("check test is first now");

		jsystem.checkTestLocationInScenariosRoot("scenarios" + File.separator + subScenarioName, "testFailWithError", 1);
	}

	/**
	 * adding sub scenario into ,main scenario , sub scenario have 2 tests in
	 * it, move down the first test and check that it`s the second now.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.4.b Change the tests location (down) within the scenario using the Navigation buttons")
	public void testMoveTestDownFromSubScenario() throws Exception {

		jsystem.launch();

		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";

		report.step("Create a scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.saveScenario();
		report.step("Create second scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.saveScenario();
		jsystem.selectSenario(rootScenarioName);

		report.step("add second scenario twice to the first one");

		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);

		jsystem.saveScenario();
		report.step("move down first test in the sub scenario");

		jsystem.moveTestDown(4);
		jsystem.saveScenario();
		report.step("check test is last now");

		jsystem.checkTestLocationInScenariosRoot("scenarios" + File.separator + subScenarioName, "testShouldPass", 2);
	}

	/**
	 * adding sub scenario into ,main scenario , sub scenario have 2 tests in
	 * it, move up the second test and check that it`s the first now.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.5.a Change the tests location (up) within the scenario using the Menu options.")
	public void testMoveTestUpByMenuOptionFromSubScenario() throws Exception {

		jsystem.launch();

		String rootScenarioName = "RootScneario";
		String subScenarioName = "SubScneario";

		report.step("Create a scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.saveScenario();
		report.step("Create second scenario with 2 tests in it");

		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.saveScenario();
		jsystem.selectSenario(rootScenarioName);

		report.step("add second scenario twice to the first one");

		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);

		jsystem.saveScenario();
		report.step("move up second test in the sub scenario");

		jsystem.moveTestUpByMenuOption(5);
		jsystem.saveScenario();
		report.step("check test is first now");

		jsystem.checkTestLocationInScenariosRoot("scenarios" + File.separator + subScenarioName, "testFailWithError", 1);
	}

	/**
	 * adding sub scenario into ,main scenario , sub scenario have 2 tests in
	 * it, move down the first test and check that it`s the second now.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.5.b Change the tests location (down) within the scenario using the Menu options.")
	public void testMoveTestDownByMenuOptionFromSubScenario() throws Exception {

		jsystem.launch();

		String rootScenarioName = "downRootScneario";
		String subScenarioName = "downSubScneario";

		report.step("Create a scenario with 2 tests in it");

		jsystem.createScenario(rootScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.saveScenario();
		report.step("Create second scenario with 2 tests in it");

		jsystem.createScenario(subScenarioName);

		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.saveScenario();
		jsystem.selectSenario(rootScenarioName);

		report.step("add second scenario twice to the first one");

		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);

		jsystem.saveScenario();
		report.step("move down first test in the sub scenario");

		jsystem.moveTestDownByMenuOption(4);
		jsystem.saveScenario();
		report.step("check test is last now");

		jsystem.checkTestLocationInScenariosRoot("scenarios" + File.separator + subScenarioName, "testShouldPass", 2);

	}

	/**
	 * 1. Create a scenario with 3 tests 1 and 3 should pass and 2 should fail.
	 * 2. uncheck test 2. 3. Execute the scenario. 4. See that all the tests
	 * passed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.7 Activate/Deactivate tests within the scenario using the menu option �Map�/�UnMap� options.")
	public void testScenarioTestUncheck() throws Exception {
		jsystem.launch();

		report.step("Clean old tests from this scenario");
		this.RemoveScenarioIfExist("scenarioToUncheck");

		report.step("Create a scenario with 3 tests 1 and 3 should pass and 2 should fail");
		jsystem.createScenario("scenarioToUncheck");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);

		report.step("uncheck test 2");
		jsystem.checkTest(2, false);

		report.step("Execute the scenario and check that all the tests pass");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);

		report.step("check 2 and uncheck 1 and 3");
		jsystem.checkTest(1, false);
		jsystem.checkTest(3, false);
		jsystem.checkTest(2, true);

		report.step("Execute the scenario and check the results");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(3);
		jsystem.checkNumberOfTestsPass(2);
	}

	/**
	 * 1. Create a scenario with 2 tests in it 2. Create second scenario with 2
	 * tests in it 3. Add second scenario to the first one 4. Check that the 1st
	 * scenario contains 1 sub-scenario and 4 tests 5. Execute the scenario and
	 * check the results
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.9.a Create Sub Scenario")
	public void testCreateSubScenario() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";
		jsystem.launch();
		report.step("Create a scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		report.step("Create second scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.selectSenario(rootScenarioName);
		report.step("add second scenario to the first one");
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.saveScenario();
		report.step("Check that scenario: \"" + rootScenarioName + "\" contain 1 sub-scenario and 4 tests");
		jsystem.checkNumberOfTestsExistInScenario(rootScenarioName, 4);
		jsystem.checkNumberOfTestsExistInScenario(subScenarioName, 2);
		jsystem.checkNumberOfSubScenariosExistInScenario(rootScenarioName, 1);
		report.step("Execute the scenario and check the results");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(4);
		jsystem.checkNumberOfTestsPass(2);
	}

	/**
	 * create sub scenario and copy it to a new scenario check that the new
	 * scenario contain the sub scenario`s tests.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.9.b Copy the create scenarios to new scenarios")
	public void testCopySubScenario() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";
		jsystem.launch();
		report.step("Create a scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		report.step("Create second scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.selectSenario(rootScenarioName);
		report.step("add second scenario to the first one");
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		report.step("copy sub scenario");
		jsystem.copyScenario("subScen_copy");
		report.step("check new scenario exists with all the sub scenario tests");
		jsystem.checkTestExistInScenario("scenarios/subScen_copy", "testShouldPass", true);
		jsystem.checkTestExistInScenario("scenarios/subScen_copy", "testFailWithError", true);
	}

	/**
	 * 1. Create master-scenario with 2 tests 2. Create sub scenario with no
	 * tests 3. Add the sub-scenario twice to the master-scenario 4. Add a test
	 * to one of the sub-scenarios 5. Run all master-scenario's tests 6. Check
	 * that 4 tests (2+1*2) were run.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.10.a Add Test To Sub Scenario")
	public void testAddTestToSubScenario() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";

		jsystem.launch();

		report.step("Create master-scenario with 2 tests");
		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.saveScenario();
		report.step("Create sub scenario with no tests");
		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);

		report.step("Check that the sub-scenario contain 0 tests");
		jsystem.checkNumberOfTestsExistInScenario(subScenarioName, 0);

		report.step("add second scenario to the first one");
		jsystem.selectSenario(rootScenarioName);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.saveScenario();
		report.step("Add a test to the 1st sub-scenario");
		jsystem.addTest(3, "testShouldPass", "GenericBasic", true); // 3 is the
		jsystem.saveScenario();
		// sub-scenario
		// location
		// (row)

		report.step("Check that the sub-scenario contain 1 test");
		jsystem.checkNumberOfTestsExistInScenario(subScenarioName, 1);

		report.step("Check that the Master scenario contain 2 sub-scenarios and 4 tests");
		jsystem.checkNumberOfSubScenariosExistInScenario(rootScenarioName, 2);
		jsystem.checkNumberOfTestsExistInScenario(rootScenarioName, 4);

		report.step("Run the tests");
		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Check that 4 tests were run");
		jsystem.checkNumberOfTestExecuted(4);
	}

	/**
	 * 1. Create master-scenario with 2 tests 2. Create sub scenario with 2
	 * tests 3. Add the sub-scenario twice to the master-scenario 4. Check that
	 * all 2 sub-scenarios and 6 tests are in the master-scenario 5. Delete a
	 * test from the first sub-scenarios 6. Check that 1 test is exist in the
	 * sub-scenario (4 tests in the master) after the test was deleted.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.10.b Remove Test From Sub Scenario")
	public void testRemoveTestFromSubScenario() throws Exception {
		jsystem.launch();

		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";

		report.step("Create a scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.saveScenario();
		report.step("Create second scenario with 2 tests in it");
		ScenarioUtils.createAndCleanScenario(jsystem, subScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testFailWithError", "GenericBasic", true);
		jsystem.saveScenario();
		report.step("add second scenario twice to the first one");
		jsystem.selectSenario(rootScenarioName);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		jsystem.saveScenario();
		report.step("check the test for delete is exist before deleting it");
		jsystem.checkNumberOfTestsExistInScenario(subScenarioName, 2);
		jsystem.checkNumberOfSubScenariosExistInScenario(rootScenarioName, 2);
		jsystem.checkNumberOfTestsExistInScenario(rootScenarioName, 6);

		report.step("delete the first test from sub scenario");
		jsystem.deleteTest(4); // 4 = the row of the 1st sub-scenario's test
		jsystem.saveScenario();
		report.step("check the deleted test in not exists in scenario");
		jsystem.checkNumberOfTestsExistInScenario(subScenarioName, 1);
		jsystem.checkNumberOfTestsExistInScenario(rootScenarioName, 4);
	}

	/**
	 * Creating a hug scenario with a lot of sub scenarios and run the scenario
	 * 
	 * This test was created mainlly to search for GUI memory leack after a
	 * night run in repeat
	 * 
	 * @throws Exception
	 */
	public void createHugeScenarioRun() throws Exception {
		int i = 0, j = 0;

		jsystem.launch();

		for (i = 0; i < 5; i++) {
			jsystem.createScenario("Scenariolevel" + i);

			for (j = 0; j < 5; j++) {
				jsystem.addTest("testRunExternalClassTests2", "ExternalClass", true);
			}

			for (j = i - 1; j >= 0; j--) {
				jsystem.addTest("Scenariolevel" + j, JSystem.SCENARIO, true);
			}
		}

		jsystem.createScenario("ScenarioMain");
		jsystem.addTest("Scenariolevel4", "scenarios", true);

		report.report("Finished to add the ScenarioMain with all its sub and sub-sub scenarios");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.deleteTest("ScenarioMain");

		for (i = 0; i < 5; i++) {
			jsystem.deleteTest("Scenariolevel" + i);
		}
	}

	/**
	 * This test does the following: 1.craetes a master scenario with 10 Sub
	 * scenario 2.Each sub scnario has one test 3.Delete two sub scenario 4.Run
	 * the master scenario 5.Verify that that 8 tests were successfully passed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.15 Remove two sub scenario From a master scenario")
	public void testCreateMasterScenarioDeleteTwoSubScenariosAndRun() throws Exception {
		String rootScenarioName = "rootScneario";

		jsystem.launch();
		report.step("Clean old tests from all scenarios in this test");

		// Creating 10 Sub Secnarios with one test
		for (int i = 0; i < 10; i++) {
			this.RemoveScenarioIfExist("subScenario" + i);
			jsystem.createScenario("subScenario" + i);
			jsystem.addTest("testShouldPass", "GenericBasic", true);
		}
		// Creating a Master scenario
		this.RemoveScenarioIfExist(rootScenarioName);
		jsystem.createScenario(rootScenarioName);

		// Adding the 10 subScenarios to the master scenario
		for (int i = 0; i < 10; i++) {
			jsystem.addTest("subScenario" + i, JSystem.SCENARIO, true);
		}
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 10);
		// Deleting two sub scenarios (5 & 8)
		jsystem.deleteTest(11);
		jsystem.deleteTest(15);
		jsystem.play();
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 8);
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(8);
		jsystem.checkNumberOfTestsPass(8);
	}

	/**
	 * This test does the following: 1.craetes a master scenario with 10 Sub
	 * scenario 2.Each sub scnario has one test 3.Run masive of movements
	 * whithing the scenario 4.Run the master scenario 5.Verify that that 10
	 * tests were successfully passed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.16 Run Masive amount of tests movements")
	public void testRunningMassiveAmountOfTestMovments() throws Exception {
		String rootScenarioName = "rootScneario";

		jsystem.launch();
		report.step("Clean old tests from all scenarios in this test");

		// Creating 10 Sub Secnarios with one test
		for (int i = 0; i < 10; i++) {
			this.RemoveScenarioIfExist("subScenario" + i);
			jsystem.createScenario("subScenario" + i);
			jsystem.addTest("testShouldPass", "GenericBasic", true);
		}
		// Creating a Master scenario
		this.RemoveScenarioIfExist(rootScenarioName);
		jsystem.createScenario(rootScenarioName);

		// Adding the 10 subScenarios to the master scenario
		for (int i = 0; i < 10; i++) {
			jsystem.addTest("subScenario" + i, JSystem.SCENARIO, true);
		}
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 10);
		// Run Masive test movements whithin the scanario
		Random generator = new Random();
		for (int i = 0; i < 100; i++) {
			jsystem.moveTestDown(generator.nextInt(17) + 1);
			jsystem.moveTestUp(generator.nextInt(17) + 1);
		}

		jsystem.play();
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 10);
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(10);
		jsystem.checkNumberOfTestsPass(10);
	}

	/**
	 * This test does the following: 1.craetes a master scenario with 10 Sub
	 * scenario 2.Each sub scnario has one test 3.Run masive of scenario
	 * collapse & expand operations 4.Run the master scenario 5.Verify that that
	 * 10 tests were successfully passed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.8 Run Masive amount openning and closing scenarios")
	public void testRunningMassiveAmountOfCollapseExpandScenarios() throws Exception {
		String rootScenarioName = "rootScneario";

		jsystem.launch();
		report.step("Clean old tests from all scenarios in this test");

		// Creating 10 Sub Secnarios with one test
		for (int i = 0; i < 10; i++) {
			this.RemoveScenarioIfExist("subScenario" + i);
			jsystem.createScenario("subScenario" + i);
			jsystem.addTest("testShouldPass", "GenericBasic", true);
		}
		// Creating a Master scenario
		this.RemoveScenarioIfExist(rootScenarioName);
		jsystem.createScenario(rootScenarioName);

		// Adding the 10 subScenarios to the master scenario
		for (int i = 0; i < 10; i++) {
			jsystem.addTest("subScenario" + i, JSystem.SCENARIO, true);
		}
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 10);
		// Run Masive test movements whithin the scenario
		Random generator = new Random();
		for (int i = 0; i < 100; i++) {
			jsystem.CollapseExpandScenario(generator.nextInt(20));
		}

		jsystem.play();
		jsystem.checkNumberOfSubScenariosExistInScenario("rootScneario", 10);
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(10);
		jsystem.checkNumberOfTestsPass(10);
	}

	public void testTest() throws Exception {
		jsystem.launch();
		jsystem.selectSenario("level1");
		sleep(3000);
		jsystem.CollapseExpandScenario(2);
		jsystem.CollapseExpandScenario(3);
	}

	public void tearDown() throws Exception {
		super.tearDown();

		jsystem.exit();
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}

	/**
	 * 
	 * @param scanrioName
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.3.2.11 Remove a scenario if it Exists")
	private void RemoveScenarioIfExist(String scanrioName) throws Exception {
		report.report("Checking if Scnario - " + scanrioName + " exists and if so it will be deleted");

		if (jsystem.checkScenarioExist(scanrioName)) {
			jsystem.selectSenario(scanrioName);
			report.report("Scenario-" + scanrioName + " was selected");

			// jsystem.cleanCurrentScenario();
			String scenarioName = jsystem.getCurrentScenario();
			ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
			sleep(2000);
			report.report("Scenario-" + scanrioName + " was cleared");
			sleep(3000);
			report.report("Scnario - " + scanrioName + " existed and was deleted");
		}

	}

	public int getNumOfTestsToAdd() {
		return numOfTestsToAdd;
	}

	public void setNumOfTestsToAdd(int numOfTestsToAdd) {
		this.numOfTestsToAdd = numOfTestsToAdd;
	}
}
