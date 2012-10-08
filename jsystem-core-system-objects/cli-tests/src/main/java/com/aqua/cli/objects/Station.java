/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.objects;

import java.util.Properties;

import jsystem.framework.analyzer.Analyzer;
import jsystem.framework.system.SystemObject;

public interface Station extends SystemObject, Analyzer {

	public void dir(String folderName) throws Exception;
	public void ipConfig() throws Exception;
	public void runCommand(String[] commands) throws Exception;
	public void runCommand(String[] commands, String[] errors)throws Exception;
	public void runCommand(String[] commands, String[] errors, String[] musts)throws Exception;
	public void runCommandWithRetryString(String[] commands, String retryString)throws Exception;
	public void runCommandWithRetryParameters(String[] commands, String retryString, int numberOfRetries, long delayInRetries)throws Exception;
	public void runCommandsArray()throws Exception;
	public void ping(String hostToPing) throws Exception;
	public void runCommandsNoEnter()throws Exception;
	public void runCommandsWithDelay()throws Exception;
	public void runSilentCommand()throws Exception;
	public void runCommandWithNewPrompt(String[] commands, String promptString)throws Exception;
	public void runCommandsWithProperties(String[] commands, Properties p)throws Exception;
	
}
