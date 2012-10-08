/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.ArrayList;

import systemobject.terminal.Prompt;

/**
 * This CLI connection enable you to describe the basic cli connection
 * using the SUT only without the need of code.<p>
 * 
 * To use it you can configure 3 connection parameters:<p>
 * <code>commandEndPrompts</code> enable you to set comma delemeted list of prompt
 * for example: "# ,> " will set "# " and "> " as command end prompts.<br>
 * <code>loginPrompt</code> will use the user name when this prompt found.<br>
 * <code>passwordPrompt</code> will use the password when this prompt found.<br>
 * 
 * @author aqua
 *
 */
public class BasicCliConnection extends CliConnectionImpl {
	protected String commandEndPrompts;
	protected String loginPrompt;
	protected String passwordPrompt;
	public Position[] getPositions() {
		return null;
	}

	public Prompt[] getPrompts() {
		ArrayList<Prompt> prompts = new ArrayList<Prompt>();
		if(getCommandEndPrompts() != null){
			String[] pString = getCommandEndPrompts().split(",");
			for(String ps: pString){
				Prompt p = new Prompt();
				p.setCommandEnd(true);
				p.setPrompt(ps);
				prompts.add(p);
			}
		}
		if(getLoginPrompt() != null){
			Prompt p = new Prompt();
			p.setPrompt(getLoginPrompt());
			p.setStringToSend(user);
			prompts.add(p);
		}
		if(getPasswordPrompt() != null){
			Prompt p = new Prompt();
			p.setPrompt(getPasswordPrompt());
			p.setStringToSend(password);
			prompts.add(p);
		}
		
		return prompts.toArray(new Prompt[0]);
	}

	public String getCommandEndPrompts() {
		return commandEndPrompts;
	}

	public void setCommandEndPrompts(String commandEndPrompts) {
		this.commandEndPrompts = commandEndPrompts;
	}

	public String getLoginPrompt() {
		return loginPrompt;
	}

	public void setLoginPrompt(String loginPrompt) {
		this.loginPrompt = loginPrompt;
	}

	public String getPasswordPrompt() {
		return passwordPrompt;
	}

	public void setPasswordPrompt(String passwordPrompt) {
		this.passwordPrompt = passwordPrompt;
	}
}
