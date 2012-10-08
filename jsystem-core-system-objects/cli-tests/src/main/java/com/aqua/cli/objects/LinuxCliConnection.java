/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.objects;

import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.Position;

/**
 * 
 * @author Denis.Malinovtsev
 * 
 * The class implements an CLI connection prompts for Linux platform
 *
 */
public class LinuxCliConnection extends CliConnectionImpl {

	public Position[] getPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Prompt[] getPrompts() {
		
		Prompt[] prompts = new Prompt[3];
		prompts[0] = new Prompt();
		prompts[0].setPrompt("login as:");
		prompts[0].setStringToSend(user);
		
		prompts[1] = new Prompt();
		prompts[1].setPrompt("password:");
		prompts[1].setStringToSend(password);
		
		prompts[2] = new Prompt();
		prompts[2].setPrompt("#");
		prompts[2].setCommandEnd(true);

		return prompts;
	}

}
