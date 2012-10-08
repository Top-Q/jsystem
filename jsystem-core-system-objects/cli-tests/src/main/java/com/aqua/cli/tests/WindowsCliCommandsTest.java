/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.tests;

import java.util.Properties;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.TextNotFound;
import junit.framework.SystemTestCase;

import com.aqua.cli.objects.Station;

/**
 *
 *	This class implements the tests for CliApplication SystemObject (infra)
 *	for Windows/Linux based platform 
 *   
 * @author Denis.Malinovtsev
 */
public class WindowsCliCommandsTest extends SystemTestCase{

	Station station;
	
	String hostToPing = "localhost";
	String fileToFind = "";
	
	public void setUp() throws Exception
	{
		station = (Station)system.getSystemObject("winStation");
	}

	/**
	 * Test executing the "dir" command and find file/folder in that directory
	 * 
	 */
	public void testExecuteDir() throws Exception
	{
		report.step("Running dir/ls command");
		station.dir("");
		report.step("Searching file/folder");
		station.analyze(new FindText(fileToFind));
	}
	
	/**
	 * Test "ipconfig" command and check if the comand putted result
	 * 
	 */
	public void testIpConfig() throws Exception
	{
		report.step("Running ipconfig/ifconfig command");
		station.ipConfig();
		//station.analyze(new FindText("eth0"));
	}
	
	/**
	 * Test ping command to any host and check if the ping wass successeful
	 * 
	 */
	public void testPing() throws Exception
	{
		
		report.step("Running ping command");
		station.ping(hostToPing);
		
		report.step("Checking if ping was successeful");
		station.analyze(new FindText("0% loss"));
	}
	
	/**
	 * Send an Array of  Commands through 
	 * CLI and check that they are successfully activated
	 * 
	 */
	public void testCommandsArray() throws Exception
	{
		report.step("Running ping command");
		station.runCommandsArray();
	}

	/**
	 *  
	 * Testing noEnter parameter by sending the command without enter ,
	 * in addition testing the timeout parameter  
	 */
	public void testCommandsNoEnter() throws Exception
	{
		report.step("Inserting commands...");
		station.runCommandsNoEnter();
	}
	
	/**
	 * Testing timeout 
	 */
	public void testCommandTimeout() throws Exception
	{
		report.step("Inserting commands...");
		station.runCommandsNoEnter();
	}
	
	
	/**
	 *  
	 * Testing the sending with delay feature (letter by letter)
	 *  
	 */
	public void testCommandsWithDelay() throws Exception
	{
		report.step("Inserting command...");
		station.runCommandsWithDelay();
	}
	
	/**
	 *  
	 * Testing the silent command feature 
	 *  
	 */
	public void testSilentCommand() throws Exception
	{
		report.step("Inserting silent command ...");
		station.runSilentCommand();
	}
	
	/**
	 *  
	 * Test errors of the commands 
	 *  
	 */
	public void testErrorCommand() throws Exception
	{
		report.step("Inserting commands with errors expected...");
		report.setFailToPass(true);
		try {
			station.runCommand(new String[] {"dir","dir ababab","ping"}, new String[] {"File Not Found"});
			report.setFailToPass(false);
			report.report("No Error was trown, test failed", false);
		} 
		catch (Throwable t){
			report.setFailToPass(false);
			assertEquals("Wrong exception message" , "The text:<File Not Found> was found", t.getMessage());
		} 
		finally {
			report.setFailToPass(false);
		}

	}
	
	/**
	 *  
	 * Test analyzers
	 * FindText analyzer -  will fail the test if text was not found
	 * 
	 * TextNotFound analyzer  - will fail the test if text was found 
	 *  
	 */
	public void testAnalyzers() throws Exception
	{
		report.step("Inserting commands...");
		station.runCommand(new String[] {"dir","ipconfig","ping"}, new String[] {"not recognized", "File Not Found"});

		report.step("Testing success analyzers...");
		
		// FindText analyzer will fail the test if text was not found
		// TextNotFound analyzer will fail the test if text was found 
		station.analyze(new FindText("IP Address"));
		station.analyze(new TextNotFound("IP Addressss"));
		
		report.step("Testing failed TextNotFound analyzer...");
		
		try{
			report.setFailToPass(true);
			station.analyze(new TextNotFound("IP Address"));
			report.report("Analyzer passed successefully, test failed", false);
		}
		catch (Throwable t)
		{
			assertEquals("Wrong exception message" , "The text:<IP Address> was found", t.getMessage());

		}
		finally
		{
			report.setFailToPass(false);
		}
		
		report.step("Testing failed FindText analyzer...");
		try{
			report.setFailToPass(true);
			station.analyze(new FindText("IP Addressss"));
			report.report("Analizer passed successefully, test failed", false);
		}
		catch (Throwable t)
		{
			assertEquals("Wrong exception message" , "The text: <IP Addressss> wasn't found", t.getMessage());

		}
		finally
		{
			report.setFailToPass(false);
		}

	}
	
	/**
	 *  
	 * Test musts - the strings that have to be in the result to pass the test
	 * 
	 */
	public void testMusts() throws Exception
	{
		report.step("Inserting commands with musts implemented , shoul'd pass...");
		station.runCommand(new String[] {"dir","ipconfig","ping"}, 
						   new String[] {"not recognized", "File Not Found"},
						   new String[] {"IP Address", "Options"});

		report.step("Now one of the musts will not be found , failed musts ...");
		report.setFailToPass(true);
		try {
			station.runCommand(new String[] {"dir","ipconfig","ping"}, 
					   new String[] {"not recognized", "File Not Found"},
					   new String[] {"IP Address", "Linux"});
						report.setFailToPass(false);
			report.report("No Error was trown, test failed", false);
		} 
		catch (Throwable t){
			report.setFailToPass(false);
			assertEquals("Wrong exception message" , "The text: <Linux> wasn't found", t.getMessage());
		} 
		finally {
			report.setFailToPass(false);
		}
	}	
	
	
	/**
	 *  
	 * Test retries - test retries with default values
	 * 
	 */
	
	public void testRetriesDefault() throws Exception
	{
		report.step("Inserting commands , retry string will not be found, shouldn't retry ");
		station.runCommandWithRetryString(new String[] {"dir","ipconfig","ping"}, "IP Addresssss");				
		
		report.step("The test should retry  5 times with 10 sec. delay this time... ");
		report.setFailToPass(true);
		try {
			station.runCommandWithRetryString(new String[] {"dir","ipconfig","ping"}, "IP Address");
			report.setFailToPass(false);
			report.report("No retry string was found, test failed", false);
		} 
		catch (Throwable t){
			report.setFailToPass(false);
			assertEquals("Wrong exception message" , "Cli command failed", t.getMessage());
		} 
		finally {
			report.setFailToPass(false);
		}
	}
	
	/**
	 *  
	 * Test retries - test retries with new values
	 * 
	 */
	
	public void testRetriesWithParameters() throws Exception
	{
		report.step("Inserting commands with retry string implemented , shouldn't retry ");
		station.runCommandWithRetryParameters(new String[] {"dir","ipconfig","ping"}, "-IP Address-", 2,5000);
		
		report.step("The test should retry  5 times with 10 sec. delay this time...");
		report.setFailToPass(true);
		try {
			station.runCommandWithRetryParameters(new String[] {"dir","ipconfig","ping"}, "IP Address", 2,5000);
			report.setFailToPass(false);
			report.report("No retry string was found, test failed", false);
		} 
		catch (Throwable t){
			report.setFailToPass(false);
			assertEquals("Wrong exception message" , "Cli command failed", t.getMessage());
		} 
		finally {
			report.setFailToPass(false);
		}

	}

	/**
	 *  
	 * Test prompt change
	 * 
	 */
	public void testPromptChange() throws Exception
	{
		report.step("Changing prompt to unexpected , should fail");
		station.runCommandWithNewPrompt(new String[] {"dir","ipconfig","ping"}, "&&&");
		
	}
	
	/**
	 * Testing parameters table 
	 */
	public void testProperties() throws Exception
	{
		Properties p = new Properties();
		p.setProperty("value1", "dir");
		p.setProperty("value2", "c:");
		report.step("Inserting commands...");
		
		station.runCommandsWithProperties(new String[] {"<value1> <value2>"},p);
	}
	
	
	/* --- Setters/Getters ---*/
	
	/**
	 * Hostname/IP Address to ping 
	 */
	public void setHostToPing(String hostToPing) {
		this.hostToPing = hostToPing;
	}
	public String getFileToFind() {
		return fileToFind;
	}

	/**
	 * Filename of the file/folder to find
	 */ 
	public void setFileToFind(String fileToFind) {
		this.fileToFind = fileToFind;
	}
	public String getHostToPing() {
		return hostToPing;
	}

	

}
