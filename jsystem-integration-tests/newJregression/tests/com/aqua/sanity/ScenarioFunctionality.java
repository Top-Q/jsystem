package com.aqua.sanity;

import junit.framework.Assert;

import org.junit.Test;

import com.aqua.analyzers.BooleanAnalyzer;
import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.jsystemobjects.TestType;

/**
 * @author Dan Hirsch & Itai
 * 
 */
public class ScenarioFunctionality extends JSysTestCase4UseExistingServer {

	@Test
	public void checkPreventAddingScenarioToItself() throws Throwable {
		scenarioClient.cleanScenario("checkPreventAddingScenarioToItselfScnario");
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		scenarioClient.addTest("checkPreventAddingScenarioToItselfScnario", TestType.SCENARIO.getType());
		boolean isWarningOpened = applicationClient.checkIfWarningDialogOpenedAndCloseIt();
		analyzer.setTestAgainstObject(isWarningOpened);
		analyzer.analyze(new BooleanAnalyzer(true));
	}

	/**
	 * <b>Save As Test</b><br>
	 * <li>Create scenario s</li> <li>Set value of one of the test parameters to
	 * v</li>< <li>Save scenario</li> <li>Set scenario s files to read only</li>
	 * <li>Set value of one of the test parameters to v'</li> <li>Save scenario
	 * s as s'</li> <li>Assert that scenario s' was loaded</li> <li>Assert that
	 * the value is v'</li> <li>Load scenario s</li> <li>Assert that the value
	 * is v</li>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveAs() throws Exception {
		report.step("Creating sub scenario");
		final String subScenarioName = "sonScenario";
		scenarioClient.cleanScenario(subScenarioName);
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		applicationClient.saveScenario();
		
		
		report.step("Creating new scenario");
		final String originalScenarioName = "saveAsScenarioOriginal";
		scenarioClient.cleanScenario(originalScenarioName);
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		scenarioClient.addTest("testWithParameters", "SimpleTests");
		scenarioClient.addTest(subScenarioName, "scenarios");

		final String expectedOriginalValue = "original";
		scenarioClient.setTestParameter(4, "General", "str", expectedOriginalValue, false);
		applicationClient.saveScenario();

		report.step("Setting scenario files to read only");
		if (!applicationClient.setScenarioFilesReadable(originalScenarioName, false)) {
			report.report("Failed to change scenario files permissions", 2);
		}
		applicationClient.refresh();
		report.step("Changing parameter value");
		final String expectedNewValue = "new";
		scenarioClient.setTestParameter(4, "General", "str", expectedNewValue, false);

		report.step("Saving scenario as new name");
		scenarioClient.selectTestRow(0);
		final String newScenarioName = "saveAsScenarioNew";
		applicationClient.saveScenarioAs(newScenarioName);
		report.report("Asserting that the new scenario was loaded");
		final String currentScenario = scenarioClient.getCurrentRootScenarioName();
		Assert.assertEquals("The new scenario was not loaded", "scenarios/" + newScenarioName, currentScenario);
		String actualValue = scenarioClient.getTestParameter(4, "General", "str");
		report.report("Asserting that the parameter value is the one setted before the 'save as' operation");
		Assert.assertEquals(expectedNewValue, actualValue);
		reporterClient.initReporters();
		applicationClient.play();
		reporterClient.checkNumberOfTestsPass(7);
		report.step("Loading orignal scenario");
		scenarioClient.openScenario(originalScenarioName);
		actualValue = scenarioClient.getTestParameter(4, "General", "str");
		Assert.assertEquals(expectedOriginalValue, actualValue);

	}

}
