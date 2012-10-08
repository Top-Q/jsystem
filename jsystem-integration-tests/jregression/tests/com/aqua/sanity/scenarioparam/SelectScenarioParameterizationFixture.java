package com.aqua.sanity.scenarioparam;


import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobject.ActivateRunnerFixture;
import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemEnvControllerOld;

public class SelectScenarioParameterizationFixture extends Fixture {
	public static final String PARENT_SCENARIO_NAME= "ScenarioParameterizationParent";
	public static final String CHILD1_LEVEL1_SCENARIO_NAME= "ScenarioParameterizationChild1Level1";
	public static final String CHILD2_LEVEL1_SCENARIO_NAME= "ScenarioParameterizationChild2Level1";
	
	private JSystemClient jsystem;
	public SelectScenarioParameterizationFixture(){
		setParentFixture(ActivateRunnerFixture.class);
	}
	
	public void setUp() throws Exception{
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		jsystem	= envController.getJSystemEnv();
		selectBasicParameterizationScenario();
	}
	
	private void selectBasicParameterizationScenario() throws Exception {
		jsystem.selectSenario(PARENT_SCENARIO_NAME);
	}

}
