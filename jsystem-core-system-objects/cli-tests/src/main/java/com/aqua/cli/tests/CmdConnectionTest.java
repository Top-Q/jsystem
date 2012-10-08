/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.tests;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CmdConnection;

import jsystem.extensions.analyzers.text.FindText;
import junit.framework.SystemTestCase;

/**
 * Example for connection with the Windows CMD console
 */
public class CmdConnectionTest extends SystemTestCase {
	private CmdConnection cliConnection;
	private boolean isClone = false;
	
	public void setUp() throws Exception{
		cliConnection	= new CmdConnection();
		cliConnection.init();
	}
	
	public void tearDown() throws Exception{
		cliConnection.close();
	}

	/**
	 * run a <I>dir</I> command and search for <I>"Directory of"</I> String in result
	 * 
	 * @throws Exception
	 */
	public void testSimpleCmdConnection() throws Exception {
		report.step("Signal that CMD should be cloned on each operation");
		cliConnection.setCloneOnEveryOperation(isClone());
		CliCommand command = new CliCommand("dir");
		cliConnection.handleCliCommand("performed dir", command);
		cliConnection.analyze(new FindText("Directory of"));
	}
	
	/**
	 * run a <I>dir</I> command and search for <I>"Directory of"</I> String in result<br>
	 * do it twice
	 * @throws Exception
	 */
	public void testCmdConnectionOneAfterAnother() throws Exception {
		testSimpleCmdConnection();
		testSimpleCmdConnection();
	}

	public void testCmdConnectionWithCommandThatRequiersClose() throws Exception {
		cliConnection.setCloneOnEveryOperation(true);
		CliCommand command = new CliCommand("wmic os get buildnumber");
		cliConnection.handleCliCommand("performed wmi operation", command);
		cliConnection.analyze(new FindText("BuildNumber"));
	}

	/**
	 * run several commands and search for expected strings in the result 
	 * 
	 * @throws Exception
	 */
	public void testCmdConnectionWithMixedCommands() throws Exception {
		CliCommand command = new CliCommand("wmic os get buildnumber");
		command.setClone(true);
		cliConnection.handleCliCommand("performed wmi operation", command);
		cliConnection.analyze(new FindText("BuildNumber"));

		command = new CliCommand("dir");
		cliConnection.handleCliCommand("performed dir", command);
		cliConnection.analyze(new FindText("Directory of"));

		command = new CliCommand("dir");
		cliConnection.handleCliCommand("performed dir", command);
		cliConnection.analyze(new FindText("Directory of"));

	}

	/**
	 * open a telnet connection
	 * 
	 * @throws Exception
	 */
	public void testCmdConnectionWithTelnet() throws Exception {
		CliCommand command = new CliCommand("telnet");
		cliConnection.handleCliCommand("", command);
		command = new CliCommand("open 127.0.0.1");
		cliConnection.handleCliCommand("", command);
	}

	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
	}

}
