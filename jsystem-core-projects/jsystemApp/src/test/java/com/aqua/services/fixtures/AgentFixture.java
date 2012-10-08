/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import com.aqua.services.AgentConnection;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.treeui.client.LocalAgentTest;
import jsystem.treeui.client.RemoteAgentClient;
import jsystem.treeui.client.RunnerEngineManager;

/**
 * This class fixture is responsible on uploading and closing the agent.
 * 
 * @author Guy Chen
 * 
 */

public class AgentFixture extends Fixture {

	AgentConnection agentSysObj;
	
	public AgentFixture() {
	}

	/**
	 * Start and upload the agent
	 */
	public void setUp() throws Exception {
		report.report("fixture set up");
		agentSysObj = (AgentConnection) system
				.getSystemObject("AgentConnection");
		agentSysObj.startAgent();
		sleep(10000);
		RunnerEngineManager.initRunnerEngine(agentSysObj
				.getAgentHost());
		LocalAgentTest.client = ((RemoteAgentClient) RunnerEngineManager
				.getRunnerEngine());
		
	/**
	 * Sync environment with agent first time 
	 */		
		
		LocalAgentTest.sWorkspace  = JSystemProperties.getCurrentTestsPath();
		LocalAgentTest.sScenario = "scenarios/default";
		

		agentSysObj = (AgentConnection) system.getSystemObject("AgentConnection");
		
		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", LocalAgentTest.client);

		LocalAgentTest.client.run(RemoteAgentClient.SyncOptions.yes);

		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, LocalAgentTest.sWorkspace);
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, LocalAgentTest.sScenario);

	}

	public void tearDown() throws Exception {
		LocalAgentTest.client.shutAgentDown();
	}

	public void failTearDown() throws Exception {
		LocalAgentTest.client.shutAgentDown();
	}
}
