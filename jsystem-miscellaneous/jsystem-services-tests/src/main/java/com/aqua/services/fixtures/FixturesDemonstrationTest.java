/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import jsystem.framework.fixture.RootFixture;
import junit.framework.SystemTestCase;

public class FixturesDemonstrationTest extends SystemTestCase {

	public FixturesDemonstrationTest(){
		setFixture(ExampleFixture.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp(){
		report.step("------------------ in test setup");
	}
	
	public void testFixturesExample(){
		report.step("------------------ in test");	
	}
	
	public void tearDown(){
		report.step("------------------ in test tearDown");
	}

}
