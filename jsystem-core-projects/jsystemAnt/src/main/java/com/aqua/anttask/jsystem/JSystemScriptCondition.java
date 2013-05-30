/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.types.optional.ScriptCondition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper for the Ant ScriptCondition to allow passing of parameters and
 * using of inner script file
 * 
 * @author Nizan, Itai
 * 
 */
public class JSystemScriptCondition extends ScriptCondition {

	Logger log = Logger.getLogger(JSystemScriptCondition.class.getName());

	String params;
	String uuid;
	String scenarioString;
	File tmpSrc;

	public void setFullUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setParentName(String name) {
		if (name.startsWith(".")) {
			name = name.substring(1);
		}
		scenarioString = name;
	}

	public String getParams() {
		return params;
	}

	/**
	 * The parameters String for the condition script (References will be
	 * replaced)
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * Check src file location and replace references
	 * 
	 * @param srcFile
	 */
	private void updateSrcFile(File srcFile) {
		// Searching for the script file in the classes/scenarios folder
		String srcStr = JSystemAntUtil.getParameterValue(scenarioString, uuid, "ScriptFile", srcFile.getAbsolutePath());
		srcFile = new File(srcStr);
		// If src file doesn't exist, check in runner/lib/scripts library
		File newSrc = null;
		if (!srcFile.exists()) { // User source or partial path
			final File destinationFolder = new File(System.getProperty("user.dir"), "scripts");
			if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
				if (!destinationFolder.mkdir()) {
					log.warning("Failed to create scripts destination folder");
					return;
				}
			}
			if (new File(destinationFolder,srcFile.getName()).exists()){
				//We already have the script in the destination. 
				newSrc = new File(destinationFolder,srcFile.getName());				
			}
			if (null == newSrc){
				newSrc = extractScriptFromClasspath("/com/aqua/anttask/jsystem/", srcFile.getName(), destinationFolder);
			}
			if (null == newSrc) {
				// Could not find the script in the classpath
				final File libDir = findLibDir();
				// We found the lib dir so we search for the jsystemAnt jar
				// inside
				final File antJarFile = findJSystemAntJar(libDir);
				if (antJarFile != null) {
					newSrc = extractScriptFromJar(antJarFile, srcFile.getName(), destinationFolder);
				}
				if (null == newSrc) {
					log.warning("Failed to find " + srcFile.getName() + " script file");
				}
			}

		}
		super.setSrc(newSrc);
	}

	/**
	 * Extracts the if script file from the JSystemAnt jar.
	 * 
	 * @param antJarFile
	 * @param scriptName
	 * @param destinationFolder
	 * @return the Location of the extracted script
	 */
	private File extractScriptFromJar(final File antJarFile, final String scriptName, final File destinationFolder) {
		File destination = new File(System.getProperty("user.dir"));
		if (!new File(destination, scriptName).exists()) { // if Script
															// file
															// doesn't
															// exist
			try { // Extract the script file from the jsystemAnt jar file
				log.info("Extract the script file from the jsystemAnt jar file to " + destination);
				FileUtils.extractOneZipFile("ifScriptCondition.js", antJarFile, destination);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Fail to locate script file for if execution: " + scriptName);
				throw new RuntimeException("Fail locating script file for if execution: " + scriptName);
			}
		}
		return new File(destination, scriptName);
	}

	/**
	 * Finds the jsystemAntJar in the lib folder.
	 * 
	 * @param libDir
	 *            Location of the lib folder
	 * @return jsystemAnt.jar or null if can't find file or folder is not exist
	 */
	private File findJSystemAntJar(final File libDir) {
		if (libDir == null || !libDir.isDirectory() || !libDir.exists()) {
			return null;
		}
		final File[] antJarFile = libDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// It's important that it will be search for the start with
				// because we need to support the Maven archetypes names
				if (name.startsWith("jsystemAnt")) {
					return true;
				}
				return false;
			}

		});

		if (antJarFile != null && antJarFile.length > 0 && antJarFile[0].exists()) {
			log.info("Ant Jar File was found here: " + antJarFile[0]);
			return antJarFile[0];
		}
		return null;
	}

	private File findLibDir() {
		String userDir = System.getProperty("user.dir");
		File libDir = null;

		// the ifScriptCondition javascript file is extracted from the
		// JSystemAnt file.
		// the Jar file is located in the runner directory.
		// if the user.dir points to jsystemApp it means we are running the
		// JRunner from the eclipse
		// rather than the batch file. in that case, we still need to fetch
		// the script from the runner
		// directory so we use the environment variable

		if (userDir.contains("jsystemApp")) {
			libDir = new File(System.getenv("RUNNER_ROOT"), "lib");
		} else {
			libDir = CommonResources.getLibDirectory();
		}
		return libDir;
	}

	private File extractScriptFromClasspath(final String packageName, final String scriptName, final File destination) {
		final InputStream inputStream = this.getClass().getResourceAsStream(packageName + scriptName);
		if (null == inputStream) {
			return null;
		}
		final File ifScriptCondition = new File(destination, scriptName);
		try {
			OutputStream outputStream = new FileOutputStream(ifScriptCondition);
			IOUtils.copy(inputStream, outputStream);

		} catch (IOException e) {
			return null;
		}
		return ifScriptCondition;

	}

	public void setSrc(File src) {
		this.tmpSrc = src;
	}

	public boolean eval() {
		updateSrcFile(tmpSrc);
		params = JSystemAntUtil.getParameterValue(scenarioString, uuid, "Parameters", params);
		return super.eval();
	}
}
