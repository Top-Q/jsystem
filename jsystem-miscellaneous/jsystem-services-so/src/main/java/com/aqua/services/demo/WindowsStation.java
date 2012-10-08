/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.demo;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.graph.Graph;
import jsystem.framework.graph.GraphMonitorManager;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;

public class WindowsStation extends SystemObjectImpl {
	
	private String userName;
	private String password;
	
	public CliConnectionImpl cliConnection;
	
	public void init() throws Exception {
		super.init();
		report.step("In init method");
	}
	
	public void close(){
		report.step("In close method");
		super.close();
	}

	public File getFile(String fileName) throws Exception{
		File f	= new File(fileName);
		FileUtils.write(f, "Writing to file " + fileName + " time is " + new Date(),false);
		return f;
	}
	
	public void command(String command) throws Exception {
		CliCommand cmd = new CliCommand(command);
		cmd.addErrors("is not recognized");
		cliConnection.handleCliCommand(command +  " was activated ", cmd);
		setTestAgainstObject(cliConnection.getTestAgainstObject());
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

	public void pingOverTime(String host, long time) throws Exception {
		long startTime = System.currentTimeMillis();
		Graph graph = GraphMonitorManager.getInstance().allocateGraph(
				"Ping over time", "received packets");
		long count = 0;
		while (System.currentTimeMillis() - startTime < time) {
			count++;
			ping(host);
			String out = (String) getTestAgainstObject();
			Pattern p = Pattern.compile("Received = (\\d+)", Pattern.MULTILINE);
			Matcher m = p.matcher(out);
			if (m.find()) {
				int receive = Integer.parseInt(m.group(1));
				graph.add("receive", count, receive);
			}
		}
		graph.show(report);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
