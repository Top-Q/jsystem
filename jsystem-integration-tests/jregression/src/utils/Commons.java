package utils;

import java.util.HashMap;

import jsystem.framework.FrameworkOptions;

/**
 * Utility for common operations
 * 
 * @author Nizan Freedman
 *
 */
public class Commons {

	/**
	 * Get all relevant values for base FrameworkOptions in JSystem properties file
	 * 
	 * @return	 a mapping of options and values
	 */
	public static HashMap<FrameworkOptions, String> getBaseJsystemProperties(){
		HashMap<FrameworkOptions, String> options = new HashMap<FrameworkOptions, String>();
		options.put(FrameworkOptions.SUB_SCENARIO_EDIT, "true");
		options.put(FrameworkOptions.AUTO_SAVE_NO_CONFIRMATION, "true");
		options.put(FrameworkOptions.AUTO_DELETE_NO_CONFIRMATION, "true");
		
		return options;
	}
	
}
