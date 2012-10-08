/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.tests;

import junit.framework.SystemTestCase;

import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliConnection;
import com.aqua.sysobj.conn.CliConnectionImpl;

public class CliConnectionFeaturesTest extends SystemTestCase {
	private CliApplication station;
	
	
	public void setUp() throws Exception {
		station = (CliApplication)system.getSystemObject("winStation");
	}
	
	public void testClone() throws Exception {
		CliConnection conn = (CliConnection)((CliConnectionImpl)station.conn.cli).clone();
		conn.connect();
	}
}
