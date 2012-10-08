package com.aqua.sanity.scenarioparam;


import utils.ScenarioUtils;
import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobject.ActivateRunnerFixture;
import com.aqua.jsystemobject.JSystem;
import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemEnvControllerOld;

public class CreateScenarioParameterizationFixture extends Fixture {
	public static final String PARENT_SCENARIO_NAME= "ScenarioParameterizationParent";
	public static final String CHILD1_LEVEL1_SCENARIO_NAME= "ScenarioParameterizationChild1Level1";
	public static final String CHILD2_LEVEL1_SCENARIO_NAME= "ScenarioParameterizationChild2Level1";
	
	private JSystemClient jsystem;
	public CreateScenarioParameterizationFixture(){
		setParentFixture(ActivateRunnerFixture.class);
	}
	
	public void setUp() throws Exception{
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		jsystem	= envController.getJSystemEnv();
		createBasicParameterizationScenario();
	}
	
	private void createBasicParameterizationScenario() throws Exception {

		ScenarioUtils.createAndCleanScenario(jsystem,CHILD1_LEVEL1_SCENARIO_NAME);
		jsystem.addTest("testParameterization1", "ScenarioParameterizationTest", true);
		jsystem.addTest("testParameterization2", "ScenarioParameterizationTest", true);
		
		ScenarioUtils.createAndCleanScenario(jsystem,CHILD2_LEVEL1_SCENARIO_NAME);
		jsystem.addTest("testParameterization1", "ScenarioParameterizationTest", true);
		jsystem.addTest("testParameterization2", "ScenarioParameterizationTest", true);
		jsystem.addTest("testParameterizationFail", "ScenarioParameterizationTest", true);
		jsystem.addTest("testParameterizationAssert", "ScenarioParameterizationTest", true);
		
		ScenarioUtils.createAndCleanScenario(jsystem,PARENT_SCENARIO_NAME);
		jsystem.addTest("testRunPropertyIsEmpty", "ScenarioParameterizationTest", true);
		jsystem.addTest(CHILD1_LEVEL1_SCENARIO_NAME,JSystem.SCENARIO, true);
		jsystem.addTest(CHILD2_LEVEL1_SCENARIO_NAME,JSystem.SCENARIO, true);		
		jsystem.addTest(CHILD1_LEVEL1_SCENARIO_NAME,JSystem.SCENARIO, true);
		jsystem.addTest("testParameterization1", "ScenarioParameterizationTest", true);
		jsystem.addTest("testParameterization2", "ScenarioParameterizationTest", true);		
	}

}
