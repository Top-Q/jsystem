/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.analyzer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AnalyzerSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Analyzer Tests");
        suite.addTestSuite(AnalyzerTest.class);
        return suite;
    }
}
