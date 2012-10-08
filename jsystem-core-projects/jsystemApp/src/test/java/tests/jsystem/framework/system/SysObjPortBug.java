/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.system;

import systemobject.tests.Device1;
import systemobject.tests.Port;
import junit.framework.SystemTestCase;

public class SysObjPortBug extends SystemTestCase {
	public void testBug() throws Exception{
		Device1 d1 = (Device1) system.getSystemObject("device1");
		Port p0 = d1.port[0];
		d1 = (Device1) system.getSystemObject("device1");
		assertEquals(d1.port[0], p0);
	}
	
	public void testThatWhenClosingThePortItIsRecreated() throws Exception{
		Device1 d1 = (Device1) system.getSystemObject("device1");
		Port p0 = d1.port[0];
		Port p1 = d1.port[1];
		assertEquals(p0.getPortId(), 0);
		assertEquals(p1.getPortId(), 1);
		p0.close();
		d1 = (Device1) system.getSystemObject("device1");
		assertNotSame(d1.port[0], p0);
		assertSame(d1.port[1], p1);
		assertEquals(d1.port[0].getPortId(), 0);
		assertEquals(d1.port[1].getPortId(), 1);

	}

}
