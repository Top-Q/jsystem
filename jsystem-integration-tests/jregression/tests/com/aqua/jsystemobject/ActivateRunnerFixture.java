package com.aqua.jsystemobject;


import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.fixture.Fixture;

/**
 * Fixture that activates the runner in the setup and kills
 * it in the tearDown
 * @author goland
 */
public class ActivateRunnerFixture extends Fixture {
	public static RunMode RUN_MODE = RunMode.DROP_EVERY_RUN;
	public ActivateRunnerFixture(){
		setParentFixture(CreateEnvFixtureOld.class);
	}
	
	public void setUp() throws Exception{
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		JSystemClient jsystem = envController.getJSystemEnv();
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, ""+RUN_MODE);
		jsystem.setInitialJsystemProperties();
		jsystem.launch();
		JSystemEnvControllerOld.setUseExistingServer(true);
		report.report("jsystem instance id="+jsystem );
	}
	
	public void tearDown() throws Exception {
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		JSystemEnvControllerOld.setUseExistingServer(false);
		envController.kill();
	}
}
