/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package org.jsystem.examples.cli;

import junit.framework.SystemTestCase;
import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliCommand;

public class CliApplicationFeaturesTest extends SystemTestCase {

	private CliApplication station;
	
	public void setUp() throws Exception {
		station = (CliApplication)system.getSystemObject("linuxStation");
	}
	
	public void testTelnetSessionToFile() throws Exception {
		station.conn.cli.setPrintStream(null);
		station.cliCommand("dir");		
	}
	
	public void testGetResultsPrompt() throws Exception {
		station.cliCommand("dir");
		assertNotNull(station.conn.cli.getResultPrompt());		
	}

	public void testGetResultsPromptAdvanced() throws Exception {
		CliCommand command = new CliCommand();
		Prompt p = new Prompt("~#",false);
		p.setCommandEnd(true);
		p.setDontWaitForScrollEnd(false);
		command.setCommands(new String[]{"dir"});
		command.setPrompts(new Prompt[]{p});
		station.handleCliCommand("testing dir oper", command);
		assertEquals(station.conn.cli.getResultPrompt().getPrompt(),"~#");			
	}

	public void testIgnoreErrorOnCliCommand() throws Exception {
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"commandthatfails"});
		command.addMusts(new String[]{"will never come"});
		command.setIgnoreErrors(true);
		command.setTimeout(300);
		station.handleCliCommand("sending a command that fails", command);
	}
	
	public void testIgnoreErrorOnCliApplication() throws Exception {
		CliCommand command = new CliCommand();
		command.setCommands(new String[]{"commandthatfails"});
		command.addMusts(new String[]{"will never come"});
		command.setTimeout(300);
		station.setForceIgnoreAnyErrors(true);
		station.handleCliCommand("sending a command that fails", command);
	}

}
