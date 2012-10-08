/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.util.Properties;
import jsystem.framework.FrameworkOptions;
import jsystem.runner.agent.server.RunnerAgent;

/**
 * jsystem.properties related utility methods.
 * @author goland
 */
public class JsystemPropertiesUtils {
	
	/**
	 */
	public static FrameworkOptions[] PROPERTIES_TO_FILTER = 
		new FrameworkOptions[]{FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT,
		                       FrameworkOptions.EXCEL_COMMAND,
		                       FrameworkOptions.HTML_OLD_DIRECTORY,
		                       FrameworkOptions.HTML_OLD_PATH,
		                       FrameworkOptions.LOG_FOLDER,
		                       FrameworkOptions.TEST_VM_PARMS,
		                       FrameworkOptions.TESTS_CLASS_FOLDER,
		                       FrameworkOptions.TESTS_SOURCE_FOLDER,
		                       FrameworkOptions.RESOURCES_SOURCE_FOLDER,
		                       FrameworkOptions.USED_SUT_FILE}; 
	
	/**
	 * Gets a map of properties and removes from it all properties
	 * that should not be updated in agent's jsystem.propeties file.
	 * @see RunnerAgent#setJsystemProperties(Properties)
	 */
	public static void filterJsystemProperties(Properties props){
		for (FrameworkOptions o: PROPERTIES_TO_FILTER){
			props.remove(o.toString());
		}
	}
}
