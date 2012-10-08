package com.aqua.sanity;

import jsystem.framework.TestProperties;
import junit.framework.Assert;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

/**
 * Tests the user documentation feature.
 * 
 * @author itai_a
 * 
 */
public class ScenariosUserDocumentationTests extends JSysTestCase4UseExistingServer {

	/**
	 * <b>Tests user documentation</b><br>
	 * <li>Create sub scenario</li><br>
	 * <li>Adds user doc to the sub scenario</li><br>
	 * <li>Create root scenario</li><br>
	 * <li>Add the sub scenario three times to the root scenario</li><br>
	 * <li>Asserts the sub scenarios user documentation</li><br>
	 * <li>Marks one of the sub scenarios as test</li><br>
	 * <li>Sets different user doc to each one of the sub scenarios</li><br>
	 * <li>Loads the sub scenario and asserts the original user documentation</li>
	 * <br>
	 * <li>Loads the root scenario and asserts the user documentation</li><br>
	 */
	@Test
	@TestProperties(paramsInclude = { "" })
	public void scenarioUserDocSanity() throws Exception {
		report.step("Creating sub scenario");
		final String subScenarioName = "docSanitySubScenario";
		scenarioClient.createScenario(subScenarioName);
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		final String sonOriginalExpectedDoc = "Son original expected documantation";
		scenarioClient.setTestUserDocumentation(0, sonOriginalExpectedDoc);
		applicationClient.saveScenario();

		report.step("Adding sub scenario to root scenario");
		final String rootScenarioName = "docSanityRootScenario";
		scenarioClient.createScenario(rootScenarioName);
		scenarioClient.selectTestRow(0);
		
		scenarioClient.addTest(subScenarioName, "scenarios", 3);
		scenarioClient.markScenarioAsTest(1, true);

		report.step("Changing the sub scenarios documentation");
		final String expectedSonUserDocPrefix = "Sub scenario user doc number ";

		String actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(1);
		Assert.assertEquals(sonOriginalExpectedDoc, actualSubScenarioDoc);
		scenarioClient.setTestUserDocumentation(1, expectedSonUserDocPrefix + "1");

		actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(2);
		Assert.assertEquals(sonOriginalExpectedDoc, actualSubScenarioDoc);
		scenarioClient.setTestUserDocumentation(2, expectedSonUserDocPrefix + "2");

		actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(6);
		Assert.assertEquals(sonOriginalExpectedDoc, actualSubScenarioDoc);
		scenarioClient.setTestUserDocumentation(6, expectedSonUserDocPrefix + "3");

		applicationClient.saveScenario();

		report.step("Loading the sub scenario and asserting documentation");
		scenarioClient.openScenario(subScenarioName);
		final String actualUserDoc = scenarioClient.getTestUserDocumentation(0);
		Assert.assertEquals(sonOriginalExpectedDoc, actualUserDoc);

		report.step("Loading root scenario and asserting sub scenarios documentation");
		scenarioClient.openScenario(rootScenarioName);
		actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(1);
		Assert.assertEquals(expectedSonUserDocPrefix + "1", actualSubScenarioDoc);
		actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(2);
		Assert.assertEquals(expectedSonUserDocPrefix + "2", actualSubScenarioDoc);
		actualSubScenarioDoc = scenarioClient.getTestUserDocumentation(6);
		Assert.assertEquals(expectedSonUserDocPrefix + "3", actualSubScenarioDoc);

		report.report("All passed sucessfully");

	}

}
