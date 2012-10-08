package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class NumOfTestsToAddSpinnerFunctionality extends JSysTestCaseOld {
	public NumOfTestsToAddSpinnerFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	/**
	 * Tests number of test to add JSpinner
	 * 
	 * 1. Add one test 20 times
	 * 2. Add one test 10 times
	 * 3. Assert that the test appears 30 times in the scenario
	 * @throws Exception
	 */
	@TestProperties(name = "test num of tests to be added to the scenario tree")
	public void testNumOfTestsToAddSpinner() throws Exception {
		jsystem.launch();
		
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		report.report("Adding test 20 times");
		jsystem.addTest("testShouldPass", "GenericBasic", 20, true);
		report.report("Adding test 10 times");
		jsystem.addTest("testShouldPass", "GenericBasic", 10, true);

		// Assert number of tests
		analyzer.setTestAgainstObject(jsystem.getNumOfTestsInScenario());
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 30, 0));
	}

}
