/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report.html;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HtmlSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("html reporting tests");
        //suite.addTestSuite(HtmlCollectorTest.class);
        return suite;
    }
}
