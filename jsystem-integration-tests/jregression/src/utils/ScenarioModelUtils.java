package utils;

import jsystem.framework.common.CommonResources;

public class ScenarioModelUtils {
	public static final String SCENARIO_HEADER = CommonResources.SCENARIOS_FOLDER_NAME + "/";

	/**
	 * cuts from scenario name the "scenarios" folder.
	 * For example if given scenario name is scenarios/default.xml,
	 * the method will return default.xml 
	 */
	public static String getScenarioNameRelativeToScenariosFolder(String scenarioName){
		if (scenarioName == null){
			throw new IllegalArgumentException("JTestContainer name is null");
		}
		
		if (scenarioName.indexOf(CommonResources.SCENARIOS_FOLDER_NAME) < 0){
			return scenarioName;
		}
		return scenarioName.substring(scenarioName.indexOf(SCENARIO_HEADER)+ SCENARIO_HEADER.length());
	}

}
