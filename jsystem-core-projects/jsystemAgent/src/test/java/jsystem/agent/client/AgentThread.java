/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.agent.client;

import jsystem.runner.agent.clients.JSystemAgentClient;

/**
 * This class is a thread that uploads the agent.
 * @author Guy Chen
 *
 */
public class AgentThread extends Thread {

	private JSystemAgentClient client;

	public AgentThread(JSystemAgentClient client)throws Exception {
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
		this.client.run();
	}
}
