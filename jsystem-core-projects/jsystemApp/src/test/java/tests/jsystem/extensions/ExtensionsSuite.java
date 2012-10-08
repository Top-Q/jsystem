/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions;

import junit.framework.Test;
import junit.framework.TestSuite;
import tests.jsystem.extensions.analyzers.AnalyzersSuite;

public class ExtensionsSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Extensions suite");
        suite.addTest(AnalyzersSuite.suite());
        return suite;
    }
}
