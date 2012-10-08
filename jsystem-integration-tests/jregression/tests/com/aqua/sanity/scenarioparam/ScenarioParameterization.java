package com.aqua.sanity.scenarioparam;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;

/**
 * Tests scenario parameterization feature.
 * 
 */
public class ScenarioParameterization extends JSysTestCaseOld {

	public ScenarioParameterization() {
		super();
//		setFixture(SelectScenarioParameterizationFixture.class);
		setFixture(CreateScenarioParameterizationFixture.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		report.step("after super setup");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);
		jsystem.mapTest(0);
		jsystem.initReporters();
	}

	public void testParametersUpdate() throws Exception {
		report.step("test started");
		Properties expectedResults = loadPropFile("com/aqua/sanity/scenarioparam/ScenarioParameterization_testParametersUpdate.propreties");
		sleep(1000);
		jsystem.setTestParameter(2, "General", "Param2", "testParametersUpdate-0", false,true);
		jsystem.setTestParameter(3, "General", "Param2", "testParametersUpdate-1", false);
		jsystem.setTestParameter(4, "General", "Param2", "testParametersUpdate-2", false);
		jsystem.setTestParameter(5, "General", "Param2", "testParametersUpdate-3", false,true);
		jsystem.setTestParameter(10, "General", "Param2", "testParametersUpdate-4", false,true);
		jsystem.setTestParameter(11, "General", "Param2", "testParametersUpdate-5", false);
		jsystem.setTestParameter(12, "General", "Param2", "testParametersUpdate-6", false);
		jsystem.setTestParameter(13, "General", "Param2", "testParametersUpdate-6", false);
		runScenarioAndValidateRun(11, 9, expectedResults);
	}

	public void testLoadScenarioAndRun() throws Exception {
		Properties expectedResults = loadPropFile("com/aqua/sanity/scenarioparam/ScenarioParameterization_testParametersUpdate.propreties");
		sleep(1000);
		jsystem.selectSenario("default");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);
		runScenarioAndValidateRun(11, 9, expectedResults);
	}

	public void testDocumentationUpdate() throws Exception {
		
		Random random = new Random();
		int[] testIndexes = new int[]{0,2,3,10,12,13};
		int[] randomArray = new int[testIndexes.length];
		
		for (int i=0 ; i<testIndexes.length ; i++){
			randomArray[i] = random.nextInt();
			jsystem.setTestUserDocumentation(testIndexes[i], "testCommentUpdate" + randomArray[i]);
		}
		
		for (int i=0 ; i<testIndexes.length ; i++){
			String userDoc = jsystem.getTestUserDocumentation(testIndexes[i]);
			assertEquals("testCommentUpdate" + randomArray[i], userDoc);
		}
		
		jsystem.selectSenario("default");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);

		
		for (int i=0 ; i<testIndexes.length ; i++){
			String userDoc = jsystem.getTestUserDocumentation(testIndexes[i]);
			assertEquals("testCommentUpdate" + randomArray[i], userDoc);
		}
	}


	public void testSelectUnSelect_1() throws Exception {
		int result = jsystem.unmapTest(0);
		if(result == -1){
			report.report("all tsets where already un mapped");
		}
		result = jsystem.mapTest(1);
		if(result != 0){
			throw new Exception("couldn't map test!!!");
		}
		runScenarioAndValidateRun(1, 1, null);
		jsystem.selectSenario("default");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);
		jsystem.initReporters();
		runScenarioAndValidateRun(1, 1, null);
	}

	public void testSelectSelect_1() throws Exception {
		jsystem.unmapTest(0);
		jsystem.mapTest(5);
		jsystem.mapTest(11);
		runScenarioAndValidateRun(5, 3, null);
		jsystem.initReporters();
		jsystem.selectSenario("default");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);
		runScenarioAndValidateRun(5, 3, null);
	}

	public void testScenarioWithManyParameters() throws Exception {
		report.report("inside testScenarioWithManyParameters()");
		jsystem.selectSenario("scenarioparameterization"+File.separator+"bigscenariowithmanytests");
		sleep(5000);
		runScenarioAndValidateRun(66, 66, null);
	}

	/**
	 * Verifies a fix to the following bug:
	 * 1. create parent scenario called parent
	 * 2. add to it a sub scenario called child
	 * 3. select the S as root scenario
	 * 4. add to the S  test and give to one of the parameters a value -X
	 * 
	 * When selecting the parent again the value 
	 * of the parameter of the added test should be X    
	 * 
	 */
	public void testScenarioWithSubScenarioWereTestsAreAddedToSubScenarioDirectlyAfterItWasAddedToParent() throws Exception {
		Properties expectedResults = loadPropFile("com/aqua/sanity/scenarioparam/addingTestToSubScenarioDirectly.propreties");
		report.report("Creating the child scenario and adding a test to it");
		ScenarioUtils.createAndCleanScenario(jsystem,"addTestToSubScenarioBug/child");
		jsystem.addTest("testParameterization1", "ScenarioParameterizationTest", true);
		jsystem.setTestParameter(1, "General", "Param2", "not default value", false);
		report.report("Creating the parent scenario and adding a test that verifies that run.properties is empty.");
		report.report("The test also resets the .run.properties. This reset is critical to the success of the test");
		report.report("In addition adding the chile scenario");
		ScenarioUtils.createAndCleanScenario(jsystem,"parent");
		jsystem.addTest("testRunPropertyIsEmpty", "ScenarioParameterizationTest", true);
		jsystem.addTest("child","addTestToSubScenarioBug", true);
		report.report("Selecting the child scenario, adding a test to it and giving one of the parameters a value");
		jsystem.selectSenario("addTestToSubScenarioBug/child");
		jsystem.addTest("testParameterization1", "ScenarioParameterizationTest", true);
		jsystem.setTestParameter(2, "General", "Param2", "not default value 2", false,true);
		report.report("Selecting the parent scenario and executing it");
		report.report("The execution saves the value of the parameters in the .run.properties. the result .run.properties is compared with the exepected .run.properties");
		jsystem.selectSenario("parent");
		runScenarioAndValidateRun(3,3, expectedResults);
	}

	private void runScenarioAndValidateRun(int numberOfRunTests, int numberOfSuccess, Properties runPropFileValues)
			throws Exception {
		report.report("inside runScenarioAndValidateRun");
		sleep(1000);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(numberOfRunTests);
		jsystem.checkNumberOfTestsPass(numberOfSuccess);
		if (runPropFileValues != null) {
			if (!getRunProperties().equals(runPropFileValues)) {
				report.report("Actual properties: " + getRunProperties().toString(), false);
				report.report("Expected properties: " + runPropFileValues.toString(), false);
			}
		}
	}

	private Properties loadPropFile(String name) throws Exception {
		Properties prop = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream(name);
		try {
			prop.load(ins);
		} finally {
			ins.close();
		}
		return prop;
	}
}
