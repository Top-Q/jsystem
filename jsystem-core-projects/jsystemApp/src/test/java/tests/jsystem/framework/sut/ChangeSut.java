/*
 * Created on Sep 28, 2005
 *
/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.sut;

import tests.sysobj.Obj;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import junit.framework.SystemTestCase;

/**
 * @author guy.arieli
 *
 */
public class ChangeSut extends SystemTestCase {
	public void testChangeSut() throws Exception{
		SutFactory.getInstance().setSut("xml4Test.xml");
		Obj obj = (Obj)system.getSystemObject("obj");
		assertEquals("Guy",obj.getTag());
		SystemManagerImpl.getInstance().closeAllObjects();
		SutFactory.getInstance().setSut("xml4Test2.xml");
		obj = (Obj)system.getSystemObject("obj");
		assertEquals("Yael",obj.getTag());
		SystemManagerImpl.getInstance().closeAllObjects();
		SutFactory.getInstance().setSut("xml4Test.xml");
		obj = (Obj)system.getSystemObject("obj");
		assertEquals("Guy",obj.getTag());
	}
}
