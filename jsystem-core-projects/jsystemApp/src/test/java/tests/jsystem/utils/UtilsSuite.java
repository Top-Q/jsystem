/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilsSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Utils suite");
        suite.addTestSuite(ClassPathFileTest.class);
        suite.addTestSuite(StringUtilsTest.class);
        return suite;
    }
}
