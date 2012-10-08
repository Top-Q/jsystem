/*
 * Created on Dec 14, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.RootFixture;
import junit.framework.SystemTestCase;

/**
 * @author guy.arieli
 *
 */
public class FixtureFailTearDownTest extends SystemTestCase {
	public FixtureFailTearDownTest(){
		super();
		setFixture(MyBasicFixture.class);
		setTearDownFixture(RootFixture.class);
	}
	public FixtureFailTearDownTest(String name){
		super(name);
		setFixture(MyBasicFixture.class);
		setTearDownFixture(RootFixture.class);
	}
	
	public void testFail() throws Exception{
		throw new Exception("this test should fail");
	}

}
