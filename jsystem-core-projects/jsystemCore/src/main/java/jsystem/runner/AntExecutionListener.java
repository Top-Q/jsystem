/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner;

import java.io.File;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.utils.StringUtils;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * Listener that should be added to ant execution commandline when executing
 * scenario using ant interpreter directly. The listener perform some
 * initialization operations which are required for proper execution of the
 * scenario.
 * 
 * @author gderazon
 */
public class AntExecutionListener implements BuildListener {

	@Override
	public void buildStarted(BuildEvent arg0) {
		// Updating jsystem.properties file
		String testsClassesDir = System.getProperty(RunningProperties.SCENARIO_BASE);
		if (!StringUtils.isEmpty(testsClassesDir)) {
			File sourceFolder = new File(testsClassesDir);
			JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, testsClassesDir);

			sourceFolder = new File(sourceFolder.getParent(), "tests");
			if (sourceFolder.exists()) {
				// Ant project structure
				JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER,
						sourceFolder.getAbsolutePath());
				JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER,
						sourceFolder.getAbsolutePath());

			} else {
				// ITAI: Maven project structure
				final File classFolder = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER));
				final File testsSourceFolder = new File(classFolder.getParentFile().getParentFile(), "src/main/java");
				JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER,
						testsSourceFolder.getAbsolutePath());
				
				final File resourcesSourceFolder = new File(classFolder.getParentFile().getParentFile(), "src/main/resources");
				JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER,
						resourcesSourceFolder.getAbsolutePath());


			}
		}
		String sutFile = System.getProperty(FrameworkOptions.USED_SUT_FILE.getString());
		if (!StringUtils.isEmpty(sutFile)) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sutFile);
		}
		// it is important to remove the property from system map otherwise,
		// change sut event won' work.
		System.getProperties().remove(FrameworkOptions.USED_SUT_FILE.getString());
		// resetting RunProperties
		RunProperties.getInstance().resetRunProperties();

		// loading scenario.
		String scenarioName = System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME);
		try {
			Scenario s = ScenariosManager.getInstance().getScenario(scenarioName);
			ScenariosManager.getInstance().setCurrentScenario(s);
		} catch (Exception e) {
			throw new RuntimeException("Failed loading scenario " + scenarioName, e);
		}
	}

	@Override
	public void buildFinished(BuildEvent arg0) {
		// ITAI: This is the place to notify that the execution ended. it fixed
		// bug
		// 3525124:last 'scenario as test' in not shown in report in cli mode
		ListenerstManager.getInstance().endRun();

		// give the reporters thread some time to write the report.
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
	}

	@Override
	public void messageLogged(BuildEvent arg0) {
	}

	@Override
	public void targetFinished(BuildEvent arg0) {
	}

	@Override
	public void targetStarted(BuildEvent arg0) {
	}

	@Override
	public void taskFinished(BuildEvent arg0) {
	}

	@Override
	public void taskStarted(BuildEvent arg0) {
	}
}
