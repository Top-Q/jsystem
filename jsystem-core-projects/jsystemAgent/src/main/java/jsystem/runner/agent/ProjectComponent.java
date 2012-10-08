/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent;

import java.io.Serializable;

import jsystem.runner.projectsync.ProjectUnZip;
import jsystem.runner.projectsync.ProjectZip;

/**
 * Enumeration of jsystem automation project parts.
 * This enumeration was created to enable effective synchronization of
 * projects between client and agent.
 * @see {{@link ProjectZip} and {@link ProjectUnZip}
 * @author goland
 * @since JSystem 5.0
 */
public enum ProjectComponent implements Serializable {
		
	/**
	 * the folder under which tests classes resides.
     * folder name doesn't necessarily have to be 'classes'.
	 */
	classes,
	
	/**
	 * the package under classes folder where scenarios reside.
	 */
	scenarios,
	
	/**
	 * the package under classes folder where sut files reside.
	 */
	suts,
	
	/**
	 * automation project lib folder. 
	 * lib folder is a sibling of the classes folder.
	 */
	libs,
	
	/**
	 * automation project resources folder.
 	 * resources folder is a sibling of the classes folder.
	 */
	resources,
	
	/**
	 * The current scenario needs a special treatment,
	 * since projects might be functionally similar but still
	 * the current scenario will be different  
	 */
	currentScenario;
}
