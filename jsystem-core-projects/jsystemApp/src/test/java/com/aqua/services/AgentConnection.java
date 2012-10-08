/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services;

import java.io.File;
import java.io.IOException;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.treeui.client.RemoteAgentClient;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

/**
 * This class is a system object that contain all SUT data and implements the
 * basic operations on the agent.
 * 
 * @author Guy Chen
 * 
 */

public class AgentConnection extends SystemObjectImpl {
	private String agentHost;
	private String jsystemHomeDir;
	private String agentDir;
	private String agentAutomationRemoteDir;
	private String runAgent;

	public AgentConnection() {
		super();
	}

	public void init() throws Exception {
		report.step("In agent init method");
		super.init();
	}

	/**
	 * Starts the agent
	 */
	
	public void startAgent() throws Exception {

		File f = new File(agentDir + runAgent);
		if (!f.exists()) {
			throw new Exception("exec file not found");
		}
		Command command = new Command();
		command.setCmd(new String[] { agentDir + runAgent });
		command.setDir(new File(agentDir));
		Execute.execute(command, false, true, true, true);

	}

	/**
	 * Create a directory
	 */
	
	public boolean createDirectory(File dstDir) throws IOException {
		if (!dstDir.exists()) {
			dstDir.mkdir();
			return true;
		} else {
			report.report("Directory already exists", 1);
			return false;
		}
	}
	
	/**
	 * Setting the workspace for the agent to run
	 * @param client TODO
	 */
	
	public void workspaceSettings(String sWorkspace, String sScenario, RemoteAgentClient client)
	throws Exception {

		JSystemProperties jsystem = JSystemProperties.getInstance();
		jsystem.setPreference(FrameworkOptions.CURRENT_SCENARIO, sScenario);
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, sWorkspace);	
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public String getJsystemHomeDir() {
		return jsystemHomeDir;
	}

	public void setJsystemHomeDir(String jsystemHomeDir) {
		this.jsystemHomeDir = jsystemHomeDir;
	}

	public String getAgentDir() {
		return agentDir;
	}

	public void setAgentDir(String agentDir) {
		this.agentDir = agentDir;
	}

	public String getAgentAutomationRemoteDir() {
		return agentAutomationRemoteDir;
	}

	public void setAgentAutomationRemoteDir(String workDir) {
		this.agentAutomationRemoteDir = workDir;
	}

	public String getRunAgent() {
		return runAgent;
	}

	public void setRunAgent(String runAgent) {
		this.runAgent = runAgent;
	}

}
