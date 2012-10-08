/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.tabletext;

import junit.framework.TestSuite;
import junit.framework.Test;

public class TabletextSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Table text suite");
        suite.addTestSuite (TableTest.class);
        suite.addTestSuite (TableCellValueTest.class);
        return suite;
    }
}
