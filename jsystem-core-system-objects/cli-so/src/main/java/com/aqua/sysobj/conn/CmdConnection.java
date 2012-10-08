/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.ArrayList;

import systemobject.terminal.Cli;
import systemobject.terminal.Cmd;
import systemobject.terminal.Prompt;

/**
 * Implementation of the CliConnection interface for working with
 * windows CMD.exe application directly.
 * When activating some command line applications (like wmic), the application
 * doesn't get the output unless the output stream is closed.
 * To support such applications, the <code>cloneOnEveryOperation</code> parameter 
 * was added tho this class.
 * When this parameter is set to true, the output stream of the connection is closed 
 * on every operation. The outcome of this is that a new process is created for every 
 * command that the user activates.
 * The user cn also set isClone() parameter of the CliCommand object.
 * This will have the same affect but only for a single CliCommand.
 * @author goland
 */
public class CmdConnection extends CliConnectionImpl {
	
	private String dir;
	private boolean cloneOnEveryOperation;
	
	public CmdConnection(){
	}

	public CmdConnection(String dir){
		this();
	}
	
	public void init() throws Exception {
		super.init();
	}
	
	public void connect() throws Exception {
		terminal = new Cmd(dir);
		terminal.setPrompts(internalGetPrompts());
		cli = new Cli(terminal);
		setEnterStr("\n");
		((Cmd)terminal).setCloseOutputOnSend(isCloneOnEveryOperation());
		
	}
	public void handleCliCommand(String title,CliCommand command) throws Exception{
		boolean commandClone = command.isClone();
		boolean isCloneOneveryOp = 	isCloneOnEveryOperation();

		if (isCloneOnEveryOperation()){
			command.setClone(true);
		}
		if (command.isClone()){
			setCloneOnEveryOperation(true);
		}
		try {
			super.handleCliCommand(title, command);
		}finally {
			command.setClone(commandClone);
			setCloneOnEveryOperation(isCloneOneveryOp);
		}
	}
	public Position[] getPositions() {
		return null;
	}

	public Prompt[] getPrompts() {
		return internalGetPrompts().toArray(new Prompt[0]);
	}

	private ArrayList<Prompt> internalGetPrompts() {
		ArrayList<Prompt> prompts = new ArrayList<Prompt>();
		Prompt p = new Prompt();
		p = new Prompt();
		p.setPrompt(">");
		p.setCommandEnd(true);
		prompts.add(p);
		return prompts;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isCloneOnEveryOperation() {
		return cloneOnEveryOperation;
	}

	public void setCloneOnEveryOperation(boolean resetOnEveryOperation) {
		this.cloneOnEveryOperation = resetOnEveryOperation;
	}	
	
	public Object clone() throws CloneNotSupportedException{
		CmdConnection connection = (CmdConnection)super.clone();
		connection.connectOnInit = false;
		connection.cloneOnEveryOperation = this.cloneOnEveryOperation;
		return connection;
	}
}
