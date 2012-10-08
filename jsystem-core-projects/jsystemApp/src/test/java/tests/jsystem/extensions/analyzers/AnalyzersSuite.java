/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers;

import tests.jsystem.extensions.analyzers.document.ElementFoundTest;
import tests.jsystem.extensions.analyzers.text.TextAnalyzeSuite;
import tests.jsystem.extensions.analyzers.tabletext.TabletextSuite;
import junit.framework.TestSuite;
import junit.framework.Test;

public class AnalyzersSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Analyzers suite");
        suite.addTest(TextAnalyzeSuite.suite());
        suite.addTest(TabletextSuite.suite());
        suite.addTestSuite(ElementFoundTest.class);
        return suite;
    }
}
