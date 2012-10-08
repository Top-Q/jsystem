package com.aqua.sanity;


import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.TestProperties;
import jsystem.framework.analyzer.AnalyzerException;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

/**
 * 
 * @author Itai.Agmon
 * 
 */
public class TestsTreeFunctionality extends JSysTestCase4UseExistingServer {

	/**
	 * Tests that documentation of test and scenario is shown correctly in the
	 * information view. <br>
	 * <li>Select test in the test tree</li> <li>
	 * Assert that the information view shows the test documentation.</li>
	 * 
	 * <li>Create new scenario</li> <li>
	 * Add user documentation</li> <li>Select scenario in the tests tree</li>
	 * <li>Assert that the information view shows the scenario documentation.</li>
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Test building block information view", paramsInclude = { "" })
	public void testBuildingBlockInfoView() throws Exception {
		testTestDocumentation();
		testScenarioDocumentation();
	}

	/**
	 * Test user documentation
	 * 
	 * @throws Exception
	 * @throws AnalyzerException
	 */
	private void testTestDocumentation() throws Exception, AnalyzerException {
		report.step("Testing test documentation");
		testsTreeClient.selectBuildingBlock("testWithDocumentation", "SimpleTests");
		final String bbInfo = testsTreeClient.getCurrentBuildingBlockInformation();

		// This is not exactly the text of the documentation. We are adding
		// spaces because the HTML edit component changes the string a little
		// bit.
		final String expectedTestDocumentation = "This     is the documentation of test testWithDocumentations  ";
		analyzer.setTestAgainstObject(bbInfo.replaceAll("\n", ""));
		analyzer.analyze(new FindText(expectedTestDocumentation));
	}

	/**
	 * Tests that the tests tree search works and supports OR, AND operators.
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Test test tree search box", paramsInclude = { "" })
	public void testSearchBox() throws Exception {
		report.report("Testing tests tree search box");
		testsTreeClient.search("warn");
		testsTreeClient.analyze(new NumberCompare(compareOption.EQUAL, 3, 0));

		testsTreeClient.search("that");
		testsTreeClient.analyze(new NumberCompare(compareOption.EQUAL, 4, 0));

		testsTreeClient.search("that AND warn");
		testsTreeClient.analyze(new NumberCompare(compareOption.EQUAL, 1, 0));

		testsTreeClient.search("that OR warn OR testWithDocumentation");
		testsTreeClient.analyze(new NumberCompare(compareOption.EQUAL, 7, 0));
	
		//TODO Arabic solution to the search box problem at the testScenarioDocumentation
		testsTreeClient.search("");

	}

	/**
	 * Tests scenario user documentation
	 */
	private void testScenarioDocumentation() throws Exception {
		report.step("Testing scenario documentation");
		final String subScenarioName = "scenarioWithUserDoc";
		scenarioClient.createScenario(subScenarioName);
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		final String userDoc = "scenario expected documentation";
		scenarioClient.setTestUserDocumentation(0, userDoc);
		applicationClient.saveScenario();
		
		testsTreeClient.selectBuildingBlock("scenarioWithUserDoc", "scenarios");

		final String bbInfo = testsTreeClient.getCurrentBuildingBlockInformation();
		analyzer.setTestAgainstObject(bbInfo.replaceAll("\n", ""));
		// This is not exactly the text of the documentation. We are adding
		// spaces because the HTML edit component changes the string a little
		// bit.
		analyzer.analyze(new FindText("scenario     expected documentation  "));

	}

}
