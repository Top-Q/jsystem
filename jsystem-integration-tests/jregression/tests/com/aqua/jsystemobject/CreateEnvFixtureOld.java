package com.aqua.jsystemobject;


import java.io.File;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;

public class CreateEnvFixtureOld extends Fixture {
	
	
	public void setUp() throws Exception{
		JSystemEnvControllerOld envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		envController.buildRunnerEnv();
		final File jsystemRegressionBaseTestFolder = new File(new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER)).getParentFile(),"resources/jsystemRegressionBaseTests");
		envController.setRunnerEnvTestFolder(jsystemRegressionBaseTestFolder.getAbsolutePath(), "sut.xml");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
