package com.aqua.fixtures;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobjects.ClientHandlerType;
import com.aqua.jsystemobjects.JSystemEnvController;
import com.aqua.jsystemobjects.clients.JApplicationClient;

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
		envController.startXmlRpcServer();
		JApplicationClient applicationClient = (JApplicationClient) envController.getSystemClient(ClientHandlerType.APPLICATION);
		applicationClient.keepAlive();
		applicationClient.setJSystemProperty(FrameworkOptions.RUN_MODE, ""+RUN_MODE);
		applicationClient.setInitialJsystemProperties();
		applicationClient.launch();
		report.report("application instance id="+applicationClient );
	}
	
	public void tearDown() throws Exception {
		JSystemEnvController envController = (JSystemEnvController)system.getSystemObject("envController");
		JApplicationClient applicationClient = (JApplicationClient)envController.getSystemClient(ClientHandlerType.APPLICATION);
		applicationClient.closeApp();
		envController.kill();
	}
}
