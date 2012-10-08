/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.reference;

import systemobject.tests.Device1;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;

public class ReferenceExampleTest extends SystemTestCase {
	
	
	public void testWithoutReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObject1");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals(sysObject.getXPath(), "/sut/internalObject1");
		assertEquals(sysObject.getReferenceXPath(),null);
		assertEquals(sysObject.getName(),"internalObject1");
		assertEquals(sysObject.getTagName(),"internalObject1");
	}
	
	public void testSystemObjectArrayIndex() throws Exception{
		Device1 device = (Device1)system.getSystemObject("device1");
		assertEquals("The SO index of the device should be -1",-1,device.getSOArrayIndex());
		assertEquals("The SO index of the port 0 should be 0",0,device.port[0].getSOArrayIndex());
		assertEquals("The SO index of the port 1 should be 1",1,device.port[1].getSOArrayIndex());
	}

	public void testWithoutReferenceTwice() throws Exception {
		ExampleSystemObject sysObject = (ExampleSystemObject)system.getSystemObject("device6");
		assertNotNull(sysObject);
		
		Object obj1 = sysObject;
		Object obj2 = sysObject.referenceObject;
		
		sysObject = (ExampleSystemObject)system.getSystemObject("device6");
		assertNotNull(sysObject);
		
		Object obj3 = sysObject;
		Object obj4 = sysObject.referenceObject;

		assertEquals(obj1, obj3);
		assertEquals(obj2, obj4);
	}

	public void testSimpleReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObject2");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals(sysObject.getXPath(), "/sut/internalObject2");
		assertEquals(sysObject.getName(),"internalObject2");
		assertEquals(sysObject.getReferenceXPath(),"/sut/internalObject1");

	}

	public void testIndexReference() throws Exception {
		DummyManager manager = (DummyManager)system.getSystemObject("dummy_manager");
		ExampleSystemObject sysObject = (ExampleSystemObject)system.getSystemObject("device1");		
		assertTrue(manager.internalObjects[0]!=null);
		assertTrue(sysObject.referenceObject!=null);
		assertEquals(manager.internalObjects[0].descriptor,sysObject.referenceObject.descriptor);
		
		assertEquals(manager.getXPath(), "/sut/dummy_manager");
		assertEquals(manager.getName(),"dummy_manager");
		assertEquals(manager.getReferenceXPath(),null);

		assertEquals(manager.internalObjects[0].getXPath(), "/sut/dummy_manager/internalObjects[@index=\"0\"]");
		assertEquals(manager.internalObjects[0].getName(),"internalObjects[0]");
		assertEquals(manager.internalObjects[0].getReferenceXPath(),null);

		assertEquals(sysObject.getXPath(), "/sut/device1");
		assertEquals(sysObject.getName(),"device1");
		assertEquals(sysObject.getReferenceXPath(),null);

		assertEquals(sysObject.referenceObject.getXPath(), "/sut/device1/referenceObject");
		assertEquals(sysObject.referenceObject.getName(),"referenceObject");
		String xPath = sysObject.referenceObject.getReferenceXPath();
		xPath = StringUtils.replace(xPath, "'", "\"");
		assertEquals(xPath,"/sut/dummy_manager/internalObjects[@index=\"0\"]");

	}
	
	public void testNestedReference() throws Exception {
		ExampleSystemObject sysObject = (ExampleSystemObject)system.getSystemObject("device2");
		assertEquals("testname",sysObject.referenceObject.descriptor);

		assertEquals(sysObject.getXPath(), "/sut/device2");
		assertEquals(sysObject.getName(),"device2");
		assertEquals(sysObject.getReferenceXPath(),"/sut/device1");

		assertEquals(sysObject.referenceObject.getXPath(), "/sut/device2/referenceObject");
		assertEquals(sysObject.referenceObject.getName(),"referenceObject");
		assertEquals(StringUtils.replace(sysObject.referenceObject.getReferenceXPath(), "'", "\""),"/sut/dummy_manager/internalObjects[@index=\"0\"]");

	}

	public void testReferenceWithoverride() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObjectWithOverride");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals("internalObjectWithOverride",sysObject.field2);
		assertEquals(sysObject.getXPath(), "/sut/internalObjectWithOverride");
		assertEquals(sysObject.getName(),"internalObjectWithOverride");
		assertEquals(sysObject.getReferenceXPath(),"/sut/internalObject1");

	}

	public void testReferenceToReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObjectRefToRef");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals("field2",sysObject.field2);
		
		assertEquals(sysObject.getXPath(), "/sut/internalObjectRefToRef");
		assertEquals(sysObject.getName(),"internalObjectRefToRef");
		assertEquals(sysObject.getReferenceXPath(),"/sut/internalObject2");

	}

	public void testReferenceWithNestedSystemObjectAndIndex() throws Exception {
		DummyManager sysObject1 = (DummyManager)system.getSystemObject("dummy_manager_1");
		assertNotNull(sysObject1);
		
		DummyManager sysObject2 = (DummyManager)system.getSystemObject("dummy_manager_2");
		assertNotNull(sysObject2);
		
		assertNotSame(sysObject1, sysObject2);
		assertNotSame(sysObject1.internalObjects[0], sysObject2.internalObjects[0]);
		
		
		assertEquals(sysObject1.getXPath(), "/sut/dummy_manager_1");
		assertEquals(sysObject1.getName(),"dummy_manager_1");
		assertEquals(sysObject1.getReferenceXPath(),"/sut/dummy_manager");
		assertEquals(sysObject1.internalObjects[0].getXPath(), "/sut/dummy_manager_1/internalObjects[@index=\"0\"]");
		assertEquals(sysObject1.internalObjects[0].getName(),"internalObjects[0]");
//		assertEquals(sysObject1.internalObjects[0].getReferenceXPath(),"/sut/dummy_manager/internalObjects[@index='0']");

		assertEquals(sysObject2.getXPath(), "/sut/dummy_manager_2");
		assertEquals(sysObject2.getName(),"dummy_manager_2");
		assertEquals(sysObject2.getReferenceXPath(),"/sut/dummy_manager");
		assertEquals(sysObject2.internalObjects[0].getXPath(), "/sut/dummy_manager_2/internalObjects[@index=\"0\"]");
		assertEquals(sysObject2.internalObjects[0].getName(),"internalObjects[0]");
//		assertEquals(sysObject1.internalObjects[0].getReferenceXPath(),"/sut/dummy_manager/internalObjects[@index='0']");
	}

	public void testReferenceWithNestedSystemObject() throws Exception {
		ExampleSystemObject sysObject1 = (ExampleSystemObject)system.getSystemObject("device6");
		assertNotNull(sysObject1);
		
		ExampleSystemObject sysObject2 = (ExampleSystemObject)system.getSystemObject("device7");
		assertNotNull(sysObject2);

		ExampleSystemObject sysObject3 = (ExampleSystemObject)system.getSystemObject("device8");
		assertNotNull(sysObject3);

		assertNotSame(sysObject1, sysObject2);
		assertNotSame(sysObject2, sysObject3);
		assertNotSame(sysObject1, sysObject3);
	
		assertNotSame(sysObject1.referenceObject, sysObject2.referenceObject);
		assertNotSame(sysObject1.referenceObject, sysObject3.referenceObject);
		assertNotSame(sysObject2.referenceObject, sysObject3.referenceObject);
	}

}
