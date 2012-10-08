/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;
import org.apache.tools.ant.types.optional.ScriptCondition;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper for the Ant ScriptCondition to allow passing of parameters and using of inner script file 
 * 
 * @author Nizan
 *
 */
public class JSystemScriptCondition extends ScriptCondition{
	
	Logger log = Logger.getLogger(JSystemScriptCondition.class.getName());
	
	String params;
	String uuid;
	String scenarioString;
	File tmpSrc;

	public void setFullUuid(String uuid){
		this.uuid = uuid;
	}

	public void setParentName(String name){
		if (name.startsWith(".")){
			name = name.substring(1);
		}
		scenarioString = name;
	}

	
	public String getParams() {
		return params;
	}

	/**
	 * The parameters String for the condition script (References will be replaced)
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * Check src file location and replace references
	 * 
	 * @param src
	 */
	private void updateSrcFile(File src){
		String srcFile = JSystemAntUtil.getParameterValue(scenarioString, uuid, "ScriptFile",src.getAbsolutePath());
		src = new File(srcFile);
		// If src file doesn't exist, check in runner/lib/scripts library
		if (!src.exists()){ // User source or partial path
			String userDir = System.getProperty("user.dir");
			File libDir;
			
			// the ifScriptCondition javascript file is extracted from the JSystemAnt file.
			// the Jar file is located in the runner directory.
			// if the user.dir points to jsystemApp it means we are running the JRunner from the eclipse
			// rather than the batch file. in that case, we still need to fetch the script from the runner
			// directory so we use the environment variable

			if (userDir.contains("jsystemApp")) { 
				libDir = new File(System.getenv("RUNNER_ROOT"),"lib");
			}else{
				libDir = CommonResources.getLibDirectory();
			}
			File antJarFile = new File(libDir,"jsystemAnt.jar");
            log.info("Ant Jar File was found here: " + antJarFile);
		    File destination = new File(System.getProperty("user.dir"));
		    if (!new File(destination,src.getName()).exists()){ // if Script file doesn't exist
		    	try { // Extract the script file from the jsystemAnt jar file
                    log.info("Extract the script file from the jsystemAnt jar file to " + destination);
			    	FileUtils.extractOneZipFile("ifScriptCondition.js", antJarFile, destination);
				} catch (IOException e) {
					log.log(Level.SEVERE, "Fail to locate script file for if execution: " + src);
					throw new RuntimeException("Fail locating script file for if execution: " + src);
				}
		    }
		    src = new File(destination,src.getName());
		}
		super.setSrc(src);
	}
	
	public void setSrc(File src) {
		this.tmpSrc = src;
	}
	
	public boolean eval(){
		updateSrcFile(tmpSrc);
		params = JSystemAntUtil.getParameterValue(scenarioString, uuid, "Parameters", params);
		return super.eval();
	}
}
