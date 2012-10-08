/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.text;

import junit.framework.TestSuite;
import junit.framework.Test;

public class TextAnalyzeSuite {
    public static Test suite() {
        TestSuite suite= new TestSuite("Text analyze suite");
        suite.addTestSuite(CountTextText.class);
        suite.addTestSuite(FindTextTest.class);
        suite.addTestSuite(TextNotFoundTest.class);
        return suite;
    }
}
