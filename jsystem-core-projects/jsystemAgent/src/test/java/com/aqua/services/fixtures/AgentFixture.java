/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import jsystem.framework.fixture.Fixture;

import org.junit.Ignore;

import com.aqua.services.AgentConnection;

/**
 * This class fixture is responsible on uploading and closing the agent.
 * 
 * @author Guy Chen
 * 
 */
@Ignore("Agent mechanism is deprected")
public class AgentFixture extends Fixture {

	// AgentConnection agentSysObj;
	private AgentConnection agentConnection;

	public AgentFixture() {
	}
	/**
	 * Start and upload the agent
	 */
	public void setUp() throws Exception {
		report.step("Fixture set up");
		agentConnection = (AgentConnection)system.getSystemObject("AgentConnection");
		report.report("Create agent dir");
		agentConnection.createAgentDir();
		report.report("Start agent " );
		agentConnection.startAgent();
		report.step("End of fixture set up");
	}

	public void tearDown() throws Exception {
		agentConnection = (AgentConnection)system.getSystemObject("AgentConnection");
		if (!agentConnection.stopAgent()) {
			report.report("Failed shutting down the agent",false);
		}
	}

	public void failTearDown() throws Exception {
		tearDown();
	}
}
