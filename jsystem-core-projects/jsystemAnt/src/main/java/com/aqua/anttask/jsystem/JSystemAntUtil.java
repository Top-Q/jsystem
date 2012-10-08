/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.common.CommonResources;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.utils.FileUtils;

import org.apache.tools.ant.ProjectComponent;

/**
 * Utility for handling different ant utilities, such as: <br>
 * 1) parameters loading from scenario properties file<br>
 * 2) reference replacement<br>
 * 3) checking of enabled tests<br>
 * <br>
 * Used by ant flow elements
 * 
 * @author Nizan Freedman
 *
 */
public class JSystemAntUtil {

	static Logger log = Logger.getLogger(JSystemAntUtil.class.getName());
	
	/**
	 * Get all properties relevant to given unique ID and scenario String
	 * @param scenarioString	The full path String of all scenarios
	 * @param fullUuid	the Full unique ID
	 * @return	the relevant properties for given parameters
	 */
	public static Properties getPropertiesValue(String scenarioString, String fullUuid){
		try {
			return ScenarioHelpers.getTestPropertiesFromScenarioProps(scenarioString, fullUuid);
		} catch (Exception e1) {
			log.log(Level.SEVERE, "Error trying to load test parameters from properties file",e1);
			return new Properties();
		}
	}
	
	/**
	 * Get a parameter value from the given properties, and replace reference parameters
	 * 
	 * @param parameterName		the parameter Name to search (key)
	 * @param parameterValue	the current parameter value
	 * @param properties		the properties to take the value form
	 * @return	The parameter value after all evaluations 
	 */
	public static String getParameterValue(String parameterName, String parameterValue, Properties properties){
		String toReturn = parameterValue;
		String tmp = properties.getProperty(parameterName, parameterValue);
		if (tmp != null){
			try {
				tmp = ParametersManager.replaceAllReferenceValues(tmp, ParameterType.STRING);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error trying to replace reference parameters for input: " + tmp,e);
			}
			toReturn = tmp;
		}
		log.log(Level.INFO,parameterName + " = " + toReturn);
		return toReturn;
	}
	
	/**
	 * Get a parameter value from the scenario properties file and replace reference parameters
	 * 
	 * @param scenarioString	The full path String of all scenarios
	 * @param fullUuid	the Full unique ID
	 * @param parameterName		the parameter Name to search (key)
	 * @param parameterValue	the current parameter value
	 * @return	The parameter value after all evaluations 
	 */
	public static String getParameterValue(String scenarioString, String fullUuid,String parameterName, String parameterValue){
		Properties properties = getPropertiesValue(scenarioString, fullUuid);
		return getParameterValue(parameterName, parameterValue, properties);
	}
	
	/**
	 * Insert all project properties to ant properties file
	 * 
	 * @param component	the component to get the project properties from
	 */
	@SuppressWarnings("unchecked")
	public static void propertiesToFile(ProjectComponent component) {
		try {	
			Enumeration<String> keys = component.getProject().getProperties().keys();
			Properties propsToFile = new Properties();
			while (keys.hasMoreElements()){
				String key  = keys.nextElement();
				propsToFile.put(key,component.getProject().getProperties().get(key));
			}
			
			// Adding status property
			boolean lastTestFailed = ListenerstManager.getInstance().getLastTestFailed();
			propsToFile.setProperty(CommonResources.TEST_STATUS, !lastTestFailed + "");
			
			FileUtils.savePropertiesToFile(propsToFile,CommonResources.ANT_INTERNAL_PROPERTIES_FILE);
		}catch (Exception e){
			component.log("Failed writing " + CommonResources.ANT_INTERNAL_PROPERTIES_FILE + " file" + e.getMessage());
		}
	}
	
	/**
	 * Check if the container with the given Unique ID has any enabled tests in it
	 * 
	 * @param fullUuid	the container full unique id
	 * @return	True if there are any enabled tests, False for none
	 */
	public static boolean doesContainerHaveEnabledTests(String fullUuid){
		Scenario s = ScenariosManager.getInstance().getCurrentScenario();
		
		while (fullUuid.startsWith(".")){
			fullUuid = fullUuid.substring(1);
		}
		
		JTestContainer container = s.getContainerByFullId(fullUuid);
		if (container!=null && (container.getEnabledTests().size() == 0)){
			return false;
		}
		return true;
	}

	
}
