/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import org.junit.Ignore;

import junit.framework.SystemTestCase;

@Ignore("Agent mechanism is deprected")
public class ExecutionClientTest extends SystemTestCase {
	
	private JSystemAgentClient client;
	
	public void setUp() throws Exception {
		client = new JSystemAgentClient("10.0.0.2",8999);
		client.init();
	}
	
	public void testRun() throws Exception  {
		client.run("default","1a941b5c-a9b1-49f6-a37a-707f0d9f2868");
	}

}
