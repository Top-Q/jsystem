/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.lifecycle;

import junit.framework.SystemTestCase;

public class ListenerExample extends SystemTestCase {

	public void setUp() throws Exception {
		system.getSystemObject("listener");
	}

	public void testPass() throws Exception {
		
	}
	
	public void testThatFailsOnAssertion() throws Exception {
		assertTrue(false);
	}

	public void testThatFailsOnException() throws Exception {
		throw new Exception();
	}

	public void testThatFailsOnLog() throws Exception {
		report.report("message",false);
	}

}
