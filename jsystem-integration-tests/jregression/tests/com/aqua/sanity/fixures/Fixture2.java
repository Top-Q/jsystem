package com.aqua.sanity.fixures;

import jsystem.framework.fixture.Fixture;

public class Fixture2 extends Fixture {
	
	public static int VALUE = -1;
	
	public Fixture2(){
		setParentFixture(Fixture1.class);
	}
	
	public void setUp() throws Exception{
		VALUE = 1;
	}
	
	public void tearDown() throws Exception{
		VALUE=0;
	}

	
}
