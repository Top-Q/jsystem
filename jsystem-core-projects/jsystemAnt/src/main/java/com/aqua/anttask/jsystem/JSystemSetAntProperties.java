/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import org.apache.tools.ant.Task;

/**
 * A task for updating the ant.properties file from the project properties.<br>
 * The task was created to solve the For parameter being passed on to elements
 * which don't extend the Ant Task and need the parameters (Such as switch inside for).
 * 
 * @author Nizan Freedman
 *
 */
public class JSystemSetAntProperties extends Task {

	public void execute(){
		JSystemAntUtil.propertiesToFile(this);
	}
	
}
