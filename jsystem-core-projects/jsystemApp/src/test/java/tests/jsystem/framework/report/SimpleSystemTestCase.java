/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import junit.framework.SystemTestCase;

public class SimpleSystemTestCase extends SystemTestCase{
    public void testReporting(){
        report.report("testing1");
        report.report("testing", "xxxxxx", true);
        report.report("testing", new Exception("xxxx"));
    }
}
