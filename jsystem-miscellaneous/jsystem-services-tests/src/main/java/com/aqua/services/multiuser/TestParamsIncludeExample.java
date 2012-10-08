/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.io.File;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

/**
 * Test parameters simple example.
 * @author goland
 */
public class TestParamsIncludeExample extends SystemTestCase {

	private String pingDestination;
	private int packetSize;
	
	private File folder;
	
	/**
	 */
	@TestProperties(paramsInclude={"pingDestination","packetSize"})
	public void testPing() throws Exception{
		report.report("Test ping");
	}
	
	/**
	 */
	@TestProperties(paramsInclude={"folder"})
	public void testCompareFolder() throws Exception{
		report.report("Test compare folder");
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

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}
}
