/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.systemobject;

import jsystem.framework.system.SystemObjectImpl;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnection;

public class MyDeviceWithNestedSOArray extends SystemObjectImpl{
	
	private String message;
	public CliConnection[] connections;
	
	public void init() throws Exception {
		super.init();
	}
	public void close(){
		super.close();
	}
	public void getHelloMessage() throws Exception {
		report.report("Hello Message",getMessage(),true);
	}
	public void dir() throws Exception {
		CliCommand command = new CliCommand("dir");
		connections[0].handleCliCommand("dir",command);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
