package com.aqua.general;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.Scenario;
import utils.ScenarioModelUtils;

import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.clients.JScenarioClient;

public class ScenarioUtils {
	
	private static String manualScenariosFolder = "Manual scenarios";
	
	/**
	 * cleans the scenario tree and recreate a scenario in the name given
	 * @param jsystemClient
	 * @param scenarioName
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void createAndCleanScenario(JSystemClient jsystemClient,String scenarioName) throws Exception {
		System.out.println("the scenario name passed to createAndClean is "+scenarioName);
		StringBuffer sb = new StringBuffer(scenarioName);
		int startIdx = sb.indexOf(ScenarioModelUtils.SCENARIO_HEADER);
		if (startIdx> -1) {
			int endIdx = startIdx + ScenarioModelUtils.SCENARIO_HEADER.length();
			sb.delete(startIdx, endIdx);
			scenarioName = sb.toString();
		}
		jsystemClient.selectSenario(scenarioName);
		jsystemClient.cleanCurrentScenario();
		jsystemClient.createScenario(scenarioName);
		report().report("Scenario-"+scenarioName+" was cleared");
	}
	@SuppressWarnings("deprecation")
	public static void createAndCleanScenario(JScenarioClient scenarioClient,String scenarioName) throws Exception {
		System.out.println("the scenario name passed to createAndClean is "+scenarioName);
		StringBuffer sb = new StringBuffer(scenarioName);
		int startIdx = sb.indexOf(ScenarioModelUtils.SCENARIO_HEADER);
		if (startIdx> -1) {
			int endIdx = startIdx + ScenarioModelUtils.SCENARIO_HEADER.length();
			sb.delete(startIdx, endIdx);
			scenarioName = sb.toString();
		}
		scenarioClient.selectScenario(scenarioName);
		scenarioClient.cleanCurrentScenario();
		scenarioClient.createScenario(scenarioName);
		report().report("Scenario-"+scenarioName+" was cleared");
	}
	
		
	
	private static Reporter report() throws Exception {
		return ListenerstManager.getInstance();
	}
	public static String getManualScenariosFolder() {
		return manualScenariosFolder;
	}
	public static void setManualScenariosFolder(String manualScenariosFolder) {
		manualScenariosFolder = manualScenariosFolder;
	}
}
