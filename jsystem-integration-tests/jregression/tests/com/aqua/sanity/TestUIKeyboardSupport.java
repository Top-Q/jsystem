package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.TestProperties;
import jsystem.treeui.teststable.ScenarioTreeKeyHandler.Keys;
import junit.framework.Assert;
import analyzers.StringCompareAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class TestUIKeyboardSupport extends JSysTestCaseOld {

	private final int numOfTests = 3;

	public TestUIKeyboardSupport() {
		setFixture(CreateEnvFixtureOld.class);
	}

	/**
	 * Creates scenario with <code>numOfTests</code> tests, deletes <code>numOfTests</code>-1 of them using the "Delete" key
	 * and asserts that only one test left.
	 * 
	 * @params.include numOfTests
	 */
	@TestProperties(name = "Delete tests from the scenarios tree using the \"Delete\" key")
	public void testDeleteTestsFromScenarioWithKey() throws Exception {
		jsystem.launch();

		// Adds tests to scenario
		report.step("create a scenario with " + numOfTests + " tests");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic",numOfTests, true);
		report.report("Number of tests in scenario : " + jsystem.getNumOfTestsInScenario());

		// Deletes tests from scenario
		for (int i = 0, testNum = 0; i < numOfTests - 1; i++) {
			testNum = numOfTests-i;
			report.report("Select test "+testNum);
			jsystem.selectTest(testNum);
			sleep(1 * 1000);
			jsystem.pressKey(Keys.DELETE);
			sleep(2000);
		}

		sleep(1 * 1000);
		
		int testsLeftInScenario = jsystem.getNumOfTestsInScenario();
		
		analyzer.setTestAgainstObject(testsLeftInScenario);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 1, 0));

	}

	/**
	 * 
	 * Maps <code>numOfTests</code> from scenario and unmap them
	 * 
	 * @params.include numOfTests
	 */
	@TestProperties(name = "Maps tests from the scenarios tree using the \"Space\" key")
	public void testMapTestsWithKey() throws Exception {
		jsystem.launch();

		// Adds tests to scenario
		report.step("create a scenario with " + numOfTests + " tests");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		report.step("adding "+numOfTests+" tests to scenario");
		jsystem.addTest("testShouldPass", "GenericBasic",numOfTests, true);

		report.step("use the space key to unmap the tests and tests that no tests are mapped");
		// unmaps tests in scenario
		for (int i = 1; i < numOfTests + 1; i++) {
			sleep(1 * 1000);
			jsystem.selectTest(i);
			sleep(1 * 1000);
			report.report("Selecting test #" + i + " with the \"Space\" key");
			jsystem.pressKey(Keys.SPACE);
			sleep(1 * 1000);
		}
		String mappedTests = jsystem.getMapedTestsInCurrentScenario();
//		Assert.assertEquals("", mappedTests);
		analyzer.setTestAgainstObject(mappedTests);
		analyzer.analyze(new StringCompareAnalyzer(""));

		// Maps tests in scenario
		report.step("use the space key to map the tests and test that "+numOfTests+" tests, are mapped");
		for (int i = 1; i < numOfTests + 1; i++) {
			sleep(1 * 1000);
			jsystem.selectTest(i);
			sleep(1 * 1000);
			report.report("Selecting test #" + i + " with the \"Space\" key");
			jsystem.pressKey(Keys.SPACE);
			sleep(1 * 1000);
		}
		mappedTests = jsystem.getMapedTestsInCurrentScenario();
		int numberOfMappedTests = mappedTests.trim().split(",").length;
		int expectedNumberOfMapeedTests = numOfTests;
		analyzer.setTestAgainstObject(numberOfMappedTests);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, expectedNumberOfMapeedTests, 0));
	}
	

	public int getNumOfTests() {
		return numOfTests;
	}
}
