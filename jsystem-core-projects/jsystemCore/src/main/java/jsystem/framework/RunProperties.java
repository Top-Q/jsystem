/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.common.CommonResources;
import jsystem.framework.report.Summary;
import jsystem.utils.FileUtils;

/**
 * This class gives a singleton service for properties that should be saved
 * during a run execution of few tests. Tests can share information between
 * them self using this services. The service is done using a file called
 * .run.properties
 * 
 * @author guy.arieli
 */
public class RunProperties {
	private static Logger log = Logger.getLogger(RunProperties.class.getName());
	private static RunProperties rp = null;

	private RunProperties() {
		// singleton
		log.info("create run properties at :"+runPropertiesFile.getAbsolutePath());
	}

	public static RunProperties getInstance() {
		if (rp == null) {
			rp = new RunProperties();
		}
		return rp;
	}

	/**
	 * The properties file
	 */
	private File runPropertiesFile = new File(CommonResources.RUN_PROPERTIES_FILE_NAME);

	/**
	 * Deletes .run.properties file.
	 * DO NOT SYNCHRONIZE THIS METHOD - COULD CAUSE DEADLOCKS WITH SUMMARY CLASS!!!
	 */
	public void resetRunProperties() {
		log.info("Reset the Run properties file");
		if (!runPropertiesFile.delete() && runPropertiesFile.exists()){
			log.warning("Failed deleting .run.properties file");
		}
		Summary.getInstance().initPublishValues();
	}
	
	/**
	 * Get run property. Can be set by different test. and valid for the all
	 * run.
	 * 
	 * @param key
	 *            the property name
	 * @return the property value
	 * @throws IOException 
	 */
	public synchronized String getRunProperty(String key) throws IOException {
		Properties p = loadProperties();
		return p.getProperty(key);
	}

	/**
	 * Set run property. will be valid for the all run
	 * 
	 * @param key
	 *            the property name
	 * @param value
	 *            the property value
	 * @throws IOException 
	 */
	public synchronized void setRunProperty(String key, String value) throws IOException {
		Properties p = loadProperties();
		p.setProperty(key, value);
		saveProperties(p);
	}
	
	/**
	 * Remove run property
	 * 
	 * @param key
	 *            the property name
	 * @throws IOException 
	 */
	public synchronized void removeRunProperty(String key) throws IOException {
		Properties p = loadProperties();
		p.remove(key);
		saveProperties(p);
	}

	/**
	 * Get all the properties of the run
	 * 
	 * @return all the properties
	 * @throws IOException 
	 */
	public Properties getRunProperties() throws IOException {
		return loadProperties();
	}
	
	/**
	 */
	private synchronized Properties loadProperties() throws IOException{
		Properties p = new Properties();
		if (runPropertiesFile.getAbsoluteFile().exists()) {
			p = FileUtils.loadPropertiesFromFile(runPropertiesFile.getAbsolutePath());
			log.log(Level.CONFIG,"load run properties at :"+runPropertiesFile.getAbsolutePath());
		}else{
			log.log(Level.CONFIG,"the run properties doesn't exist,return new Properties object");
		}
		return p;
	}
	
	/**
	 */
	private synchronized void saveProperties(Properties props) throws IOException{
		FileUtils.savePropertiesToFile(props, runPropertiesFile.getAbsolutePath());
	}

}
