/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.ArrayList;

import systemobject.terminal.Prompt;
import systemobject.terminal.VT100FilterInputStream;

/**
 * Default CliConnection for a Cli connection to a linux machine.
 * Protocol is ssh
 * Default port 22
 * @author goland
 */
public class LinuxDefaultCliConnection extends CliConnectionImpl {

	public LinuxDefaultCliConnection(){
		setDump(true);
		setUseTelnetInputStream(true);
		setProtocol("ssh");
		setPort(22);
	}

	public LinuxDefaultCliConnection(String host,String user,String password){
		this();
		setUser(user);
		setPassword(password);
		setHost(host);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
	}
	
	@Override
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
		p.setCommandEnd(true);
		p.setPrompt("# ");
		prompts.add(p);

		p = new Prompt();
		p.setPrompt("login: ");
		p.setStringToSend(getUser());
		prompts.add(p);

		p = new Prompt();
		p.setPrompt("Password: ");
		p.setStringToSend(getPassword());
		prompts.add(p);
		return prompts.toArray(new Prompt[prompts.size()]);
	}

}
