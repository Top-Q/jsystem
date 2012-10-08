/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.analyzer;

import junit.framework.SystemTestCase;
import systemobject.tests.Device1;
import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.analyzer.AnalyzerException;

public class AnalyzerTest extends SystemTestCase{
    Device1 device;
    public void setUp() throws Exception {
        device = (Device1)system.getSystemObject("device1");
    }
    public void testSetAndAnalyze() throws AnalyzerException {
        device.telnet.dirCommand();
        device.telnet.analyze(new FindText("Program Files"));
    }
}
