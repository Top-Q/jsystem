package com.aqua.sanity.fixures;

import jsystem.framework.fixture.Fixture;

public class Fixture1 extends Fixture {
	
	public static int VALUE = -1;
	
	public Fixture1(){
	
	}
	
	public void setUp() throws Exception{
		VALUE = 1;
	}
	
	public void tearDown() throws Exception{
		VALUE=0;
	}

	
}
