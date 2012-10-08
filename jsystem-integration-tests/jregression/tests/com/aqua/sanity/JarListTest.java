package com.aqua.sanity;

import java.io.File;

import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;
import analyzers.JarListAnalyzeContent;
import analyzers.JarListAnalyzeSort;
import analyzers.StringCompareAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.general.ScenarioUtils;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class JarListTest extends JSysTestCaseOld {

	private final String[] jarList = { "ant.jar", "jemmy.jar", "jsystemCore.jar", "jsystemCommon.jar",
			"jsystemAgent.jar", "jsystemApp.jar", "jregression.jar", "jsystem-launcher.jar", "ant-jsystem.jar",
			"poi.jar", "mysql-connector-java-3.1.10-bin.jar", "junit.jar" };

	private String jarToFind = "jsystem";

	private String jarFilePath;

	private final String jarFileName = "svnant.jar";

	private String runnerOutDir;

	public JarListTest() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		jarFilePath = envController.getRegressionBaseTestsFolder() + File.separatorChar + "resources";
		runnerOutDir = envController.getRunnerOutDir();
	}

	public void tearDown() throws Exception {
		File fileToDelete = new File(runnerOutDir + File.separatorChar + "runnerout" + File.separatorChar + "runner"
				+ File.separatorChar + "lib" + File.separatorChar + jarFileName);
		fileToDelete.delete();
		fileToDelete = new File(jarFilePath + File.separatorChar + jarFileName);
		fileToDelete.delete();
		super.tearDown();
	}

	/**
	 * test the open jar list option in runner
	 * 
	 * @params.exclude jarToFind,jarFilePath,jarFileName
	 */
	public void testOpenJarList() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String txt = jsystem.openJarList();
		validateJarList(txt, 1);
	}

	/**
	 * test the sort jar list option in jar list frame
	 * 
	 * @params.exclude jarToFind,jarFilePath,jarFileName
	 */

	public void testSortJarList() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String txt = jsystem.sortJarList();
		validateJarList(txt, 2);
	}

	/**
	 * test the search option in jar list frame
	 * 
	 * @params.include
	 */
	public void testJarInList() throws Exception {
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String txt = jsystem.jarInList(jarToFind);
		validateJarList(txt, 3);
	}

	/**
	 * add new jar manually and check if the jar list was updated
	 * 
	 * @params.include
	 */
	public void testAddJarToList() throws Exception {
		String runnerOutDir = envController.getRunnerOutDir();
		File jarFile = new File(jarFilePath + File.separatorChar + jarFileName);
		jarFile.createNewFile();
		report.report("DEBUG >>> created the new file " + jarFile.getAbsolutePath());
		FileUtils.copyFile(jarFile.getAbsolutePath(), runnerOutDir + "runnerout" + File.separatorChar + "runner"
				+ File.separatorChar + "lib" + File.separatorChar + jarFileName);
		report.report("DEBUG >>> copied the new jar file to " + runnerOutDir + "runnerout" + File.separatorChar
				+ "runner" + File.separatorChar + "lib" + File.separatorChar + jarFileName);
		jsystem.launch();
		String txt = jsystem.openJarList();
		report.report("DEBUG >>> the jar list is: " + txt + "\n\nlooking for " + jarFileName + " in it!!!");
		validateJarList(txt, 4);
	}

	/**
	 * add new jar manually and check if the jar list was updated
	 * 
	 * @params.include
	 */
	public void testAddJarToBaseTestsList() throws Exception {
		String runnerOutDir = envController.getRunnerOutDir();
		File jarFile = new File(jarFilePath + "/" + jarFileName);
		jarFile.createNewFile();
		FileUtils.copyFile(jarFile.getAbsolutePath(), runnerOutDir + "/runnerout/testsProject/lib/" + jarFileName);
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String txt = jsystem.openJarList();
		report.report(txt);
		File fileToDelete = new File(runnerOutDir + "/runnerout/testsProject/lib/" + jarFileName);
		fileToDelete.delete();
		validateJarList(txt, 4);
	}

	private void validateJarList(String param, int test) throws Exception {
		switch (test) {
		case 1:
			analyzer.setTestAgainstObject(param);
			analyzer.analyze(new JarListAnalyzeContent(jarList));
			break;
		case 2: // after sort A to Z
			String[] params = param.trim().split(" +");
			analyzer.setTestAgainstObject(params);
			analyzer.analyze(new JarListAnalyzeSort(jarList));
			break;
		case 3:
			// since I am going to split with regex "<bold> " i want to remove
			// the
			// first regex to prevent a first String in array that is ""
			if (param.startsWith("<bold> ")) {
				param = param.substring("<bold> ".length() + 1);
			}
			String[] strArr = param.trim().split("<bold> ");

			for (String str : strArr) {
				if (str.contains(jarToFind)) {
					continue;
				} else {
					throw new Exception("a returned search parameter does not contain the jarToFind as substring");
				}
			}
			break;
		case 4:
			analyzer.setTestAgainstObject(param);
			analyzer.analyze(new StringCompareAnalyzer(jarFileName, StringCompareAnalyzer.TestOption.Contains));
			break;
		default:
			break;
		}

	}

}
