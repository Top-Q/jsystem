/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.system;

import junit.framework.TestSuite;
import junit.framework.Test;

public class SystemSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Blocks Tests");
        suite.addTestSuite(DeviceManagerTest.class);
        return suite;
    }
}
