/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts;

import java.io.File;

import javax.swing.ImageIcon;

/**
 * Used as interface to define script execution engine. The script execution
 * engine is used for easy integration of 3'rd party scripts like Ant. In the
 * future we will add perl and tcl.
 * <p>
 * Every engine uses unique ID. This ID will be used to tag the test into the
 * scenario files and to identfy the engine required for a specific script.
 * <p>
 * 
 * 
 * @author guy.arieli
 * 
 */
public interface ScriptEngine {
	/**
	 * Check if the file is applicable for this engine to use as test.
	 * @param file the file to check.
	 * @return to if this is a test (or tests) that this engine will know to
	 * execute.
	 */
	public boolean accept(File file);

	/**
	 * Process the file and create the tests execution objects
	 * @param file the file to process
	 * @return the tests to be executed
	 */
	public ScriptExecutor[] getExecutor(File file);

	/**
	 * Get the engine ID. The class of the ScriptExecutor extention class should be used.
	 * So in AntScriptEngine the AntScriptExecutior will be used as an ID.
	 * @return
	 */
	public String getId();

	/**
	 * Check if this ID is applicable for this engine.
	 * or should this engine be used the script with the following ID.
	 * @param id the ID to process
	 * @return true if the ID is accepted
	 */
	public boolean accept(String id);

	/**
	 * Create executor from the given tag
	 * @param tag the tag to be used
	 * @return script executor
	 */
	public ScriptExecutor getExecutor(String tag);

	/**
	 * 
	 * @return the icon to be used in the GUI.
	 */
	public ImageIcon getBasicImageIcon();

	/**
	 * 
	 * @return the icon to be used in the GUI when running.
	 */
	public ImageIcon getRunningImageIcon();

	/**
	 * 
	 * @return the icon to be used in the GUI when fail.
	 */
	public ImageIcon getFailImageIcon();

	/**
	 * 
	 * @return the icon to be used in the GUI when error.
	 */
	public ImageIcon getErrorImageIcon();

	/**
	 * 
	 * @return the icon to be used in the GUI when OK.
	 */
	public ImageIcon getOKImageIcon();

}
