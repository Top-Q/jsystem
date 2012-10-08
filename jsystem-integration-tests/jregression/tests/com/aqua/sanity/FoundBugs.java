package com.aqua.sanity;

import java.io.File;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.GeneralEnums.RunMode;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * Add to regression tests that were found manualy
 * @author guy.arieli
 *
 */
public class FoundBugs extends JSysTestCaseOld {
	public FoundBugs(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		//setTearDownFixture(RootFixture.class);
	}
	/**
	 * Test add press add to scenario when no tests are selected
	 * 1. Create a scenario with a test.
	 * 2. then just press the add button 2 times.
	 * 3. run the scenario and check only one test were executed.
	 */
	@TestProperties(name = "5.2.4.22 press add test, when no test is selected")
	public void testAddTestWithNoTestSelected() throws Exception{
		jsystem.launch();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.moveCheckedToScenarioTree();
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkNumberOfTestExecuted(1);
	}
	
	/**
	 * 
	 */
	@TestProperties(name="check unmap all functionality")
	public void testCheckUnmapAllFunctionality() throws Exception{
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"child");
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"master");
		jsystem.addTest("child","addTestToSubScenarioBug", true);
		jsystem.addTest("testShouldPass", "GenericBasic", 3, true);
		jsystem.unmapTest(0);
		jsystem.setTestAgainstObject(0);
		jsystem.analyze(new NumberCompare(compareOption.EQUAL, jsystem.getNumOfchkBoxChekced(), 0));
	}
	
	@TestProperties(name="check tests adding to subscenario is successful")
	public void testAddTestToSubscenario()throws Exception{
		final int numOfTests = 3;
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"child");
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"master");
		jsystem.addTest("child","addTestToSubScenarioBug", true);
		jsystem.addTest("testShouldPass", "GenericBasic", numOfTests, true);
		jsystem.setTestAgainstObject(numOfTests);
		jsystem.analyze(new NumberCompare(compareOption.EQUAL, jsystem.getNumOfTestsInScenario(), 0));
	}
	/**
	 * tests that run mode 4 (change jvm every scenario change) 
	 * doesn't interfere with correct run of all test after pressing play
	 * @throws Exception
	 */
	@TestProperties(name="test scenario run mode stability")
	public void testAddTestToScenarioEndInRunmode4() throws Exception{
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_SCENARIO.toString());
		jsystem.launch();
		//create a folder under default Scenarios folder named "addTestToSubScenarioBug" and inside it create
		//a scenario called child and one calld master
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"child");
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug"+File.separator+"master");
		
		//after adding the master to tree, add the child scenario
		jsystem.addTest("child","addTestToSubScenarioBug", true);
		
		jsystem.addTest(1,"testShouldPass", "GenericBasic", true);
		jsystem.addTest(2,"testShouldPass", "GenericBasic", true);
		jsystem.addTest(3,"testShouldPass", "GenericBasic", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(3);
		jsystem.checkNumberOfTestExecuted(3);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.initReporters();
		jsystem.play();
		jsystem.waitForRunEnd();
		//check that run mode doesn't interfere with correct program run
		//still all test will pass even though it's a run mode to change jvm
		//on each scenario
		jsystem.checkNumberOfTestsPass(5);
		jsystem.checkNumberOfTestExecuted(5);

	}
	/**
	 * run a test with preDefined parameters, and it changes the value of one of them.
	 * only the value of parameter str8 is changed for the run
	 * @throws Exception
	 */
	@TestProperties(name="test equal sign parameters for test")
	public void testParametersWithEquals() throws Exception {
		jsystem.launch();
//		ScenarioUtils.createAndCleanScenario(jsystem, ScenariosManager.getInstance().getCurrentScenario().getScenarioName());
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testWithInclueParametersNewLine", "ParamaetersHandlingSection", true);
		jsystem.setTestParameter(1, "General", "Str8", "x=2=3=4, v b n eee=,", false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(1);
		String propValue = 
			getRunProperties().getProperty("testWithInclueParametersNewLine_str8");
		assertEquals("x=2=3=4, v b n eee=,", propValue);
	}
}
