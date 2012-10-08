/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework;

import junit.framework.Test;
import junit.framework.TestSuite;
import tests.jsystem.framework.fixture.FixtureSuite;
import tests.jsystem.framework.report.ReportSuite;
import tests.jsystem.framework.sut.SutSuite;
import tests.jsystem.framework.system.SystemSuite;
import tests.jsystem.framework.analyzer.AnalyzerSuite;

public class FrameworkSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Framework suite");
        //suite.addTest(SystemTestCaseTest.suite());
        suite.addTest(FixtureSuite.suite());
        suite.addTestSuite(SimpleSystemTestCase.class);
        suite.addTest(ReportSuite.suite());
        suite.addTest(SutSuite.suite());
        suite.addTest(SystemSuite.suite());
        suite.addTest(AnalyzerSuite.suite());
        return suite;
    }
}
