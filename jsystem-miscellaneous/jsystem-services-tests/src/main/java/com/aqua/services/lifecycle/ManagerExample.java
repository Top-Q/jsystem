/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.lifecycle;

import com.aqua.services.demo.StationsManager;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

/**
 * Demonstrates system and SUT services.
 * Issues that are covered:
 * 
 * 1. init, close
 * 2. lifetime
 * 3. SUT file
 * 4. SUT editor
 * 5. Direct access to SUT
 * 
 */
public class ManagerExample extends SystemTestCase {
	private StationsManager manager;

	
	

	public void setUp() throws Exception {
		manager = (StationsManager)system.getSystemObject("StationsManagerWithRef");
	}

	/**
	 * Test ping operation on remote machine
	 * @params.include pingHost,lifeTime
	 */
	@TestProperties(name="Test ping operation on machine ${pingHost}, with SystemObject lifetime=${lifeTime}")
	public void testPing() throws Exception {
		report.report("is of station 0 is " + manager.stations[0].cliConnection.getHost());
		report.report("is of station 1 is " + manager.stations[1].cliConnection.getHost());
		manager.stations[0].ping("127.0.0.1");
		
	}
}
