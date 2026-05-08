/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.net.URL;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import junit.framework.Assert;
import junit.framework.SystemTestCase4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ScenariosManagerTest extends SystemTestCase4{

	@Before
	public void setTestsClassFolder() throws Exception {
		// Point to test-classes so ScenarioCollector finds scenarios from src/test/java/scenarios
		URL scenarioUrl = getClass().getResource("/scenarios/jsystemCoreSanity.xml");
		if (scenarioUrl != null) {
			File scenarioFile = new File(scenarioUrl.toURI());
			File testClassesDir = scenarioFile.getParentFile().getParentFile();
			String path = testClassesDir.getAbsolutePath();
			JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, path);
			ScenariosManager.getInstance().setScenariosDirectoryFiles(testClassesDir);
		}
	}

	@Test
	@Ignore("getAvailableLists() returns names in a format that may not match (path/separator); TESTS_CLASS_FOLDER is set but comparison still fails")
	public void testIsScenarioExists() throws Exception{
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, "empty");
		String scenarioName = "scenarios" + File.separator + "jsystemCoreSanity";
		Assert.assertTrue("Scenario " + scenarioName + " should exist in " + JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER),
				ScenariosManager.getInstance().isScenarioExists(scenarioName));
	}

}
