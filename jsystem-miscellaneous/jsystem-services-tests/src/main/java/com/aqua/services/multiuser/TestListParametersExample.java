/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import junit.framework.SystemTestCase;

/**
 * Test list parameters example.
 * @author goland
 */
public class TestListParametersExample extends SystemTestCase {

	enum PacketSize {
		SMALL,
		MEDIUM,
		BIG;
	}

	private String pingDestination;
	private PacketSize packetSize;
	
	
	public void testPingWithLists() throws Exception{
		report.report("Test ping");
	}

	public String getPingDestination() {
		return pingDestination;
	}

	public String[] getPingDestinationOptions(){
		return new String[]{"127.0.0.1","localhost"};
	}
	
	public void setPingDestination(String pingDestination) {
		this.pingDestination = pingDestination;
	}

	public PacketSize getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(PacketSize packetSize) {
		this.packetSize = packetSize;
	}
}
