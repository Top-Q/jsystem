/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.system;

import junit.framework.SystemTestCase;
import systemobject.tests.Device1;

public class DeviceManagerTest extends SystemTestCase{
    Device1 device1;
    public void setUp() throws Exception{
        device1 =  (systemobject.tests.Device1)system.getSystemObject("device1");
    }
    public void testGetDevice() throws Exception{
        assertNotNull("Device1 wasn't init", device1);
    }
    public void testInitSubDevice() throws Exception{
        assertNotNull("Telnet sub device wasn't init", device1.telnet);
    }
    public void testDeviceNotClosed(){
        assertFalse("Device shouldn't be closed", device1.isClosed());
    }
    public void testDeviceSetSetter(){
        assertEquals("Fail to set setter", "10.10.10.10", device1.getIp());
    }

    public void testIntSet(){
        assertEquals("Fail to set int setter", 1, device1.telnet.getIndex());
    }
    public void testBooleanSet(){
        assertEquals("Fail to set boolean setter", true, device1.telnet.isDummy());
    }

    public void testLongSet(){
        assertEquals("Fail to set long setter", 40000, device1.telnet.getTimeout());
    }
    public void testPortArrayInit(){
        assertNotNull("Telnet sub device wasn't init", device1.port);
        assertEquals("Array components weren't init properly", 2, device1.port.length);
        assertNotNull("Array components weren't init properly",device1.port[0]);
        assertNotNull("Array components weren't init properly",device1.port[1]);
        assertEquals("Wrong inner field value", 0, device1.port[0].getPortId());
        assertEquals("Wrong inner field value", 1, device1.port[1].getPortId());
    }
}
