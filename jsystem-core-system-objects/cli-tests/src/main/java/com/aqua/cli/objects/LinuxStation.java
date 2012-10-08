/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.objects;

import java.util.Properties;

import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliCommand;

/**
 * @author Denis.Malinovtsev
 *
 *	This class implements the commands for Linux based platform 
 *  and runing them using the CliApplication System Object 
 */
public class LinuxStation extends CliApplication implements Station {

	
	/**
	 * This method running dir (ls) command in CLI and check error messages tht can be thrown
	 * 
	 * @param folderName
	 */
	public void dir(String folderName) throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ls" + folderName});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		handleCliCommand("Show folder content:  " + folderName, cmd);
	}
	
	/**
	 * This method running ifconfig command in CLI and check error messages that could be thrown
	 * 
	 */
	public void ipConfig()throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ifconfig"});
		cmd.addErrors("Device not found");
		cmd.addErrors("command not found");
		handleCliCommand("Show interfaces", cmd);
	}
	
	/**
	 * This method running ping command in CLI and check error messages that could be thrown
	 * 
	 * @param hostToPing
	 * 
	 */
	public void ping(String hostToPing)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ping -c 5 " + hostToPing});
		cmd.addErrors("unknown host");
		cmd.addErrors("command not found");
		handleCliCommand("Ping to :  "+ hostToPing, cmd);
	}

	/**
	 * This method array of commands command in CLI
	 * 
	 */
	public void runCommandsArray()throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ls","ifconfig","ps aux"});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		
		handleCliCommand("Running Comands...", cmd);
	}
	
	/**
	 * This method running custom command in CLI
	 * 
	 * @param hostToPing 
	 * 
	 */
	public void runCommand(String[] commands) throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		
		handleCliCommand("Running commands...", cmd);
	}
	
	/**
	 * This method running custom command in CLI
	 * 
	 * @param hostToPing , errors
	 * 
	 */
	public void runCommand(String[] commands, String[] errors) throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		
		for (int i=0 ; i< errors.length; i++) {
			cmd.addErrors(errors[i]);
		}
		
		handleCliCommand("Running commands...", cmd);
	}
	
	/**
	 * This method running custom command in CLI
	 * added musts array that include musts strings to search in the result
	 * 
	 * @param hostToPing , errors , musts
	 * 
	 */
	public void runCommand(String[] commands, String[] errors, String[] musts)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		cmd.addMusts(musts);
		
		for (int i=0 ; i< errors.length; i++) {
			cmd.addErrors(errors[i]);
		}
		
		handleCliCommand("Running commands with errors and musts implemented...", cmd);
	}
	
	/**
	 * This method running custom command in CLI
	 * added retry string to search in the result
	 * @param hostToPing , retryString
	 * 
	 */
	public void runCommandWithRetryString(String[] commands, String retryString)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		cmd.setRetryString(retryString);
		
		handleCliCommand("Running commands with retries...", cmd);
	}
	
	/**
	 * This method running custom command in CLI
	 * added retry string to search in the result and counters for this strings
	 * @param hostToPing , retryString , numberOfRetries, delayInRetries
	 * 
	 */
	public void runCommandWithRetryParameters(String[] commands, String retryString, int numberOfRetries, long delayInRetries)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		cmd.setRetryString(retryString);
		cmd.setNumberOfRetries(numberOfRetries);
		cmd.setDelayInRetries(delayInRetries);
		
		handleCliCommand("Running commands with new new retry parameters...", cmd);
	}
	
	/**
	 * This method running dir command in CLI without pressing "enter" in the end of the command
	 *
	 * 
	 */
	public void runCommandsNoEnter()throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ls"});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		cmd.setAddEnter(false);
		cmd.setIgnoreErrors(true);
		cmd.setTimeout(2000);
		handleCliCommand("Command not sent, waiting for timeout ...", cmd);
		
		cmd = new CliCommand();
		cmd.setCommands(new String[]{""});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		handleCliCommand("And now command was sent...", cmd);
	}
	
	/**
	 * This method running dir command in CLI with delay sending method (letter by letter)
	 *
	 * 
	 */
	public void runCommandsWithDelay()throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[]{"ls"});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		cmd.setDelayTyping(true);
		handleCliCommand("Command was sent using delay ...", cmd);
	}
	
	/**
	 * This method running dir command in CLI without sending the rreport to log file
	 *
	 * 
	 */
	public void runSilentCommand()throws Exception {
		CliCommand cmd = new CliCommand();

		cmd.setCommands(new String[]{"ls"});
		cmd.addErrors("No such file or directory");
		cmd.addErrors("command not found");
		cmd.setSilent(true);
		handleCliCommand("This title couldn't be printed ...", cmd);
		
	}
	
	/**
	 * This method running commands in CLI but changing the default prompt to new value and expecting for it
	 *
	 * 
	 */
	public void runCommandWithNewPrompt(String[] commands, String promptString)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		cmd.setPromptString(promptString);
		cmd.setIgnoreErrors(true);
		cmd.setTimeout(5000);
		
		handleCliCommand("Running commands with new new prompt..." , cmd);
	
		cmd = new CliCommand();
		cmd.setCommands(commands);
		
		handleCliCommand("Changing the prompt back...", cmd);
	
	}
	
	/**
	 * This method running commands in CLI but changing the properties table
	 *
	 * 
	 */
	public void runCommandsWithProperties(String[] commands, Properties p)throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(commands);
		cmd.addErrors("Conflicting format options");
		cmd.addErrors("command not found");
		cmd.addErrors("syntax error");
		cmd.addProperties(p);
		
		handleCliCommand("Running commands with parameters..." , cmd);
	
	}
	
	
}