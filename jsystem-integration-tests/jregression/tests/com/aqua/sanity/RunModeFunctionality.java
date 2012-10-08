package com.aqua.sanity;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
/**
 * Test the functionality of the runner
 * Things like refresh, change tests directory,
 * freeze and more.
 * @author guy.arieli
 *
 */
public class RunModeFunctionality extends JSysTestCaseOld {
	public RunModeFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		backupJSystemProperties();
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		restoreJSystemProperties();
	}
	
	/**
	 * This tests is used to test the run mode 2
	 * JVM per test
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.7.11 Run tests in mode 2 -JVM per test")
	public void testRunModeNewJVMForEachTest() throws Exception {
		report.step("set run mode to 2");
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_TEST.toString());
		jsystem.launch();
		jsystem.selectSenario("runmode/runmodeRoot");
		jsystem.refresh();
		jsystem.setTestParameter(5, "General", "ExpectedCounterValue", "1",false);
		Thread.sleep(5000);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(4);

	}
	/**
	 * This tests is used to test the run mode 2
	 * JVM per Scenario
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.7.12 Run tests in mode 4 -JVM per scenario")
	public void testRunModeNewJVMForEachScenario() throws Exception {
		report.step("set run mode to 4");
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_SCENARIO.toString());
		jsystem.launch();
		jsystem.selectSenario("runmode/runmodeRoot");
		jsystem.refresh();
		jsystem.setTestParameter(5, "General", "ExpectedCounterValue", "2",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(4);
	}

}
