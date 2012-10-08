/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.sut;

import java.util.List;

import jsystem.framework.sut.SutFactory;
import junit.framework.Assert;
import junit.framework.SystemTestCase4;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * Test class which demonstrates how to make xpath queries on the SUT file
 * using jsystem API.
 * @author gderazon
 */
public class ReadingDirectlyFromSut extends SystemTestCase4 {
	
	@Before
	public void setSut() throws Exception {
		SutFactory.getInstance().setSut("helloWithNestedSO.xml");
	}
	
	@Test
	public void exampleHowToReadFromSutSingleValue() throws Exception {
		Assert.assertEquals("simpleuser",SutFactory.getInstance().getSutInstance().getValue("sut/helloWorld/connection/user"));
	}
	
	@Test
	public void exampleHowToReadFromSutMultipleValues() throws Exception {
		List<Node> list = SutFactory.getInstance().getSutInstance().getAllValues("sut/helloWorld/connection/*");
		Assert.assertEquals(4,list.size());
		Assert.assertEquals("com.aqua.sysobj.conn.WindowsDefaultCliConnection",list.get(0).getTextContent());
	}	
}
