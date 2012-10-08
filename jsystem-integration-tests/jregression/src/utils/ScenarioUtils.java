package utils;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

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
		scenarioName = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(scenarioName);
		jsystemClient.selectSenario(scenarioName);
		jsystemClient.cleanCurrentScenario();
		jsystemClient.createScenario(scenarioName);
		report().report("Scenario-"+scenarioName+" was cleared");
	}
	@SuppressWarnings("deprecation")
	public static void createAndCleanScenario(JScenarioClient scenarioClient,String scenarioName) throws Exception {
		scenarioName = ScenarioModelUtils.getScenarioNameRelativeToScenariosFolder(scenarioName);
		scenarioClient.selectScenario(scenarioName);
		scenarioClient.cleanCurrentScenario();
		scenarioClient.createScenario(scenarioName);
		report().report("Scenario-"+scenarioName+" was cleared");
	}
	
	/**
	 * Returns element value
	 * 
	 * @param elem
	 *            element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	public static String findScenarioInPath(String path) {
		int fromLocation = path.lastIndexOf("/") + 1;
		int toLocation = path.indexOf(".xml");
		return path.substring(fromLocation, toLocation);
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
