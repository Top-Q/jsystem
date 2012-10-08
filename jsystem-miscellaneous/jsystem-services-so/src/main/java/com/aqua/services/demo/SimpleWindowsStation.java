/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.demo;

import jsystem.framework.system.SystemObjectImpl;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;

public class SimpleWindowsStation extends SystemObjectImpl {
		
	public CliConnectionImpl cliConnection;
	
	public void init() throws Exception {
		super.init();
		report.step("In init method");
	}
	
	public void close(){
		report.step("In close method");
		super.close();
	}
	
	public void mkdir(String folderName) throws Exception {
		CliCommand cmd = new CliCommand("mkdir " + folderName);
		cmd.addErrors("unknown command");
		cliConnection.handleCliCommand("created dir " + folderName, cmd);
		setTestAgainstObject(cliConnection.getTestAgainstObject());
	}

	public void dir(String folderName) throws Exception {
		CliCommand cmd = new CliCommand("dir " + folderName);
		cmd.addErrors("unknown command");
		cliConnection.handleCliCommand("dir " + folderName, cmd);
		setTestAgainstObject(cmd.getResult());
	}

	public void ping(String host) throws Exception {
		CliCommand cmd = new CliCommand("ping " + host);
		cmd.addErrors("unknown command");
		cliConnection.handleCliCommand("ping " + host, cmd);
		setTestAgainstObject(cliConnection.getTestAgainstObject());
	}
}
