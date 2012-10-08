package com.aqua.sanity;

import java.io.File;
import java.io.FileNotFoundException;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystem;

public class FixtureFunctionality extends JSysTestCaseOld {
	public FixtureFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	/**
	 * Execute test that require AdvanceFixture than check all the fixture path
	 * was executed. <br>
	 * <br>
	 * "FixtureTest" set to AdvanceFixture<br>
	 * BasicFixture is the parent of AdvanceFixture<br>
	 * The purpose of the test is to verify that the setUp methods<br>
	 * performed in the right order: BasicFixture -> AdvanceFixture ->
	 * FixtureTest<br>
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.1.6 Test Set Current fixture")
	public void testBasicExecution() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatPass", "FixtureTest", true);
		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze right setUp order performed");

		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvanceFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "FixtureTest setUp");
	}
	/**
	 * 1. Create a scenario 
	 * 2. Create second scenario
	 * 3. Add second scenario to the first one 
	 * 4. Check that the 1st scenario contains 1 sub-scenario and 1 fixture that is not identified
	 *	  as a test 
	 * 5. Execute the scenario and check the results
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "Test for bug: Create Sub Scenario With Fixture")
	public void testCreateSubScenarioWithFixture() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";
		jsystem.launch();
		
		ScenarioUtils.createAndCleanScenario(jsystem,rootScenarioName);
		ScenarioUtils.createAndCleanScenario(jsystem,subScenarioName);
		
		jsystem.selectSenario(rootScenarioName);
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
										
		report.step("Adds BasicFixture2");		
		jsystem.addTest(1,"regression.generic.fixturetree.BasicFixture2", "fixturetree", true);
		jsystem.addTest("testThatPass", "FixtureTest", true);
				
		jsystem.play(true);
		
		report.step("Checks if fixture was identified as a test or not. ");
		jsystem.checkNumberOfTestExecuted(1);		
	}

	/**
	 * 1. create a root scenario
	 * 2. select the scenario and add a fixture to it
	 * 3. add a test
	 * check that only 1 test was counted as test, and that the report xml has a line
	 * representing the fixture setUp
	 */
	@TestProperties(name = "Test that fixtures work well when test is added after fixture")
	public void testFirstFixtureThenTest() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";
		jsystem.launch();
		
		ScenarioUtils.createAndCleanScenario(jsystem,rootScenarioName);
		ScenarioUtils.createAndCleanScenario(jsystem,subScenarioName);
		
		jsystem.selectSenario(rootScenarioName);
		report.step("Adds BasicFixture");		
		jsystem.addTest("regression.generic.BasicFixture", "generic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);										
				
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("Checks if fixture was identified as a test or not. ");
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture setUp");

	}

	/**
	 * 1. Create a sub scenario 
	 * 2. Create a root scenario 
	 * 3. Add test + fixture to Scenario 
	 * 4. add subScenario 
	 * 5. add test + fixture to subScenario
	 * check that the right setUp order was performed
	 * 
	 * @throws Exception
	 */
	@TestProperties(name="test check root fixture against sub fixture")
	public void testCheckRootFixtureAgainstSubFixture() throws Exception {
		String rootScenarioName = "rootScneario";
		String subScenarioName = "subScneario";
		jsystem.launch();
		//Creates the SubScenario in the tests tree- and puts it as only Scenario in Scenario tree
		ScenarioUtils.createAndCleanScenario(jsystem,subScenarioName);		
		//Creates the Root Scenario- and clears Scenario tree from subScenario, and add root to tree
		ScenarioUtils.createAndCleanScenario(jsystem,rootScenarioName);
		//adds test + fixture under rootScenario
		jsystem.addTest("testShouldPass", "GenericBasic", true);		
		jsystem.addTest("regression.generic.fixturetree.AdvancedFixture1", "fixturetree", true);

		//Adds the SubScenario.
		jsystem.addTest(subScenarioName, JSystem.SCENARIO, true);
		//add test and fixture to subScenario
		jsystem.addTest(3,"testShouldPass", "GenericBasic", true);
		jsystem.addTest(3,"regression.generic.fixturetree.AdvancedFixture2", "fixturetree", true);


		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		report.step("Analyze right setUp order performed");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvancedFixture1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture1 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture1 tearDown");
		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture1 tearDown");
		
	}	
	
	/**
	 * Set the current fixture to AdvanceFixture execute the test, Only the test
	 * report should be found.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.2.1 Test Set Current fixture")
	public void testSetCurrent() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatPass", "FixtureTest", true);

		report.step("Set JSystem Current Fixture to AdvanceFixture");

		jsystem.setCurrentFixture("AdvanceFixture");
		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze only test setup activated");

		jsystem.checkXmlTestAttributeNotExist(1, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttributeNotExist(1, "steps", "AdvanceFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "FixtureTest setUp");
	}

	/**
	 * 1. Creates a Secnario
	 * 2. Add BasicFixture2
	 * 3. Add test - testShouldPass
	 * 4. runs and checks if BasicFixture replace the default fixture which is - TestFixture	 
	 * 
	 * @throws Exception
	 */
	@TestProperties(name="test basic fixture replace the default fixture for the test")
	public void testBasicFixture() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatPass", "FixtureTest", true);	
		jsystem.addTest(1,"regression.generic.fixturetree.BasicFixture2", "fixturetree", true);		
		jsystem.play();		
		jsystem.waitForRunEnd();
		
		report.step("Searching for BasicFixture2 in the report");
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture2 setUp");		
	}
	
	
	/**
	 * 1. Create a Scenario.
	 * 2. Adds a Fixture.
	 * 3. Selects the root Scenario and adds a Test.
	 * 4. Checks what is first in the tree - the Test or the Fixture.
	 * 
	 * @throws Exception
	 */
	public void testCheckWhatCameFirstTestOrFixture() throws Exception
	{
		String rootScenarioName = "rootScneario";
		String fixture = "BasicFixture2" ;
		String fixturePath = "fixturetree";
		String fixturePackage = "regression.generic";
		
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		ScenarioUtils.createAndCleanScenario(jsystem, rootScenarioName);
		jsystem.selectSenario(rootScenarioName);		
		
		jsystem.addTest(0, fixturePackage +"." + fixturePath + "." + fixture, fixturePath, true);
		jsystem.addTest("testThatPass", "FixtureTest", true);
		jsystem.saveScenario();
		//jsystem.addTest(1,fixturePackage+ "."+ fixturePath + "." +fixture, fixturePath);		
						
		String sFirstRow; //Fixture
		String sSecondRow; //Test 
		
		sFirstRow = "setUp";
		sSecondRow = "testThatPass";
		
		rootScenarioName = "scenarios/" + rootScenarioName;
		jsystem.checkTestLocationInScenariosRoot(rootScenarioName, sFirstRow, 1);
		jsystem.checkTestLocationInScenariosRoot(rootScenarioName, sSecondRow, 2);
	}	
	
	/**
	 * Execute a test that fail then a test that pass both requested
	 * AdvanceFixture. Check all the results available. Then rerun the pass
	 * test and check the current fixture stayed AdvanceFixture.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.2.3 Test fail fixture")
	public void testFailTest() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatFail", "FixtureTest", true);
		jsystem.addTest("testThatPass", "FixtureTest", true);
		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze all Fixtures setUp  in the way activated for the first test");

		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvanceFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "FixtureTest setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "FixtureTest tearDown");

		report.step("Analyze failTearDown activated for all the Fixtures ( because the test setTearDownFixture set to RootFixture)");

		jsystem.checkXmlTestAttribute(1, "steps", "AdvanceFixture failTearDown");
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture failTearDown");

		report.step("Analyze all Fixtures setUp  in the way activated for the second test");

		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvanceFixture setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "FixtureTest setUp");

		report.step("Create new scenario with 1 pass test");

		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatPass", "FixtureTest", true);
		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze only test setUp activated");

		jsystem.checkXmlTestAttributeNotExist(3, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttributeNotExist(3, "steps", "AdvanceFixture setUp");
		jsystem.checkXmlTestAttribute(3, "steps", "FixtureTest setUp");
	}

	/**
	 * check that the goto functionality is working.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.2.2 Test go to fixture")
	public void testGoTo() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());

		jsystem.addTest("testThatPass", "FixtureTest", true);

		report.step("Activate goTo AdvanceFixture");
		
		jsystem.goToFixture("AdvanceFixture");
//		wait till all setUp and TearUp operations are done
		jsystem.waitForRunEnd();
		
//		check that the relevant fixture setUps where made.
//		basic and then advanced
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvanceFixture setUp");
		jsystem.refresh();
		jsystem.play();
//		Thread.sleep(5000);
//		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze only test setUp activated");

		jsystem.checkXmlTestAttribute(2, "steps", "FixtureTest setUp");
	}

	/**
	 * Check navigation result on 2 tests using 2 fixtures in 2 different
	 * branches
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.1.6 Check navigation result on 2 tests using 2 fixtures")
	public void testNavigationOn2Branches() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());

		jsystem.addTest("testPass2", "UsingAdvancedFixture1_1Tests", true);
		jsystem.addTest("testPass3", "UsingAdvancedFixture2_1Tests", true);

		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze navigation to AdvancedFixture1_1");

		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvancedFixture1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvancedFixture1_1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "UsingAdvancedFixture1_1Tests setUp");

		report.step("Analyze navigation back to Root");

		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture1_1 tearDown");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture1 tearDown");
		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture1 tearDown");

		report.step("Analyze navigation to AdvancedFixture2_1");

		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture2_1 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "UsingAdvancedFixture2_1Tests setUp");
	}

	/**
	 * check the work around to set Fixture as parameter to test.
	 * add two identical tests to test tree and set them with fixture as parameter
	 * watch the navigation path from one fixture to the other throw teardowns 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.2.4 - check the work around to set Fixture as parameter to test")
	public void testFixturesUsingParametersWorkAround() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());

		jsystem.addTest("testPass", "FixtureAsParameterTests", true);
		jsystem.addTest("testPass", "FixtureAsParameterTests", true);
		
		//set the test parameteres from runner tab testInfo to paramName = testfixture
		//and value = advancedFixture1_1 OR advancedFixture2_1
		jsystem.setTestParameter(1, "General", "TestFixture", "AdvancedFixture1_1",false);
		jsystem.setTestParameter(2, "General", "TestFixture", "AdvancedFixture2_1",false);

		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("Analyze navigation to AdvancedFixture1_1");
		
		//check execution path results
		jsystem.checkXmlTestAttribute(1, "steps", "BasicFixture1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvancedFixture1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "AdvancedFixture1_1 setUp");
		jsystem.checkXmlTestAttribute(1, "steps", "FixtureAsParameterTests setUp");

		report.step("Analyze navigation back to Root");

		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture1_1 tearDown");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture1 tearDown");
		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture1 tearDown");

		report.step("Analyze navigation to AdvancedFixture2_1");

		jsystem.checkXmlTestAttribute(2, "steps", "BasicFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture2 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "AdvancedFixture2_1 setUp");
		jsystem.checkXmlTestAttribute(2, "steps", "FixtureAsParameterTests setUp");
	}

	int stressAmount = 5;

	/**
	 * check navigation from branch to branch for a long time.
	 * add tests to tests tree 2 times the amount set in stressAmount
	 * set test parameters with fixture, each test with different fixture
	 * AdvancedFixture1_1 Or AdvancedFixture2_1
	 * check the fixture navigation path in report. 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.6.3.1 - Stress add massive amount of tests with fixtures")
	public void testStress() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		
		//add two tests per iteration in loop
		for (int i = 0; i < stressAmount; i++) {
			jsystem.addTest("testPass", "FixtureAsParameterTests", true);
			jsystem.addTest("testPass", "FixtureAsParameterTests", true);
		}
		
		//set all tests added with fixture parameters
		for (int i = 0; i < (stressAmount * 2); i += 2) {

			jsystem.setTestParameter(i + 1, "General", "TestFixture", "AdvancedFixture1_1",false);
			jsystem.setTestParameter(i + 2, "General", "TestFixture", "AdvancedFixture2_1",false);

		}

		jsystem.play();
		jsystem.waitForRunEnd();
		
		//fun on all tests and check their fixture setup stages, 
		//tearDown stages. 
		for (int i = 0; i < (stressAmount * 2); i += 2) {
			//in the first iteration there will be a tearDown to root
			if (i != 0) {

				report.step("Analyze navigation back to Root");

				jsystem.checkXmlTestAttribute((i + 1), "steps", "AdvancedFixture2_1 tearDown");
				jsystem.checkXmlTestAttribute((i + 1), "steps", "AdvancedFixture2 tearDown");
				jsystem.checkXmlTestAttribute((i + 1), "steps", "BasicFixture2 tearDown");
			}

			report.step("Analyze navigation to AdvancedFixture1_1");

			jsystem.checkXmlTestAttribute((i + 1), "steps", "BasicFixture1 setUp");
			jsystem.checkXmlTestAttribute((i + 1), "steps", "AdvancedFixture1 setUp");
			jsystem.checkXmlTestAttribute((i + 1), "steps", "AdvancedFixture1_1 setUp");
			jsystem.checkXmlTestAttribute((i + 1), "steps", "FixtureAsParameterTests setUp");

			report.step("Analyze navigation back to Root");

			jsystem.checkXmlTestAttribute((i + 2), "steps", "AdvancedFixture1_1 tearDown");
			jsystem.checkXmlTestAttribute((i + 2), "steps", "AdvancedFixture1 tearDown");
			jsystem.checkXmlTestAttribute((i + 2), "steps", "BasicFixture1 tearDown");

			report.step("Analyze navigation to AdvancedFixture2_1");

			jsystem.checkXmlTestAttribute((i + 2), "steps", "BasicFixture2 setUp");
			jsystem.checkXmlTestAttribute((i + 2), "steps", "AdvancedFixture2 setUp");
			jsystem.checkXmlTestAttribute((i + 2), "steps", "AdvancedFixture2_1 setUp");
			jsystem.checkXmlTestAttribute((i + 2), "steps", "FixtureAsParameterTests setUp");
		}
	}
	/**
	 * test that if exists fixture loop then the test fails
	 * @throws Exception
	 */
	@TestProperties(name="test loop fixture run fail")
	public void testLoopFixture() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("regression.generic.FixtureLoop1", "generic", true);
		jsystem.addTest("testStartEndReport", "ReporterTests", true);
		jsystem.play(true);
		File reportXml = new File(envController.getDistRunOut() + "\\log\\current\\reports.0.xml");
		if (!reportXml.exists()) {
			report.report("checkXmlTestAttribute file, reporter xml file: " + reportXml.getAbsolutePath()
					+ ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		analyzer.setTestAgainstObject((FileUtils.read(reportXml)));
//		analyzer.analyze(new FindText("testLoopFixture"));
		analyzer.analyze(new FindText("ReporterTests.testStartEndReport"));
		jsystem.checkTestStatus(1, false);
	}
    public int getStressAmount() {
		return stressAmount;
	}
	
	public void setStressAmount(int stressAmount) {
		this.stressAmount = stressAmount;
	}
}
