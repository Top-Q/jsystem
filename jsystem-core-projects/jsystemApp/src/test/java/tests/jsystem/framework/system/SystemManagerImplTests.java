/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.system;

import java.util.Vector;

import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import junit.framework.SystemTestCase;

/**
 * Test for jsystem.framework.system.SystemManagerImpl class.
 * 
 * @author Uri.Koaz
 */
public class SystemManagerImplTests extends SystemTestCase {

	/**
	 * The test is testing the method getAllObjects in  
	 * jsystem.framework.system.SystemManagerImpl class.
	 */
	public void testGetAllObjects(){
		SutFactory.getInstance().setSut("xml4Test2.xml");
		
		report.step("Check Method brings all SUT elemnts");
		
		Vector<String> vector = SystemManagerImpl.getAllObjects();
	
		assertEquals(3, vector.size());
		
		assertEquals("device1", vector.get(0));
		assertEquals("obj", vector.get(1));
		assertEquals("notSO", vector.get(2));
		
		report.step("Check Method brings Only System Objects SUT elemnts");
		
		vector = SystemManagerImpl.getAllObjects(true);
	
		assertEquals(2, vector.size());
		
		assertEquals("device1", vector.get(0));
		assertEquals("obj", vector.get(1));
	}
}
