/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.sut;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SutSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Sut suite");
        suite.addTestSuite(DefaultSutTest.class);
        suite.addTestSuite(SutFactoryTest.class);
        return suite;
    }
}
