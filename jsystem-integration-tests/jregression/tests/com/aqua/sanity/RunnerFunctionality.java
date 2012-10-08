package com.aqua.sanity;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import utils.ScenarioUtils;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * Test the functionality of the runner Things like refresh, change tests
 * directory, freeze and more.
 * 
 * @author guy.arieli
 * 
 */
public class RunnerFunctionality extends JSysTestCaseOld {
	private int repeatAmount = 3;

	public RunnerFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	/**
	 * 1. Create a scenario 2. add few tests to the scenario 3. backup the
	 * scenario to temp file 4. clear the scenario 5. copy the backup to the
	 * clean scenario (it now as the few tests) 6. refresh 7. run the scenario
	 * 8. check it run the scenario with the few tests.
	 * 
	 * @throws Exception
	 * @params.include
	 */
	@TestProperties(name = "5.2.2.4 test refresh functionlity")
	public void testRefresh() throws Exception {
		jsystem.launch();

		int numOfTests = 4;
		String refreshScenarioName = "refreshScenario";

		report.step("create a scenario with few steps");
		jsystem.createScenario(refreshScenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", numOfTests, true);
		jsystem.saveScenario();

		report.step("backup the scenario");
		final File refreshScenarioXml = new File(jsystem.getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER)
				+ File.separator + "scenarios" + File.separator + refreshScenarioName + ".xml");
		Assert.assertTrue("Scenario file was not found", refreshScenarioXml.exists());
		final File refreshScenarioProp = new File(jsystem.getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER)
				+ File.separator + "scenarios" + File.separator + refreshScenarioName + ".properties");
		Assert.assertTrue("Scenario file was not found", refreshScenarioProp.exists());

		File tempScenarioXml = new File(refreshScenarioXml.getParent(), "tempScen.xml");
		tempScenarioXml.delete();
		FileUtils.copyFile(refreshScenarioXml, tempScenarioXml);
		Assert.assertTrue("Temp Scenario file was not found", tempScenarioXml.exists());
		File tempScenarioProp = new File(refreshScenarioXml.getParent(), "tempScen.properties");
		tempScenarioProp.delete();
		FileUtils.copyFile(refreshScenarioProp, tempScenarioProp);
		Assert.assertTrue("Temp Scenario file was not found", tempScenarioProp.exists());

		report.step("clear the scenario");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.createScenario(refreshScenarioName);
		jsystem.saveScenario();
		if (!refreshScenarioXml.delete() || !refreshScenarioProp.delete()) {
			throw new IOException("Failed to delete scenario " + refreshScenarioName);
		}

		report.step("restore the scenario");
		FileUtils.copyFile(tempScenarioXml, refreshScenarioXml);
		FileUtils.copyFile(tempScenarioProp, refreshScenarioProp);

		report.step("refresh and run the scenario");
		jsystem.refresh();

		report.step("check that the restored scenario was executed");
		jsystem.checkNumberOfTestsExistInScenario(refreshScenarioName, numOfTests);

		tempScenarioXml.delete();
		tempScenarioProp.delete();
	}

	/**
	 * 1. Copy the classes folder to classes2 2. Crate scenario with 2 steps 3.
	 * Change the tests folder to classes2 4. Create a scenario with 4 tests 5.
	 * Execute the 4 tests scenario and check all passes 6. return to the
	 * classes folder run the 2 tests scenario and check all pass.
	 * 
	 * @throws Exception
	 * @params.include
	 */
	@TestProperties(name = "5.2.2.5 test change test folder functionlity")
	public void testChangeCurrentTestsFolder() throws Exception {
		// create tests folder from jsystem.properties
		File testsClassFolder = new File(jsystem.getJSystemProperty(FrameworkOptions.TESTS_CLASS_FOLDER));
		// create another tests folder
		File otherClassFolder = new File(testsClassFolder.getParentFile(), "classes2");

		if (otherClassFolder.exists()) {
			FileUtils.deltree(otherClassFolder);
		}

		// copy the tests directory to the other one, a copy called classes2
		FileUtils.copyDirectory(testsClassFolder, otherClassFolder);

		jsystem.launch();

		report.step("create a scenario with few steps");
		// jsystem.cleanCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, "classesScenario");
		jsystem.addTest("testShouldPass", "GenericBasic", 2, true);

		jsystem.changeTestDir(otherClassFolder.getAbsolutePath(), "", false);
		System.out.println("otherClassFolder is , " + otherClassFolder.getAbsolutePath().toString());
		sleep(10000); // wait for changing project (or "TestDir")
		report.report("we are after change dir");
		ScenarioUtils.createAndCleanScenario(jsystem, "classesScenario");
		report.report("We after cleanScenario");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		report.report("We add test 1");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		report.report("We add test 2");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		report.report("We add test 3");
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		report.report("We add test 4");

		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(4);
		jsystem.changeTestDir(testsClassFolder.getAbsolutePath(), "", false);
		sleep(10000); // wait for changing project (or "TestDir")
		jsystem.selectSenario("classesScenario");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(6);

		FileUtils.deltree(otherClassFolder);
	}

	/**
	 * 1. Create a scenario with tests that fail 2. Set the freeze on fail to
	 * true 3. Run the scenario and wait for the freeze dialog 4. set the freeze
	 * on fail to false 5. Run the scenario and wait for it to finish. 6. Check
	 * that 2 tests run and 0 passes.
	 * 
	 * @throws Exception
	 * @params.include
	 */
	@TestProperties(name = "5.2.7.15-16 test freeze on fail functionlity")
	public void testFreezeOnFail() throws Exception {
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, "" + getRunMode());
		jsystem.launch();

		report.step("create a scenario with few steps");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testFailWithError", "GenericBasic", true);

		jsystem.setFreezeOnFail(true);
		jsystem.play();

		jsystem.waitForFreezeDialog();

		jsystem.checkNumberOfTestExecuted(0); // should be 0?
		jsystem.checkNumberOfTestsPass(0);

		jsystem.setFreezeOnFail(false);

		jsystem.play();
		jsystem.waitForRunEnd();

		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(0);
	}

	/**
	 * 1. create scenario with 2 tests that run for 10 sec each (total 20 sec).
	 * 2. Run the scenario. 3. After 5 sec enable the repeat. 4. After 25 sec
	 * (~10 sec in the second round) disable the repeat. 5. Wait for run end and
	 * check 4 tests were executed successfully.
	 * 
	 * @throws Exception
	 * @params.include
	 */
	@TestProperties(name = "5.2.7.14 test repeat functionlity")
	public void testRepeate() throws Exception {
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, "" + getRunMode());
		jsystem.launch();
		report.step("create a scenario with few steps");
		// jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testThatRunFor10Sec", "GenericBasic", 2, true);

		jsystem.play();
		sleep(5000);
		jsystem.setReapit(true);

		jsystem.waitForRunEnd(3);
		jsystem.setReapit(false);
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2 * 4); // (tests)*(cycle repeats)
		jsystem.checkNumberOfTestsPass(2 * 4);
	}

	/**
	 * 1. Create scenario with 2 tests. 2. Set the repeate to enable and the
	 * amount to 3. 3. Play and wait for run end. 4. Check that 6 tests were
	 * executed.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.7.17 test repeat amount")
	public void testRepeateAmount() throws Exception {
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, "" + getRunMode());
		jsystem.launch();
		report.step("create a scenario with few steps");
		// jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", 2, true);
		jsystem.setReapit(true);
		jsystem.setRepAmount(getRepeatAmount());
		jsystem.play();
		jsystem.waitForRunEndUntilLeftRepeatAmountIs(0);
		jsystem.checkNumberOfTestExecuted(getRepeatAmount() * 2);
		jsystem.checkNumberOfTestsPass(getRepeatAmount() * 2);
	}

	/**
	 * Tests the exit menu item
	 * 
	 * @params.include
	 */
	public void testExitThroughMenu() throws Exception {
		jsystem.launch();
		// jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.exitThroughMenu(false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		try {
			jsystem.exitThroughMenu(true);
			assertTrue("Runner should exit ", false);
		} catch (Exception e) {
			report.report("runner Exited successfully");
			return;
		}
	}

	/**
	 */
	public void testRunInLoop() throws Exception {
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, "" + getRunMode());
		jsystem.launch();
		report.step("execute test that get the current test folder");
		ScenarioUtils.createAndCleanScenario(jsystem, "RunInLoop");
		jsystem.addTest("testReportFor10Sec", "GenericBasic", 3, true);
		report.report("Scenario was created");
		for (int i = 0; i < getRepeatAmount(); i++) {
			report.report("in iteration number " + i);
			jsystem.initReporters();
			report.report("Give the init reporter some time to clean logs ... ");
			Thread.sleep(10000);
			jsystem.play();
			Thread.sleep(3000);
			jsystem.playPause();
			Thread.sleep(2000);
			jsystem.play();
			jsystem.waitForRunEnd();
			report.report("give the reporter thread some time to write log.");
			Thread.sleep(3000);
			report.step("check that the test end successful and the report step was added");
			jsystem.checkNumberOfTestsPass(3);
		}
	}

	public int getRepeatAmount() {
		return repeatAmount;
	}

	public void setRepeatAmount(int repeatAmount) {
		this.repeatAmount = repeatAmount;
	}
}
