/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.monitors;

import com.aqua.services.demo.PingMonitor;

import jsystem.framework.monitor.Monitor;
import junit.framework.SystemTestCase;

/**
 * Demonstrates monitors usage.
 */
public class MonitorTest extends SystemTestCase {

	public void testWithMonitor() throws Exception {
		Monitor monitor = new PingMonitor("localhost");
		monitors.startMonitor(monitor);
		Thread.sleep(20000);
		monitors.stopMontior(monitor);
	}
}

