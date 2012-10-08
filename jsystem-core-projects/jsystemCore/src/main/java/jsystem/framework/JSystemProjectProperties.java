/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Handles jsystem project properties.
 */
public class JSystemProjectProperties {
	private static Logger log = Logger.getLogger(JSystemProjectProperties.class.getName());
	/**
	 * JSystem project properties file name
	 */
	public static final String JSYSTEM_PROJECT_PROPERTIES_FILE_NAME = "jsystemProject.properties";

	private static JSystemProjectProperties rp = null;

	private JSystemProjectProperties() {
	}

	public static JSystemProjectProperties getInstance() {
		if (rp == null) {
			rp = new JSystemProjectProperties();
		}
		return rp;
	}
	
	/**
	 */
	public String getProperty(String key) throws Exception {
		Properties p = loadProperties();
		return p.getProperty(key);
	}

	/**
	 */
	public void setProperty(String key, String value) throws Exception {
		Properties p = loadProperties();
		p.setProperty(key, value);
		saveProperties(p);
	}
	
	/**
	 */
	private Properties loadProperties() throws IOException{
		String testsClassesFolderName = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER);
		File projectProps = new File(testsClassesFolderName,JSYSTEM_PROJECT_PROPERTIES_FILE_NAME);
		Properties p = new Properties();
		if (projectProps.exists()) {
			FileInputStream inStream = new FileInputStream(projectProps);
			try {
				p.load(inStream);
			}finally{
				try{inStream.close();} catch(Exception e){};
			}
		}
		return p;
	}
	
	/**
	 */
	private void saveProperties(Properties props) throws IOException{
		//save to src
		String testsSrcFolderName = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_SOURCE_FOLDER);
		if (new File(testsSrcFolderName).exists()){
			File projectProps = new File(testsSrcFolderName,JSYSTEM_PROJECT_PROPERTIES_FILE_NAME);
			FileOutputStream out = new FileOutputStream(projectProps);
			try {
				props.store(out, null);
			}finally{
				try{out.close();}catch(Exception e){};
			};
		}else {
			log.warning(FrameworkOptions.TESTS_SOURCE_FOLDER.getString() + " points to a none existing folder.");
		}
		//save to classes
		String testsClassesFolderName = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER);
		File projectProps = new File(testsClassesFolderName,JSYSTEM_PROJECT_PROPERTIES_FILE_NAME);
		FileOutputStream out = new FileOutputStream(projectProps);
		try {
			props.store(out, null);
		}finally{
			try{out.close();}catch(Exception e){};
		};
	}

}
