package com.aqua.sanity.fixures;

import jsystem.framework.fixture.Fixture;

public class Fixture5 extends Fixture {
	
	public static int VALUE = -1;
	
	public Fixture5(){
		setParentFixture(Fixture4.class);
	}
	
	public void setUp() throws Exception{
		VALUE = 1;
	}
	
	public void tearDown() throws Exception{
		VALUE=0;
	}

	
}
