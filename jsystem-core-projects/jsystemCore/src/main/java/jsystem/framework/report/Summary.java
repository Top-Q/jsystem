/*
 * Created on Sep 19, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.RunProperties;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;

/**
 * A singleton that give a system summary properties service. Use setProperty to
 * save the property to summary.properties file, as well as to the
 * run.properties. setTempProperty will be save the property only to the
 * run.properties and will not be available on the next test run. Get
 * getProperties will merge the 2 properties (run into summary) were the
 * run.properties is stronger.
 * 
 * @author guy.arieli
 * 
 */
public class Summary {
//	Thread safe singleton instance
	private static final Summary summary_Instance = new Summary();
	
	private static Logger log = Logger.getLogger(Summary.class.getName());
	
	private static String SUMMARY_FILE_NAME = "summary.properties";
	
	public void initPublishValues(){
		setTempProperty("User", System.getProperty("user.name"));
		setTempProperty("Version", "unknown");
		String scenarioName = ScenariosManager.getInstance().getCurrentScenario().getName();
		setTempProperty("Scenario", ScenarioHelpers.removeScenarioHeader(scenarioName));
		try {
			setTempProperty("Station", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException unknownHostException) {
		}
		setTempProperty("Date", DateUtils.getDate());
	}

	/**
	 * Get an instance of the summary service 
	 * 
	 * @return a singleton of the summary service.
	 */
	public static Summary getInstance() {
		return summary_Instance;
	}

	/**
	 * Set a property (will be saved to the summary.properties as well as to the
	 * run.properties)
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 * @throws Exception
	 */
	public synchronized void setProperty(String key, String value) throws Exception {
		Properties properties = new Properties();
		if ((new File(SUMMARY_FILE_NAME)).exists()) {
			properties = FileUtils.loadPropertiesFromFile(SUMMARY_FILE_NAME);
		}
		properties.setProperty(key, value);
		FileUtils.savePropertiesToFile(properties, SUMMARY_FILE_NAME);
		RunProperties.getInstance().setRunProperty("summary." + key, value);
	}

	/**
	 * Set a property (will be save to the run.properties only)
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 */
	public void setTempProperty(String key, String value) {
		try {
			RunProperties.getInstance().setRunProperty("summary." + key, value);
		} catch (Exception exception) {
			log.log(Level.WARNING, 
					"Fail to save property key: " + key + ", value: " + value, 
					exception);
		}
	}

	/**
	 * 
	 * @return a merge of both the run.properties and the summary.properties.
	 */
	public synchronized Properties getProperties() {
		Properties properties = new Properties();
		File summaryFile = new File(SUMMARY_FILE_NAME);
		if (summaryFile.exists()) {
			try {
				properties = FileUtils.loadBeanPropertiesFromFile(SUMMARY_FILE_NAME);
			} catch (Exception exception) {
				log.log(Level.WARNING, "Fail to load summary properties", exception);
			}
		}
		try {
			Properties runProperties = RunProperties.getInstance().getRunProperties();
			Enumeration<Object> keys = runProperties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith("summary.")) {
					properties.put(key.substring("summary.".length()), runProperties.getProperty(key));
				}
			}
		} catch (Exception exception) {
			log.log(Level.WARNING, "Fail to load run properties", exception);
		}
		return properties;
	}

	/**
	 * Set the version of the SUT
	 * 
	 * @param version
	 *            the version of the SUT
	 */
	public void setVersion(String version){
		setTempProperty("Version", version);
	}
	
	/**
	 * get a summary property
	 * 
	 * @param key	the property key
	 * @return	the property value, if exists
	 */
	public Object getProperty(String key){
		return getProperties().getProperty(key);
	}
	
	/**
	 * clear Summary properties file and init only publish properties
	 */
	public synchronized void clearAllProperties(){
		try {
			FileUtils.savePropertiesToFile(new Properties(), SUMMARY_FILE_NAME);
		} catch (IOException ioException) {
			log.log(Level.WARNING, "Fail to reset Summary properties", ioException);
		}
		initPublishValues();
	}

}
