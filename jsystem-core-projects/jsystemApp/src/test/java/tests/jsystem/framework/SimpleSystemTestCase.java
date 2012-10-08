/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework;

import junit.framework.SystemTestCase;
import tests.jsystem.framework.fixture.Fixture1_1_1;

public class SimpleSystemTestCase extends SystemTestCase{
    public SimpleSystemTestCase(String name){
        super(name);
        setFixture(Fixture1_1_1.class);
    }
    public void testRun() throws Exception{
        jsystem.utils.ResultCollector.collector.addResult("SimpleSystemTestCase-testRun>>");
    }
    public void tearDown(){
    }
}
