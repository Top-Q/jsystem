/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.ScenariosManager;

/**
 * The class target is to generate the JSystem application title. when using the
 * generateTitle, the method will return the current title for the Jsystem app.
 * 
 * @author uri.koaz
 * 
 */
public class JsystemAppTitle {

	/**
	 * current project path.
	 */
	private String testClassFolderName;

	/**
	 * current scenario
	 */
	private String currentScenarioName;

	/**
	 * current running test or Idle id nor running any test.
	 */
	private String currentRunTest = "Idle";

	private static JsystemAppTitle instance;

	private JsystemAppTitle() {

	}

	public static JsystemAppTitle getInstance() {
		if (instance == null) {
			instance = new JsystemAppTitle();
		}

		return instance;
	}

	/**
	 * 
	 * @return String contains the JSystem title.
	 */
	public String generateTitle() {
		testClassFolderName = JSystemProperties.getCurrentTestsPath();
		currentScenarioName = ScenariosManager.getInstance().getCurrentScenario().getName();
		// return "JSystem - " + testClassFolderName + " - " +
		// currentScenarioName + " - " + currentRunTest;

		// TODO: complete the task
		return "JSystem - " + testClassFolderName;
	}

	public String getCurrentRunTest() {
		return currentRunTest;
	}

	public void setCurrentRunTest(String currentRunTest) {
		this.currentRunTest = currentRunTest;
	}

	public String getCurrentScenarioName() {
		return currentScenarioName;
	}

	public void setCurrentScenarioName(String currentScenarioName) {
		this.currentScenarioName = currentScenarioName;
	}

	public String getTestClassFolderName() {
		return testClassFolderName;
	}

	public void setTestClassFolderName(String testClassFolderName) {
		this.testClassFolderName = testClassFolderName;
	}
}
