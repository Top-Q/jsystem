/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import junit.framework.Test;
import junit.framework.TestSuite;
import tests.jsystem.framework.report.html.HtmlSuite;

public class ReportSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Reports tests");
        suite.addTest(HtmlSuite.suite());
        suite.addTestSuite(InternalTestTest.class);
        return suite;
    }
}
