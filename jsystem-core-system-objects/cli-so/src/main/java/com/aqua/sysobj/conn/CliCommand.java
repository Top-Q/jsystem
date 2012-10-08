/*
 * Created on 19/07/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.Properties;
import java.util.Vector;

import systemobject.terminal.Prompt;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.analyzer.AnalyzerParameter;

/**
 * 
 * 
 * 
 * This class implements two steps , running command and printing report to log. 
 * 
 *  1) Running command or several commands on CLI .<br>
 *  
 *     When running command or several commands:<br> 
 *     - If enterFlag is off , there is "Enter" action sent after every command ,<br> 
 *     - The whole results of running command or commands are saved , <br>
 *     - When errors array include expected error messages for the command , they will be searched<br> 
 *     	 at the results , in case of finding one of the error messages at the result buffer,<br>
 *       the test will be declared as failed (if errorIgnore flag not raised). <br>
 *   
 *  2)  Making Report. <br>
 *  	Running Analyzers on the result. Seraching the text in the results and printing the right message.<br>  
 *		If an erros has been occured during running command , report printing right message for that error.<br>
 *
 *
 *
 * Properties description
 * 
 * ---Input properties---
 * 
 * commands - Array of commands to run in CLI , when there are several commands , they runnig at once , 
 * 			  just an "enter" inserted after every command.
 * 
 * timeout - Time to wait for command execution until throwing error and declaring the command as failed.
 * 
 * errors - Array of error messages that will be searched at the results , 
 * 			in case of match , the command will be declared as failed.
 * 
 * musts - Array of strings that must be appear at the result, if one of the musts did not appear , 
 * 		   the test declared as failed.
 * 
 * addEnter - Flag that defines if "enter" will be inserted after inserting command, 
 * 			  true value will add "enter" and false will not insert "enter" and 
 * 			  will just wait for additional char or string to insert , 
 * 			  useful on platforms with no "enter" needed after inserting some chars 
 * 			  Example:  Cisco IOS - "show ?" will be accepted as command .
 * 
 * position - Position in the CLI hierarchy\mode
 * 			  Example : " router(config)# " and " router# " are prompts of two different modes 
 * 			  and every mode has its own commands to run,
 * 			  every position has commands defined in every system object (CiscoCliCommand class for example)
 * 
 * delayTyping - Flag that defines a way of sending the command string to CLI , 
 * 				 if false - a whole command will be sent as one string , 
 * 				 if true - the command will be sent letter by letter.
 *   
 * result - Result that appears after running the command, if there is several commands , 
 * 			all of the results of every command will be buffered.
 * 
 * thrown - Exception that thrown in case of error, used just to save an exception itself ,if it was thrown.
 * 
 * failed - Flag that defines if the command ran successfully or not, can be manually changed also.
 * 
 * retryString - If defined , this string will be searched in result and in found case , 
 * 				 the command will run again and will be retried while receiving the string , 
 * 				 if string will be found again and again , the command will be declared as failed ,
 * 				 if the string will not be found , the test will move on. 
 * 				 used also as flag for retry and checked if not equal to null.
 * 
 * numberOfRetries - Number of retry times to run a command in case of retryString is not null.
 *  
 * delayInRetries - Delay between sending command retry in case of retryString is not null.
 * 
 * ---Output properties---
 * 
 * ignoreErrors - Flag that defines an ignore of error messages matching , 
 * 			      default is false , but when true and one of the error messages found in the results ,
 * 				  the test will not be declared as false.
 * 
 * promptString - Prompt string that expected when running command , 
 * 				  with ignoring previously implemented prompts in the CliConnection class.
 * 
 * properties - Table of keys and values from String type to look in the command .
 * 				This HashTable include keys (string to search in the command) 
 * 				and values (string to change with in case of key match). 
 * 				in case of finding the match , the value in the command will be changed with value in the HashTable.
 * 				The value to change in the command must be between "< >" chars, in order to SystemObject catch it.
 * 			    Example. Command - "this is a <value1> command" when value1 is "test" string, the command 
 * 				that will be sent to device will be "this is a test command".
 * 				
 *  
 * failCause - String with fail cause message , this message appears in the log, 
 * 			   not given to be changed manually.
 * 
 * silent - Flag that defines if a command will be shown in log or will run in the background.
 * 
 * analyzers - Vector that contains analyzers of the command (FindText and TextNotFound analyzers), 
 * 			   used by error array and other additional strings to search in the results,
 * 			   FindText analyzer - will declare failed command if text was not found
 * 			   TextNotFound analyzer  - this analyzer is used in the error search , 
 * 			   							will declare failed command if text was found. 
 *  
 * prompts - Array of prompts for the command , defined in CliConnection class.
 * 
 *
 * @author guy.arieli 
 */

public class CliCommand {
	
	
	static long defaultTimeout = 30000;

	static String[] defaultErrors = null;
	
	/*--- Input properties ---*/
	
	String[] commands;
	
	long timeout = defaultTimeout;
	
	String[] errors = defaultErrors;
	
	String[] musts = null;
	
	boolean addEnter = true;
	
	String position = null;
	
	boolean delayTyping = false;
	
	StringBuffer result = new StringBuffer();
	
	Prompt resultPrompt;

	Exception thrown;
	
	boolean failed = false;
	
	String retryString = null;
	
	int numberOfRetries = 5;
	
	long delayInRetries = 10000;
	
	/*--- Output properties ---*/
	
	boolean ignoreErrors = false;
	
	String promptString = null;
	
	Properties properties = new Properties();
	
	String failCause = null;
	
	boolean silent = false;
	
	boolean suppressEcho = false;

	Vector<AnalyzerParameter> analyzers = new Vector<AnalyzerParameter>();
	
	Prompt[] prompts = null;
	
	private boolean clone = false;
	
	private boolean dontWaitForPrompts = false;
	
	public CliCommand(){

	}

	/**
	 * create a new command with given String
	 * 
	 * @param command	the command String
	 */
	public CliCommand(String command){
		setCommand(command);
	}
	
	/*--- Methods --*/
	
	/**
	 * @return the array of analyzers defined for the command
	 */
	public AnalyzerParameter[] getAnalyzers() {
		Object[] o =analyzers.toArray();
		AnalyzerParameter[] ret = new AnalyzerParameter[o.length];
		System.arraycopy(o,0,ret,0,o.length);
		return ret;
	}
	
	/**
	 * add an analyzer to the list of the command analyzers<br>
	 * the analysis of all analyzers will be done on the found input after running the command (the TestAgainstObject)  
	 * 			   
	 * @param analyzer	an AnalyzerParameterImpl analyzer
	 */
	public void addAnalyzers(AnalyzerParameter analyzer) {
		analyzers.addElement(analyzer);
	}
	
	/**
	 * @return the Failure String if the command failed , or null otherwise
	 */
	public String getFailCause() {
		return failCause;
	}
	
	/**
	 * in case the command fails, the fail message is set
	 * 
	 * @param failCause	
	 */
	public void setFailCause(String failCoase) {
		this.failCause = failCoase;
	}
	
	/**
	 * @return	True if errors where signaled to be ignored (command execution will always pass)
	 */
	public boolean isIgnoreErrors() {
		return ignoreErrors;
	}
	
	/**
	 * if set To True all command execution errors will be ignored and command will always be signaled as passed<br>
	 * default is false
	 * 
	 * @param ignoreErrors 
	 */
	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}
	
	/**
	 * if set, the prompt String represents the prompt to wait for (overrides Prompts array)
	 * 
	 * @return promptString	if one was set
	 */
	public String getPromptString() {
		return promptString;
	}
	
	/**
	 * sets the prompt String to wait for, overrides the Prompt array
	 * 
	 * @param promptString
	 */
	public void setPromptString(String promptString) {
		this.promptString = promptString;
	}
	
	/**
	 * the default time out for command execution (in ms)
	 * 
	 * @return defaultTimeout 
	 */
	public static long getDefaultTimeout() {
		return defaultTimeout;
	}
	
	/**
	 * Default value of time (in ms) to wait for command execution until throwing error and declaring the command has failed.
	 * 
	 * @param defaultTimeout 
	 */
	public static void setDefaultTimeout(long defaultTimeout) {
		CliCommand.defaultTimeout = defaultTimeout;
	}
	
	/**
	 * if True then enter string will be added to the command string
	 * 
	 * @return addEnter 
	 */
	public boolean isAddEnter() {
		return addEnter;
	}
	
	/**
	 * if True then enter string will be added to the command string
	 * 
	 * @param addEnter 
	 */
	public void setAddEnter(boolean addEnter) {
		this.addEnter = addEnter;
	}
	
	/**
	 * get the array of command set to be sent to the terminal
	 * 
	 * @return commands 
	 */
	public String[] getCommands() {
		return commands;
	}
	
	/**
	 * Array of commands to run in the CLI<br>
	 * if several commands are used , they run one after the other, separated by the enter String 
	 * 
	 * @param commands 
	 */
	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	/**
	 * set a single command to be sent to the CLI
	 * 
	 * @param command 
	 */
	public void setCommand(String command) {
		setCommands(new String[]{command});
	}

	
	/**
	 * Flag that indicates if command writing to the terminal should be done with a 20ms delay between each char
	 * 
	 * @return delayTyping 
	 */
	public boolean isDelayTyping() {
		return delayTyping;
	}
	
	/**
	 * Flag that defines a way of sending the command string to CLI ,<br> 
	 * 				 if false - a whole command will be sent as one string ,<br> 
	 * 				 if true - the command will be sent letter by letter.<br>
	 * 
	 * @param delayTyping 
	 */
	public void setDelayTyping(boolean delayTyping) {
		this.delayTyping = delayTyping;
	}
	
	/**
	 * get all errors created while executing the command(s)
	 * 
	 * @return errors 
	 */
	public String[] getErrors() {
		return errors;
	}
	
	/**
	 * add an error to the Array of error messages that will be searched at the results , 
	 * 			in case of match , the command will be declared as failed.
	 * 
	 * @param error 
	 */
	public void addErrors(String error) {
		addAnalyzers(new TextNotFound(error));
	}
	
	/**
	 * Flag that defines if the command ran successfully or not, can be manually changed also.
	 * 
	 * @return failed
	 */
	public boolean isFailed() {
		return failed;
	}
	
	/**
	 * 
	 * @param failed If True, Declare the command as failed, by default - false
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	/**
	 * get the array of Strings that must appear in the command result
	 * 
	 * @return musts
	 */
	public String[] getMusts() {
		return musts;
	}
	
	/**
	 * Array of strings that must be appear at the result, if one of the musts did not appear , 
	 * 		   the test will be declared as failed.
	 * 
	 * @param musts
	 */
	public void addMusts(String[] musts) {
		this.musts = musts;
		for (int i = 0; i < musts.length; i++){
			addAnalyzers(new FindText(musts[i]));
		}
	}
	
	/**
	 * 
	 * @return position
	 */
	public String getPosition() {
		return position;
	}
	
	/**
	 * Position in the CLI hierarchy\mode
	 * 			  Example : " router(config)# " and " router# " are prompts of two different modes 
	 * 			  and every mode has its own commands to run,
	 * 			  every position has commands defined in every system object (CiscoCliCommand class for example)
	 * 
	 * @param position
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	
	/**
	 * the response String read from the terminal 
	 * 
	 * @return result buffer
	 */
	public String getResult() {
		return result.toString();
	}
	
	/**
	 * Result that appears after running the command, if there is several commands , 
	 * 			all of the results of every command will be buffered.
	 * 
	 * @param result
	 */
	public void addResult(String result) {
		this.result.append(result);
	}
	
	/**
	 * get the Thrown exception if one was thrown during command execution
	 * 
	 * @return thrown 
	 */
	public Exception getThrown() {
		return thrown;
	}
	
	/**
	 * set the Exception that is thrown in case of error, used just to save an exception itself ,
	 * 			if it was thrown.
	 * @param thrown 
	 */
	public void setThrown(Exception thrown) {
		this.thrown = thrown;
	}
	
	/**
	 * get the defined timeout for the command.<br>
	 * the default is defaultTimeOut
	 * 
	 * @return timeout 
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * Time to wait for command execution until throwing error and declaring the command as failed.
	 * 
	 * @param timeout 
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * get the array of errors that should be searched in the result<br>
	 * any match will result in command failure
	 * 
	 * @return defaultErrors 
	 */
	public static String[] getDefaultErrors() {
		return defaultErrors;
	}
	
	/**
	 * Array of error messages that will be searched at the results , 
	 * in case of match , the command will be declared as failed.
	 * 
	 * @param defaultErrors 
	 */
	public static void setDefaultErrors(String[] defaultErrors) {
		CliCommand.defaultErrors = defaultErrors;
	}
	
	/**
	 * the time to sleep in ms between command re-sending if retry string was set 
	 * 
	 * @return delayInRetries 
	 */
	public long getDelayInRetries() {
		return delayInRetries;
	}
	
	/**
	 * the time in ms to sleep between command re-sending if retry String is not null 
	 * 
	 * @param delayInRetries 
	 */
	public void setDelayInRetries(long delayInRetries) {
		this.delayInRetries = delayInRetries;
	}
	
	/**
	 * number of times to check for retry String (also Max number of times for command re-sending)
	 * 
	 * @return numberOfRetries 
	 */
	public int getNumberOfRetries() {
		return numberOfRetries;
	}
	
	/**
	 * Number of retry times to run a command in case of retryString is not null.
	 * 
	 * @param numberOfRetries 
	 */
	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}
	
	/**
	 * if set, the retry string is the string that signals that the command should be resent
	 * 
	 * @return	the String to be searched in the result
	 */
	public String getRetryString() {
		return retryString;
	}
	
	/**
	 *  If defined , this string will be searched in result.<br>
	 *  in case it is found , the command will run again and will be retried while receiving the string ,<br> 
	 * 	if string will be found again and again , the command will be declared as failed ,<br>
	 * 	if the string will not be found , the test will move on. <br>
	 * 	if set to null - signal for no retry
	 * 
	 * @param retryString
	 */
	public void setRetryString(String retryString) {
		this.retryString = retryString;
	}
	
	/**
	 * Table of keys and values from String type to replace in the command before sending.<br>
	 * This HashTable include keys (strings to search in the command) <br>
	 * and values (string to set in case of key match). <br>
	 * in case of a match , the value in the command will be changed with value in the HashTable.<br>
	 * The value to change in the command must be between "< >" chars.<br>
	 * Example:<br>
	 * Command = "this is a "<"<b>value1</b>">" command" when value1 is "test" string, the command<br>
	 * that will be sent to device will be "this is a <b>test</b> command".
	 * 
	 * @param Properties
	 */
	public void addProperties(Properties p){
		properties.putAll(p);
	}
	
	/**
	 * 
	 * get the keys and values that should be replaced in the command before command(s) execution
	 * 
	 * @return Properties
	 */
	public Properties getProperties(){
		return properties;
	}
	
	/**
	 * Flag that defines if a command will be shown in log or will run in the background.
	 * 
	 * @return silent 
	 */
	public boolean isSilent() {
		return silent;
	}
	
	/**
	 * if set to True then command will not be shown in log 
	 * 
	 * @param silent 
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	/**
	 * get the array of Prompts defined for the command
	 * 
	 * @return prompts 
	 */
	public Prompt[] getPrompts() {
		return prompts;
	}
	
	/**
	 * Array of prompts for the command , defined in CliConnection class.
	 * 
	 * @param prompts 
	 */
	public void setPrompts(Prompt[] prompts) {
		this.prompts = prompts;
	}

	/**
	 * if true then the command will be cut out of the result string
	 * 
	 * @return
	 */
	public boolean isSuppressEcho() {
		return suppressEcho;
	}

	/**
	 * Usually when sending a command to the remote cli agent the command is echoed
	 * back by the cli agent.
	 * When this flag is turned to true, the command is cut out of the command output.
	 *  
	 * @param suppressEcho
	 */
	public void setSuppressEcho(boolean suppressEcho) {
		this.suppressEcho = suppressEcho;
	}

	/**
	 *  The prompt that triggered the end of the cli command.
	 *  If the CliObject contains several commands, the prompt is 
	 *  set to the last command prompt.
	 * 
	 * @return
	 */
	public Prompt getResultPrompt() {
		return resultPrompt;
	}

	/**
	 *  The prompt that triggered the end of the cli command.
	 *  If the CliObject contains several commands, the prompt is 
	 *  set to the last command prompt.
	 * 
	 * @param resultPrompt
	 */
	void setResultPrompt(Prompt resultPrompt) {
		this.resultPrompt = resultPrompt;
	}

	/**
	 * 
	 * @return if the command(s) should be executed on a cloned connection
	 */
	public boolean isClone() {
		return clone;
	}
	/**
	 * If set to true command(s) will be executed on a cloned connection
	 * @param clone
	 */
	public void setClone(boolean clone) {
		this.clone = clone;
	}

	/**
	 * @return is the command signaled to run with no prompt waiting
	 */
	public boolean isDontWaitForPrompts() {
		return dontWaitForPrompts;
	}

	/**
	 * Default is false.
	 * If set to true will not wait for any prompts, just send the commands and return
	 * @param dontWaitForPrompts
	 */
	public void setDontWaitForPrompts(boolean dontWaitForPrompts) {
		this.dontWaitForPrompts = dontWaitForPrompts;
	}
}
