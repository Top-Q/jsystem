/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.loader.LoadersManager;

/**
 * The engine manager is a singleton class responsible of loading the engines.
 * and find the right engine for a specific script.
 * <p>
 * To load a script engine it should be set to the jsystem.properties file.
 * Use script.engines property to set the class of the engines. use ';' as a delimiter
 * for the list.
 * <p>
 * 
 * @author guy.arieli
 *
 */
public class ScriptsEngineManager {
	private static Logger log = Logger.getLogger(ScriptsEngineManager.class.getName());
	
	private static ScriptsEngineManager manager;
	
	public static ScriptsEngineManager getInstance(){
		if(manager == null){
			manager = new ScriptsEngineManager();
		}
		return manager;
	}
	
	private ScriptsEngineManager(){
		// singleton constractor
	}
	ArrayList<ScriptEngine>engines = null;
	
	/**
	 * Load all the engines that are specified in the jsystem.proprties file:
	 * <p>
	 * <code>script.engines=jsystem.framework.scripts.ant.AntScriptEngine</code>
	 *
	 */
	public void loadEngines(){
		engines = new ArrayList<ScriptEngine>();
		String engs = JSystemProperties.getInstance().getPreference(FrameworkOptions.SCRIPT_ENGINES);
		if (engs == null) {
			return;
		}
		StringTokenizer st = new StringTokenizer(engs, ";");

		while (st.hasMoreTokens()) {
			String engineClassName = st.nextToken();
			try {
				loadManager(engineClassName);
			} catch (Exception e) {
				log.log(Level.WARNING, "fail to load engine: " + engineClassName, e);
			}
		}
		
	}
	
	private void loadManager(String className) throws Exception {
		Class<?> reporterClass = LoadersManager.getInstance().getLoader().loadClass(className);
		engines.add((ScriptEngine)reporterClass.newInstance());
	}
	
	/**
	 * Find the right engine for a specific file by going over all the engines
	 * and ask them to accept the file. The first to accept will be returned.
	 * <code>null</code> will be return if no engine was found.
	 * @param file the script file to check.
	 * @return The script engine or <code>null</code> if not found.
	 */
	public ScriptEngine findExecutor(File file){
		if(engines == null){
			loadEngines();
		}
		for(ScriptEngine engine: engines){
			if(engine.accept(file)){
				return engine;
			}
		}
		return null;
	}
	/**
	 * Find the right engine for a specific script id by going over all the engines
	 * and ask them to accept the id. The first to accept will be returned.
	 * <code>null</code> will be return if no engine was found.
	 * @param id the script id to check.
	 * @return The script engine or <code>null</code> if not found.
	 */
	public ScriptEngine findExecutor(String id){
		if(engines == null){
			loadEngines();
		}
		for(ScriptEngine engine: engines){
			if(engine.accept(id)){
				return engine;
			}
		}
		return null;
	}
	
	
}
