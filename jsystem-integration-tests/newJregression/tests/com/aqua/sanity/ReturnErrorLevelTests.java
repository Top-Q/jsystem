package com.aqua.sanity;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.fixture.FixtureManager;

import org.junit.After;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.fixtures.CreateEnvFixture;
import com.aqua.fixtures.NewActivateRunnerFixture;

public class ReturnErrorLevelTests extends JSysTestCase4UseExistingServer {

	/**
	 * Test the runner exists with error level 101 when one test fails
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testFailureErrorLevel() throws Throwable {
		report.step("Creating and running scenario with error tests");
		scenarioClient.cleanScenario("FailureErrorLevel");
		scenarioClient.addTest("testThatFail", "SimpleTests", 1);
		scenarioClient.addTest("testThatWarns", "SimpleTests", 1);
		scenarioClient.addTest("testThatPass", "SimpleTests", 2);
		applicationClient.play();

		report.step("Exiting application and asserting error level");
		analyzer.setTestAgainstObject(applicationClient.closeApp());
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 101, 0));
	}

	/**
	 * Test the runner exists with error level 102 when one test has warning
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWarningErrorLevel() throws Exception {
		report.step("Creating and running scenario with warning tests");
		scenarioClient.cleanScenario("WarningErrorLevel");
		scenarioClient.addTest("testThatWarns", "SimpleTests", 1);
		scenarioClient.addTest("testThatPass", "SimpleTests", 2);
		applicationClient.play();

		report.step("Exiting application and asserting error level");
		analyzer.setTestAgainstObject(applicationClient.closeApp());
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 102, 0));
	}

	/**
	 * Test the runner exists with error level 0 when all tests pass
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessErrorLevel() throws Exception {
		report.step("Creating and running scenario with only pass tests");
		scenarioClient.cleanScenario("SuccessErrorLevel");
		scenarioClient.addTest("testThatPass", "SimpleTests", 3);
		applicationClient.play();

		report.step("Exiting application and asserting error level");
		analyzer.setTestAgainstObject(applicationClient.closeApp());
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 0, 0));
	}

	@After
	public void relaunchRunner() throws Throwable {
		FixtureManager.getInstance().goTo(CreateEnvFixture.class.getName());
		FixtureManager.getInstance().goTo(NewActivateRunnerFixture.class.getName());
	}
}
