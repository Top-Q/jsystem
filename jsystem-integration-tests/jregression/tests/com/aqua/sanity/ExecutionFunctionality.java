package com.aqua.sanity;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.fixture.RootFixture;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * Test execution issues stop on execution start, stop on execution end,
 * pause and more.
 * @author guy.arieli
 *
 */
public class ExecutionFunctionality extends JSysTestCaseOld {
	public ExecutionFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * 1. create a scenario with 2 tests that will execute for 10 sec each.
	 * 2. play the scenario and stop it imidiatly.
	 * 3. wait for the run end.
	 * 4. check that 1 test run and it fails.
	 * 
	 */
	@TestProperties(name = "5.2.7.4 Run test and stop it in the middle")
	public void testStopImidiate() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 2 tests that will execute for 10 sec each");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		
		report.step("play the scenario and stop it imidiatly");
		jsystem.play();
		Thread.sleep(2000);
		jsystem.stop();
		jsystem.waitForRunEnd();
		
		report.step("check that 1 test run and it fails");
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.checkNumberOfTestsPass(0);
	}
	
	/**
	 * 1. create a scenario with 2 tests that will execute for 10 sec each.
	 * 2. play the scenario and stop it once (graceful).
	 * 3. wait for the run end.
	 * 4. repeat 1-3 again
	 * 4. check that 2 test run and they fail.
	 */
	@TestProperties(name = "Run a test and stop it gracefully")
	public void testGracefulStop() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 2 tests that will execute for 10 sec each");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic",2, true);//uses the version with parameter number of test to add	
		
		report.step("play the scenario and stop it imidiatly");
		jsystem.play();
		Thread.sleep(3000);
		jsystem.gracefulStop();
		jsystem.waitForRunEnd();
		jsystem.play();
		Thread.sleep(3000);
		jsystem.gracefulStop();
		jsystem.waitForRunEnd();
		
		report.step("check that 2 test run and they fail");
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(0);
		
	}
	/**
	 * 1. create a scenario with 4 tests that will execute for 10 sec each.
	 * 2. play the scenario and stop it imidiatly.
	 * 3. wait for the run end.
	 * 4.Start the scenrio again
	 * 5.Wait for it to end
	 * 4. check that 4 tests, have bee activated(Scanrio Started from the begining)
	 * 
	 */
	@TestProperties(name = "5.2.7.6 Run test and stop and start it again")
	public void testThatAfterStopScenarioStartfromTheBegining() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 4 tests that will execute for 10 sec each");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic",3, true);
		report.step("play the scenario and stop it imidiatly");
		jsystem.play();
		Thread.sleep(2000);
		jsystem.stop();
		jsystem.waitForRunEnd();
		jsystem.initReporters();
		report.step("Run again and check that all tests are activated");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(4);
		jsystem.checkNumberOfTestsPass(4);

	}
	/**
	 * 1. create a scenario with 2 tests that will execute for 10 sec each.
	 * 2. play the scenario.
	 * 3. wait for 18 sec (over all execution time should be 20 sec) then stop.
	 * 3. wait for the run end.
	 * 4. check that 2 tests run and only one passed.
	 * 
	 */
	@TestProperties(name = "5.2.7.5 Run test and stop it in the end of the scenario")	
	public void testStopScenarioEnd() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 2 tests that will execute for 10 sec each");
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic",2, true);
		
		report.step("play the scenario");
		jsystem.play();
		
		report.step("wait for 18 sec (over all execution time should be 20 sec) then stop");
		sleep(18000);
		report.report("\n\n\n---------------------------------------");
		report.report("jsystem == null? "+(jsystem == null));
		report.report("\n\n\n---------------------------------------");
		jsystem.stop();
		jsystem.waitForRunEnd();
		
		report.step("check that 2 tests run and only one passed");
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(1);
	}
	
	/**
	 * Test that stop is functional in repeat mode
	 * 1. create a scenario with 2 tests that will execute for 10 sec each.
	 * 2. Set repeat mode to enable
	 * 3. run the scenario and stop it after 35 sec (2'nd round,midle of the second test).
	 * 4. check that 4 tests were executed and 3 passed.
	 */
	@TestProperties(name = "5.2.7.14 Stop in a scenario that is been repeated")	
	public void testStopInRepeateMode() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 2 tests that will execute for 10 sec each");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testReportFor10Sec", "GenericBasic",2, true);
		
		report.step("Set repeate mode to enable");
		jsystem.setReapit(true);
		
		report.step("run the scenario and stop it after 35 sec (2'nd round,midle of the second test)");
		jsystem.play();
		
		/*
		 * wait for three tests to run, and then stop.
		 */
		for(int i = 0; i < 3; i++){
			jsystem.waitForTestToEnd();
		}
		
		jsystem.stop();
		jsystem.waitForRunEnd();
		jsystem.setReapit(false);
		
		report.step("check that 4 tests were executed and 3 passed");
		jsystem.checkNumberOfTestExecuted(4);
		jsystem.checkNumberOfTestsPass(3);
		
	}
	
	/**
	 * Check the stop feature in drop every test running mode:
	 * 1. backup the jsystem.properties file
	 * 2. set the run mode to 2 (drop every test)
	 * 3. create a scenario with 2 tests that will execute for 10 sec each.
	 * 4. run the scenario and stop it after 12 sec (midle of the second test).
	 * 5. check that 2 tests were executed and 1 passed.
	 * 6. restore the original jsystem.properties
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.7.11 Run a scenarion in mode 2 - JVM per test")
	public void testStopInDropEveryTestMode() throws Exception{
		report.step("backup the jsystem.properties file");
		backupJSystemProperties();
		
		report.step("set the run mode to 2 (drop every test)");
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_TEST.toString());
		jsystem.launch();
		
		report.step("create a scenario with 2 tests that will execute for 10 sec each");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic",2, true);
		
		report.step("run the scenario and stop it after 15 sec (midle of the second test)");
		jsystem.play();
		jsystem.waitForTestToEnd();
		sleep(2000);
		jsystem.stop();
		jsystem.waitForRunEnd();
		
		report.step("check that 2 tests were executed and 1 passed");
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(1);
		
		report.step("restore the original jsystem.properties");
		restoreJSystemProperties();
	}
	@TestProperties(name = "5.2.7.7-8 Pause a scenario")
	public void testPause() throws Exception{
		jsystem.launch();
		
		report.step("create a scenario with 3 tests that will execute for 10 sec each");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		jsystem.addTest("testReportFor10Sec", "GenericBasic", true);
		
		report.step("run the scenario");
		jsystem.play();
		
		sleep(3000);
		
		for (int i = 0; i < 5; i++){
			jsystem.playPause();
			sleep(3000);
			jsystem.play();
			sleep(1000);
		}
		jsystem.waitForRunEnd();
		
		report.step("check that 3 tests were executed and 3 passed");
		jsystem.checkNumberOfTestExecuted(3);
		jsystem.checkNumberOfTestsPass(3);
		
	}
}
