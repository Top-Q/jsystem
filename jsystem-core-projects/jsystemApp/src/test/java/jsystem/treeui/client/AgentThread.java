/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

/**
 * This class is a thread that uploads the agent.
 *
 * @author Guy Chen
 */
public class AgentThread extends Thread {

	private RemoteAgentClient client;

	public AgentThread(long minPrime, RemoteAgentClient client)
			throws Exception {
		this.client = client;
	}

	/**
	 * Run the scenario
	 */

	public void run() {

		try {
			runClient();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public void runClient() throws Exception {
		this.client.run(RemoteAgentClient.SyncOptions.no);
	}
}
