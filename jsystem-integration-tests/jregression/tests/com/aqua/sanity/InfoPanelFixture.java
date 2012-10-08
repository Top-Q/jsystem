package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobject.ActivateRunnerFixture;
import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemEnvControllerOld;

public class InfoPanelFixture extends Fixture {

	private JSystemClient jsystem;

	public InfoPanelFixture() {
		setParentFixture(ActivateRunnerFixture.class);
	}

	public void setUp() throws Exception {
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld) system.getSystemObject("envControllerOld");
		jsystem = envController.getJSystemEnv();
		createBasicParameterizationScenario();
	}

	private void createBasicParameterizationScenario() throws Exception {
		ScenarioUtils.createAndCleanScenario(jsystem, "infopanel");
		jsystem.addTest("testFileParameter", "AdvancedTestParametersTest", true);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
	}

	public void tearDown() throws Exception {
		jsystem.exit();
		super.tearDown();
	}

}
