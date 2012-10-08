/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.scenarionamehook;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.StringUtils;

/**
 * 
 * @author gderazon
 */
public class ScenarioNameHookManager {
	
	static public class HookData {
		public String projectName;
		public String scenarioId;
	}
	
	/**
	 * Returns scenario name hook class instance.
	 * If Scenario name hook is not defined, returns null. 
	 */
	public static ScenarioNameHook getHookClass() throws Exception {
		String className = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SCENARIO_NAME_HOOK);
		if (StringUtils.isEmpty(className)){
			return null;
		}
		
		Object nameHook = 
			LoadersManager.getInstance().getLoader().loadClass(className).newInstance();
		
		if (!(nameHook instanceof ScenarioNameHook)){
			throw new Exception("Scenario name hook must be an instance of ScenarioNameHook interface");
		}
		
		return (ScenarioNameHook)nameHook;
	}
	
	public static HookData getNextScenarioData() throws Exception {
		ScenarioNameHook hook = getHookClass();
		if (hook == null){
			throw new Exception("name hook not defined");
		}
		String scenarioId = hook.getScenarioId();
		String projectName = hook.getProjectName();
		HookData data = new HookData();
		data.projectName = projectName;
		data.scenarioId = scenarioId;
		return data;
	}
	
	public static String getScenarioNameForHookData(HookData data,String baseName) {
		if (baseName.endsWith(".xml")){
			return baseName.substring(0,baseName.length()-4) + data.scenarioId + ".xml";
		}
		
		return baseName+data.scenarioId;
	}
	
	public static Scenario createScenarioWithNameHook(String name) throws Exception {
		ScenarioNameHook hook = getHookClass();
		if (hook == null){
			throw new Exception("name hook not defined");
		}
		String scenarioId = hook.getScenarioId();
		String projectName = hook.getProjectName();
		Scenario s = new Scenario(ScenariosManager.getInstance().getScenariosDirectoryFiles(),name+scenarioId,scenarioId,projectName);
		return s;
	}	
	
	public static void saveScenarioAsWithNameHook(Scenario scenario, String scenarioNameWithHook,HookData data) throws Exception {
		scenario.save(scenarioNameWithHook);
		scenario.setProjectName(data.projectName);
		scenario.setExternalId(data.scenarioId);
		scenario.save();
	}	
}
