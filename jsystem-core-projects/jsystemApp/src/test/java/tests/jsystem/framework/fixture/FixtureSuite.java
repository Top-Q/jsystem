/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import junit.framework.Test;
import junit.framework.TestSuite;


public class FixtureSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite= new TestSuite("Fixture Tests");
		suite.addTestSuite(FixtureTest.class);
        suite.addTestSuite(FixtureManagerTest.class);
		return suite;
	}
}