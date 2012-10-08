package sysobj;

import junit.framework.SystemTestCase;

public class ReferenceExampleTest extends SystemTestCase {
	
	
	public void testWithoutReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObject1");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
	}
	
	public void testSimpleReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObject2");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
	}

	public void testIndexReference() throws Exception {
		DummyManager manager = (DummyManager)system.getSystemObject("dummy_manager");
		ExampleSystemObject sysObject = (ExampleSystemObject)system.getSystemObject("device1");		
		assertTrue(manager.internalObjects[0]!=null);
		assertTrue(sysObject.referenceObject!=null);
		assertEquals(manager.internalObjects[0].descriptor,sysObject.referenceObject.descriptor);
	}
	
	public void testNestedReference() throws Exception {
		ExampleSystemObject sysObject = (ExampleSystemObject)system.getSystemObject("device2");
		assertEquals("testname",sysObject.referenceObject.descriptor);
	}

	public void testReferenceWithoverride() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObjectWithOverride");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals("internalObjectWithOverride",sysObject.field2);
	}

	public void testReferenceToReference() throws Exception {
		InternalSysObject sysObject = (InternalSysObject)system.getSystemObject("internalObjectRefToRef");
		assertNotNull(sysObject);
		assertEquals("internalObject1",sysObject.descriptor);
		assertEquals("field2",sysObject.field2);
	}

}
