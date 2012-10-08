/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.Fixture;
import jsystem.utils.ResultCollector;

public class Fixture1 extends Fixture{
    public void setUp() throws Exception {
        ResultCollector.collector.addResult("Fixture1-setUp>>");
    }

    public void tearDown() throws Exception {
        jsystem.utils.ResultCollector.collector.addResult("Fixture1-tearDown>>");
    }
}
