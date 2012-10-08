/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.agent.client;

import java.io.File;

import jsystem.framework.RunProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.runner.agent.server.RunnerEngineExecutionState;
import junit.framework.SystemTestCase;

import com.aqua.services.AgentConnection;

public class RemoteAgentClientTest extends SystemTestCase {

	private String agentHost = "10.0.0.28:8999";
	public AgentConnection agentSysObj;
	String agentDir = "";;
	
	public void setUp() throws Exception{
		agentDir = RunProperties.getInstance().getRunProperty("agentDir");
	}
	
	public void _testRun() throws Exception {
		agentSysObj.client.run();
	}

	public void testGetVersion() throws Exception {
		agentSysObj.client.shutAgentDown();
	}

	public void testShutdown() throws Exception {
		agentSysObj.client.shutAgentDown();
		
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}
	
	public void testWaitForAgentToBeAvailable () throws Exception{
		
	
		
		System.out.println("The current project name is : " + agentSysObj.client.getCurrentProjectName());
		report.report("The Current Agent ID " + agentSysObj.client.getId(),0);
		report.report("The Agent " + agentSysObj.client.getId() +" working on ver " + agentSysObj.client.getEngineVersion());
		// Setting the active scenario
		agentSysObj.client.setActiveScenario(new Scenario(new File(agentDir
				+ "jsystemApp/resources/jsystemAgentPorject/classes/"),"scenarios/agentScenarioDefault") );
		
		// Delete any leftover from any previous Test
		agentSysObj.deleteFile(agentDir + "/MyFile.txt");
		agentSysObj.client.run();
		
//		if (agentSysObj.checkIfFileExists(agentDir + "/MyFile.txt") == true) {
//			report.report("Agent Performed the test", 0);
//		} else {
//			report.report("Error: Agent was not Performed the test", 1);
//		}
		
		
//		if(client.waitForAgentToBeAvailable(10000)){
//			System.out.println("Agent is Available");
//		}else{
//			System.out.println("Agent is NOT Available");
//		}
	}
	
	/**
	 * NOT FINISHED
	 * @throws Exception
	 */
	
	public void testWaitForExecutionState () throws Exception {
		agentSysObj.workspaceSettings(agentDir
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault");
		if(agentSysObj.client.waitForExecutionState(10000,RunnerEngineExecutionState.idle)){
			System.out.println("Agnet is Ready to work");
		}else{
			System.out.println("Agent is NOT Ready to work");
		}
	}
	

}
