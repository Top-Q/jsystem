package com.aqua.sanity;

import java.io.File;
import java.io.FileFilter;

import org.junit.Assert;

import utils.ScenarioUtils;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * Test for the jsystem.properties functionalities.
 * 
 * @author guy.arieli
 * 
 */
public class JSystemPropertiesFunctionality extends JSysTestCaseOld {

	public JSystemPropertiesFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		backupJSystemProperties();
	}

	public void tearDown() throws Exception {
		if (jsystem != null) {
			restoreJSystemProperties();
		}
		super.tearDown();
	}

	/**
	 * check that the repeat.enable property work properly. When set the runner
	 * should be opened when the checkbox of the repeat checked. 1. set the
	 * repeat.enable to true and then launch the jsystem. 2. check the checkbox
	 * is set. 3. create a scenario with 2 tests that will execute for 10 sec
	 * each. 4. play the scenario and after 30 sec change the repeate to
	 * disable. 5. check that 4 tests were executed.
	 */
	@TestProperties(name = "5.1.2 test jsystem.properties repeat mode")
	public void testRepeateEnable() throws Exception {
		report.step("set the repeate.enable to true and then launch the jsystem");
		jsystem.setJSystemProperty(FrameworkOptions.REPEAT_ENABLE, "true");

		jsystem.launch();
		report.step("check the checkbox is set");
		jsystem.checkIsRepeatSet(true);

		report.step("create a scenario with 2 tests that will execute for 10 sec each");
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testThatRunFor10Sec", "GenericBasic", 2, true);

		report.step("play the scenario and after 30 sec change the repeate to disable");
		jsystem.play();
		sleep(30000);
		jsystem.setReapit(false);
		jsystem.waitForRunEnd();

		report.step("check that 4 tests were executed");
		jsystem.checkNumberOfTestExecuted(4);
		jsystem.checkNumberOfTestsPass(4);

	}

	/**
	 * Test the htmlLogFolder property. 1. set the htmlLogFolder to log2 and
	 * then launch the jsystem. 2. create a scenario with 2 tests play the
	 * scenario and wait for execution end. 3. check that the new folder was
	 * created and that the report was generated in this folder.
	 * 
	 */
	@TestProperties(name = "5.1.1 test jsystem.properties log directoy")
	public void testLogFolder() throws Exception {
		report.step("set the htmlLogFolder to log2, delete it and then launch the jsystem");
		File logDir = new File(jsystem.getUserDir() + File.separator + "log2", "current");
		if (logDir.exists()) {
			FileUtils.deltree(logDir.getParentFile());
		}
		if (!logDir.mkdirs()) {
			throw new Exception("didn't succeed in creating all needed dirs under log2 from testLogFolder");
		}

		jsystem.setJSystemProperty(FrameworkOptions.LOG_FOLDER, "log2");

		jsystem.launch();
		jsystem.initReporters(); // must init for Log folder to be updated!!!

		report.step("create a scenario with 2 tests play the scenario and wait for execution end");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic", 2, true);

		jsystem.play();
		jsystem.waitForRunEnd();

		report.step("check that 2 tests were executed");
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);

		report.step("check that the new folder was created and that the report was generated in this folder");
		report.step("Log dir content" + StringUtils.stringArrayToSet(logDir.list()));
		assertTrue("log dir wasn't found", logDir.exists());
		assertTrue("test_1 folder wasn't found", new File(logDir, "test_1").exists());
	}

	/**
	 * Test the zip disable property 1. set the zip option to be enable and then
	 * launch the jsystem. 2. create a scenario with 2 tests. 3. play the
	 * scenario and wait for execution end. 4. check a single zip file was
	 * created. 5. repeate all the process with zip file set to disable and
	 * check no file were found.
	 * 
	 */
	@TestProperties(name = "5.2.14.3 test jsystem.properties disable log zipping")
	public void testZipDisable() throws Exception {
		report.step("set the zip option to be enable and then launch the jsystem");

		File oldLogDir = new File(jsystem.getUserDir() + File.separator + "log", "old");
		FileUtils.deltree(oldLogDir);
		// manual creation of old log dir.
		oldLogDir.mkdirs();
		jsystem.setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE, "false");
		jsystem.launch(false);
		report.step("create a scenario with 2 tests");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic", 2, true);

		report.step("play the scenario and wait for execution end");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.exit();// when closed, runner is expected to create a log zip
		report.step("check a single zip file was created");

		FileFilter zipFilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".zip") && pathname.isFile();
			}
		};

		File[] listOfZip = oldLogDir.listFiles(zipFilter);

		report.step("Verify that one zip file exists");
		analyzer.setTestAgainstObject(listOfZip.length);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 1, 0));

		jsystem = envController.getJSystemEnv();

		report.step("Delete old log directory");

		FileUtils.deltree(oldLogDir);
		oldLogDir.mkdirs();

		listOfZip = oldLogDir.listFiles(zipFilter);

		report.step("Verify that the old log directory has no zip files");
		analyzer.setTestAgainstObject(listOfZip.length);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 0, 0));

		report.step("set the zip option to be disabled and then launch the jsystem");
		jsystem.setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE, "true");
		jsystem.launch();

		report.step("create a scenario with 2 tests");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic", 2, true);

		report.step("play the scenario and wait for execution end");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.exit();
		listOfZip = oldLogDir.listFiles(zipFilter);

		report.step("Verify that no zip files exist");
		analyzer.setTestAgainstObject(listOfZip.length);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 0, 0));
	}

	/**
	 * Test the stdout ability.<br>
	 * 1. set the STDOUT_FILE_NAME to some temporary file, and then launch the
	 * jsystem.<br>
	 * 2. create a scenario with 2 tests play the scenario and wait for
	 * execution end.<br>
	 * 3. check that the new text file was created and that the file contains
	 * the stdout log.
	 * 
	 */
	@TestProperties(name = "5.1.3 Test output file creation")
	public void testStdoutProperty() throws Exception {
		File f = File.createTempFile("JSystemStdout", ".dat");
		report.report("Verifying that output file doesn't exist");
		f.delete();
		if (f.exists()) {
			throw new Exception("the file exists although it has been deleted");
		}
		report.step("set the stdout option to be enable and then launch the jsystem");
		report.report("Setting stdout file to be in " + f.getCanonicalPath());
		jsystem.setJSystemProperty(FrameworkOptions.STDOUT_FILE_NAME, f.getCanonicalPath());
		jsystem.launch();
		report.step("create a scenario with 2 tests");
		jsystem.cleanCurrentScenario();
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		report.step("play the scenario and wait for execution end");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.exit();
		report.step("check if an stdout txt file was created");
		Assert.assertTrue("File " + f.getName() + " is not exists!", f.exists());
		analyzeOutputFile(FileUtils.read(f));
	}

	/**
	 * receives a text to compare with default output file content and checks it
	 * holds all relevant text.
	 * 
	 * @param text
	 * @throws Exception
	 */
	private void analyzeOutputFile(String text) throws Exception {

		String[] substr = { "jsystem", "INFO", "Path", "Total time", "End time", "end test" };
		boolean[] status = new boolean[substr.length];
		for (int v = 0; v < substr.length; v++) {
			status[v] = false;
		}
		for (int v = 0; v < substr.length; v++) {
			substr[v] = substr[v].replace("\r", "");
			int index1 = text.indexOf(substr[v]);
			if (index1 != -1) {
				status[v] = true;
			}
		}
		for (int v = 0; v < substr.length; v++) {
			if (status[v] == false) {
				throw new Exception("the file was not recognize as stdout file");
			}
		}
	}
}
