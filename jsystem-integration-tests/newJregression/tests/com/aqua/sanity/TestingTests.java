package com.aqua.sanity;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;

import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class TestingTests extends JSysTestCase4UseExistingServer {
	
	public TestingTests() {
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		super.setUp();
	}
	
	@Test
	public void testThatPass()throws Exception{
		analyzer.setTestAgainstObject(1);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,1,0));
	}
	
	@Test
	public void testThatFails()throws Exception{
		analyzer.setTestAgainstObject(1);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,2,0));
	}
}
