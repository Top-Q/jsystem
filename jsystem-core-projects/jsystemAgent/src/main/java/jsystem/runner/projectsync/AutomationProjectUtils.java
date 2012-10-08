/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.io.File;

/**
 * Automation project utility methods.
 * 
 * @author goland
 */
public class AutomationProjectUtils {

	/**
	 * Checks whether the path referenced by <code>projectClassesPath</code>
	 * is a valid jsystem automation project.
	 */
	public static boolean isValidProject(File projectClassesPath) throws Exception {
		if (!projectClassesPath.exists()){
			return false;
		}
		if (!projectClassesPath.isDirectory()){
			return false;
		}
		if (projectClassesPath.list().length == 0){
			return false;
		}	
		return true;
	}

}
