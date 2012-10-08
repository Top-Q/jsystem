package com.aqua.sanity.fixures;


import junit.framework.SystemTestCase;

/**
 * Test execution issues stop on execution start, stop on execution end,
 * pause and more.
 * @author guy.arieli
 *
 */
public class FixtureTest1 extends SystemTestCase {
	public FixtureTest1(){
		super();
		setFixture(Fixture3.class);
	}
	
	public void testSimpl1() throws Exception{
		assertEquals(Fixture1.VALUE, 1);
		assertEquals(Fixture2.VALUE, 1);
		assertEquals(Fixture3.VALUE, 1);
	}
}
