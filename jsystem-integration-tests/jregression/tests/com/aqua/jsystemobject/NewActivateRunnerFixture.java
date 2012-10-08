package com.aqua.jsystemobject;


import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.handlers.JServerHandlers;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.fixture.Fixture;

/**
 * Fixture that activates the runner in the setup and kills
 * it in the tearDown
 * @author goland
 */
public class NewActivateRunnerFixture extends Fixture {
	public static RunMode RUN_MODE = RunMode.DROP_EVERY_RUN;
	public NewActivateRunnerFixture(){
		setParentFixture(CreateEnvFixture.class);
	}
	
	public void setUp() throws Exception{
		JSystemEnvController envController = (JSystemEnvController)system.getSystemObject("envController");
		envController.startRemoteEnv();
		JApplicationClient applicationClient = (JApplicationClient) envController.getSystemClient(JServerHandlers.APPLICATION);
		applicationClient.setJSystemProperty(FrameworkOptions.RUN_MODE, ""+RUN_MODE);
		applicationClient.setInitialJsystemProperties();
		applicationClient.launch();
		envController.setUseExistingServer(true);
		report.report("application instance id="+applicationClient );
	}
	
	public void tearDown() throws Exception {
		JSystemEnvController envController = (JSystemEnvController)system.getSystemObject("envController");
		envController.setUseExistingServer(false);
		envController.kill();
	}
}
