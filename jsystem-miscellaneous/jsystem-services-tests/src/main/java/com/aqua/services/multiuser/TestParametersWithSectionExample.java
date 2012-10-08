/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import jsystem.framework.ParameterProperties;
import junit.framework.SystemTestCase;

/**
 * Test parameters with section example.
 * @author goland
 */
public class TestParametersWithSectionExample extends SystemTestCase {

	private String pingDestination;
	private int packetSize;
	
	
	public void testPing() throws Exception{
		report.report("Test ping");
	}

	public String getPingDestination() {
		return pingDestination;
	}

	/**
	 */
	@ParameterProperties(section="Ping Destination",description="Destination of ping operation")
	public void setPingDestination(String pingDestination) {
		this.pingDestination = pingDestination;
	}

	public int getPacketSize() {
		return packetSize;
	}
	
	/**
	 */
	@ParameterProperties(section="Ping Configuration",
			            description="Size of packet which will be sent in ping message")
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
}
