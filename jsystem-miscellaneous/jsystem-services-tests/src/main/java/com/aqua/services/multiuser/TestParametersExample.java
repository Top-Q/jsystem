/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import junit.framework.SystemTestCase;

/**
 * Test parameters simple example.
 * @author goland
 */
public class TestParametersExample extends SystemTestCase {

	private String pingDestination;
	private int packetSize;
	
	
	public void testPing() throws Exception{
		report.report("Test ping");
	}

	public String getPingDestination() {
		return pingDestination;
	}

	/**
	 * Destination of ping operation.
	 */
	public void setPingDestination(String pingDestination) {
		this.pingDestination = pingDestination;
	}

	public int getPacketSize() {
		return packetSize;
	}
	
	/**
	 * Size of packet which will be sent in ping message.
	 */
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
}
