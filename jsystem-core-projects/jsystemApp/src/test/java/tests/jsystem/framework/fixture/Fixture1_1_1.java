/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.Fixture;
import jsystem.utils.ResultCollector;

public class Fixture1_1_1 extends Fixture{
    public Fixture1_1_1(){
        setParentFixture(Fixture1_1.class);
        //setFailToFixture(Fixture1.class);
    }
    public void setUp() throws Exception {
        jsystem.utils.ResultCollector.collector.addResult("Fixture1_1_1-setUp>>");
    }

    public void tearDown() throws Exception {
        ResultCollector.collector.addResult("Fixture1_1_1-tearDown>>");
    }
}
