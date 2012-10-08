/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import tests.jsystem.framework.FrameworkSuite;
import tests.jsystem.utils.UtilsSuite;
import tests.jsystem.extensions.ExtensionsSuite;
import jsystem.framework.JSystemProperties;

public class AllTests {
    
	public static Test suite() {
        JSystemProperties.getInstance();
        TestSuite suite= new TestSuite("All Tests");
        suite.addTest(FrameworkSuite.suite());
        suite.addTest(UtilsSuite.suite());
        suite.addTest(ExtensionsSuite.suite());
        return suite;
    }
	
}
