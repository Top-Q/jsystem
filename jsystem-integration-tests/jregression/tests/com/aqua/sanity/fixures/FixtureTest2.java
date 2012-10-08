package com.aqua.sanity.fixures;


import junit.framework.SystemTestCase;

/**
 * Test execution issues stop on execution start, stop on execution end,
 * pause and more.
 * @author guy.arieli
 *
 */
public class FixtureTest2 extends SystemTestCase {
	public FixtureTest2(){
		super();
		setFixture(Fixture5.class);
	}
	
	public void testSimpl1() throws Exception{
		assertEquals(Fixture1.VALUE, 1);
		assertEquals(Fixture2.VALUE, 1);
		assertTrue(Fixture3.VALUE == 0 || Fixture3.VALUE == -1);
		assertEquals(Fixture4.VALUE, 1);
		assertEquals(Fixture5.VALUE, 1);
	}
}
