/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.systemobject;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

import com.aqua.sysobj.conn.CmdConnection;

public class MyDevice extends SystemObjectImpl{
	
	private String fileName;
	
	public void init() throws Exception {
		super.init();
		report.report("Hello world init");
	}
	public void close(){
		report.report("Hello world close");
		super.close();
	}
	public void readFromFile() throws Exception {
		String textFromFile = FileUtils.read(getFileName());
		setTestAgainstObject(textFromFile);
	}
	public void dir() throws Exception {
		CmdConnection conn = new CmdConnection();
		conn.connect();
		conn.sendString("cd " + getFileName(), false);
		Thread.sleep(150);
		String res = conn.read();
		conn.sendString("dir", false);
		Thread.sleep(1500);
		res = conn.read();
		setTestAgainstObject(res);
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
