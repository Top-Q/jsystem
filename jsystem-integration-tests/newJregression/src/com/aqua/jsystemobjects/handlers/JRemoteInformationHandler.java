package com.aqua.jsystemobjects.handlers;

import java.io.File;
import java.util.Properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.utils.FileUtils;

/**
 * 
 * @author Dan Hirsch
 *	
 * this class is used to get remote application information such as jsystem.properties file
 * values of the remote project etc.
 */
public class JRemoteInformationHandler extends BaseHandler {
	
	public String getJsystemPropertyValueForKey(String key) throws Exception{
		jemmySupport.report("jsystem.properties file path is: "+JSystemProperties.getInstance().getPreferencesFile().getAbsolutePath());
		Properties p = FileUtils.loadPropertiesFromFile(JSystemProperties.getInstance().getPreferencesFile().getAbsolutePath());
		jemmySupport.report("and value for the key "+key+" is: "+p.getProperty(key));
		String value = null;
		if(p.getProperty(key) == null){
			value = "null";
		}
		else{
			value = p.getProperty(key);
		}
		return value;
	}
	
	/**
	 * get the number of ROOT test for given Scenario
	 * 
	 * @param scenarioName	the scenario to check 
	 * @return	the amount of root tests in the scenario
	 * @throws Exception
	 */
	public int getNumOfRootTestsForScenario(String scenarioName) throws Exception{
		String fullName = ScenarioHelpers.addScenarioHeader(scenarioName);
		Scenario scenario = ScenariosManager.getInstance().getScenario(fullName);
		return scenario.getRootTests().size();
	}
	
	public String getReportDir() throws Exception {
		return System.getProperty("user.dir") + File.separator + JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER)
				+ File.separator + "current" + File.separator + "reports.0.xml";
	}
}
