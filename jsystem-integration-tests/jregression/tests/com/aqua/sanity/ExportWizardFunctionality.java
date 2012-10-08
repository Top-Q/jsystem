package com.aqua.sanity;

import java.io.File;
import java.util.ArrayList;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.utils.FileUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * A group of tests as part of the jsystem sanity. This class contain tests to
 * test the reporter functionalities.
 * 
 * @author guy.arieli
 * 
 */
public class ExportWizardFunctionality extends JSysTestCaseOld {

	String[] expectedFilesList;
	String[] expectedDirsList;

	public ExportWizardFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
	}

	/**
	 * launch the runner, and activate the export wizard check that the wizard
	 * is activated successfully.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.12.1 Save a project using the export Wizard")
	public void testActivateExportWizardSimple() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", false, false, false, false, false, false, false);
	}

	/**
	 * launch the runner, activate export wizard with only compiledOutPut
	 * checked. then verify that all relevant files are in the dir
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "3.1.2 run export wizard with compiledOutPut checked")
	public void testActivateExportWizard_extractCompiledOutpu() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", true, true, false, true, true, true, false);
		jsystem.extract("reg_env-1.0.zip");
		expectedFilesList = new String[] {};
		expectedDirsList = new String[] { "project" };
		verifyAllFilesInExtractDir(expectedFilesList, expectedDirsList);
	}

	/**
	 * launch the runner, activate export wizard with only runner and log
	 * checked. then verify that all relevant files are in the dir
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "run export wizard with runner and log checked")
	public void testActivateExportWizard_extractRunner() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", false, false, true, true, false, false, false);
		jsystem.extract("reg_env-1.0.zip");
		expectedFilesList = new String[] { "jsystem.properties", "run", "run.bat", "runBase.bat", "runBase" };
		expectedDirsList = new String[] { "lib", "thirdparty" };
		verifyAllFilesInExtractDir(expectedFilesList, expectedDirsList);
	}

	/**
	 * launch the runner, activate export wizard with only compiledOutPut and
	 * systemObjects and exportRunner and exportLog checked. then verify that
	 * all relevant files are in the dir
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "run export wizard with compiledOutPut and exportRunner and exportLog checked")
	public void testActivateExportWizard_CompiledOutput_Runner() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", true, true, true, true, true, true, false);
		jsystem.extract("reg_env-1.0.zip");
		expectedFilesList = new String[] { "jsystem.properties", "run", "run.bat" };
		expectedDirsList = new String[] { "lib", "project", "thirdparty" };
		verifyAllFilesInExtractDir(expectedFilesList, expectedDirsList);
	}

	/**
	 * launch the runner, activate export wizard with only compiledOut and
	 * systemObject and exportRunner and exportJdk checked. then verify that all
	 * relevant files are in the dir
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "run export wizard with compiledOut and exportRunner and exportJdk checked")
	public void testActivateExportWizard_CompiledOutput_Runner_Jdk() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", true, true, true, true, true, true, true);
		jsystem.extract("reg_env-1.0.zip");
		expectedFilesList = new String[] { "jsystem.properties", "run", "run.bat" };
		expectedDirsList = new String[] { "lib", "project", "thirdparty" };
		verifyAllFilesInExtractDir(expectedFilesList, expectedDirsList);
	}

	/**
	 * checks that the specified expected files(logs) are in the directory
	 * internalLog under the extract directory path.
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "test export wizard with logs")
	public void testActivateExportWizard_WithLog() throws Exception {
		jsystem.launch();
		report.step("Activate wizard");
		jsystem.activateExportWizard(".", false, false, false, true, false, false, false);
		jsystem.extract("reg_env-1.0.zip");
		expectedFilesList = new String[] { "jsystem.properties", "jsystem0.log", "debug.properties" };
		// this function does the analysis and updates the reporter report as to
		// success or failure
		verifyExpectedFilesInDir("internalLog", expectedFilesList);
	}

	/**
	 * checks that all expected files and directories are inside the directory
	 * to which files were extracted.
	 * 
	 * @param expectedFilesList
	 * @param expectedDirsList
	 * @throws Exception
	 */
	private void verifyAllFilesInExtractDir(String[] expectedFilesList, String[] expectedDirsList) throws Exception {
		File f = new File(jsystem.getUserDir(), "tmpExtractDir");
		String[] filesList = FileUtils.listFiles(f);
		String[] dirsList = FileUtils.listDirs(f);

		ArrayList<String> filesArrayList = new ArrayList<String>();
		ArrayList<String> dirsArrayList = new ArrayList<String>();

		for (int i = 0; i < filesList.length; i++) {
			filesArrayList.add(filesList[i]);
		}
		for (int i = 0; i < dirsList.length; i++) {
			dirsArrayList.add(dirsList[i]);
		}

		for (int i = 0; i < expectedFilesList.length; i++) {
			if (filesArrayList.contains(expectedFilesList[i])) {
				report.report(expectedFilesList[i] + " file was found");
			} else {
				report.report(expectedFilesList[i] + " file was not found", false);
			}
		}

		for (int i = 0; i < expectedDirsList.length; i++) {
			if (dirsArrayList.contains(expectedDirsList[i])) {
				report.report(expectedDirsList[i] + " directory was found");
			} else {
				report.report(expectedDirsList[i] + " directory was not found", false);
			}
		}
	}

	/**
	 * receives a dir to search inside for expected files, and checks that all
	 * expected files are found. a file found is reported as found, a file not
	 * found is reported as not found and test filed
	 * 
	 * @param dir
	 * @param expectedFilesList
	 * @throws Exception
	 */
	private void verifyExpectedFilesInDir(String dir, String[] expectedFilesList) throws Exception {
		// create a file with path to extract dir on server/tmpExtractDir
		File f = new File(jsystem.getUserDir(), "tmpExtractDir");
		// make f hold a reference to path/tmpExtractDir/dir
		f = new File(f, dir);
		// check if the f path exist
		if (!f.exists()) {
			throw new Exception("File not found " + f.getPath());
		}
		// list all files in f
		String[] filesList = FileUtils.listFiles(f);

		ArrayList<String> filesArrayList = new ArrayList<String>();

		for (int i = 0; i < filesList.length; i++) {
			filesArrayList.add(filesList[i]);
		}
		// if the files in the f path contain the expected files report found
		// else report not found and fail on test
		for (int i = 0; i < expectedFilesList.length; i++) {
			if (filesArrayList.contains(expectedFilesList[i])) {
				report.report(expectedFilesList[i] + " file was found");
			} else {
				report.report(expectedFilesList[i] + " file was not found", false);
			}
		}
	}

	public void tearDown() throws Exception {
		File f = new File(jsystem.getUserDir(), "tmpExtractDir");
		f.exists();
		FileUtils.deltree(f);
		if (f.exists()) {
			report.report("export wizard folder exists after deletion", Reporter.WARNING);
		}
		f = new File(jsystem.getUserDir(), "reg_env-1.0.zip");
		f.delete();
		if (f.exists()) {
			report.report("export wizard zip exists after deletion", Reporter.WARNING);
			f.deleteOnExit();
		}
		super.tearDown();
	}
}
