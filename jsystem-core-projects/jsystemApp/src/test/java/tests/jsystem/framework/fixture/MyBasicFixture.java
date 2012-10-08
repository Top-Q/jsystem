/*
 * Created on Dec 14, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.Fixture;

/**
 * @author guy.arieli
 *
 */
public class MyBasicFixture extends Fixture {
	public void setUp(){
		report.report("MyBasicFixture setup");
	}
	public void tearDown(){
		report.report("MyBasicFixture tearDown");
	}

}
