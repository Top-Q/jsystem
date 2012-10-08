package com.aqua.fixtures;

import java.io.File;
import java.io.IOException;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobjects.JSystemEnvController;

public class CreateEnvFixture extends Fixture {
	public void setUp() throws Exception {
		JSystemEnvController envController = (JSystemEnvController) system.getSystemObject("envController");
		envController.buildRunnerEnv();
		final File jsystemRegressionBaseTestFolder = new File(new File(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_SOURCE_FOLDER)).getParentFile(), "resources/newJregressionBaseTests");
		if (!jsystemRegressionBaseTestFolder.exists()) {
			throw new IOException("Folder: " + jsystemRegressionBaseTestFolder.getAbsolutePath() + " is not exists.");
		}
		envController.setRunnerEnvTestFolder(jsystemRegressionBaseTestFolder.getAbsolutePath(), "sut.xml");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
