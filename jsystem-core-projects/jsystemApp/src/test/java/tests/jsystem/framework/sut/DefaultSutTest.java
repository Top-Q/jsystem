/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.sut;

import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutImpl;
import jsystem.utils.ClassPathFile;
import junit.framework.SystemTestCase;

public class DefaultSutTest extends SystemTestCase{

	Sut sut;
    
	public void setUp() throws Exception{
        sut = new SutImpl();
        byte[] xml = (new ClassPathFile()).getFile("sut/xml4Test.xml");
        sut.setSutXml(xml,"unknown");
    }
    
	public void testGetMatch() throws Exception{
        assertEquals("Value doesn't much","10.10.10.10", sut.getValue("/sut/device1/ip/text()"));
        //System.out.println(sut.getValue("text(/sut/device1/telnet/prompt/@regExp)"));
    }
	
}
