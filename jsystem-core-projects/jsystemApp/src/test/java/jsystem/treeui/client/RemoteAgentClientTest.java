/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import junit.framework.SystemTestCase;

public class RemoteAgentClientTest extends SystemTestCase {

	private String agentHost = "10.0.0.28:8999";
	private RemoteAgentClient client;
	
	public void setUp() throws Exception{
		RunnerEngineManager.initRunnerEngine(agentHost);
		client = (RemoteAgentClient)RunnerEngineManager.getRunnerEngine();
	}
	
	public void _testRun() throws Exception {
		client.run();
	}

	public void testGetVersion() throws Exception {
//		String version = client.getAgentVersion();
//		assertNotNull(version);
		client.shutAgentDown();
	}

	public void testShutdown() throws Exception {
		client.shutAgentDown();
		
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

}
