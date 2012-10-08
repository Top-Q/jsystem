/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.ArrayList;

import systemobject.terminal.Prompt;
import systemobject.terminal.VT100FilterInputStream;

/**
 * Default CliConnection for a Cli connection to a windows xm/vista machine.
 * Protocol is Telnet.
 * Default port is 23.
 * 
 * @author goland
 */
public class WindowsDefaultCliConnection extends CliConnectionImpl {

	public WindowsDefaultCliConnection(){
		setDump(true);
		setUseTelnetInputStream(true);
		setProtocol("telnet");
		setPort(23);
	}

	public WindowsDefaultCliConnection(String host,String user,String password){
		this();
		setUser(user);
		setPassword(password);
		setHost(host);
	}
	
	public void init() throws Exception {
		super.init();
	}
	
	public void connect() throws Exception {
		super.connect();
		terminal.addFilter(new VT100FilterInputStream());
	}
	
	public Position[] getPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Prompt[] getPrompts() {
		ArrayList<Prompt> prompts = new ArrayList<Prompt>();
		Prompt p = new Prompt();
		p.setPrompt("login:");
		p.setStringToSend(getUser());
		prompts.add(p);
		p = new Prompt();
		p.setPrompt("password:");
		p.setStringToSend(getPassword());
		prompts.add(p);
		p = new Prompt();
		p.setPrompt(">");
		p.setCommandEnd(true);
		prompts.add(p);
		return prompts.toArray(new Prompt[prompts.size()]);
	}

}
