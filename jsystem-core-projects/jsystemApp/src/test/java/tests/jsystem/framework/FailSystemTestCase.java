/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework;

import junit.framework.SystemTestCase;
import tests.jsystem.framework.fixture.Fixture1_1_1;
import jsystem.framework.fixture.RootFixture;

public class FailSystemTestCase extends SystemTestCase{
    public FailSystemTestCase(String name){
        super(name);
        setFixture(Fixture1_1_1.class);
        setTearDownFixture(RootFixture.class);
    }
    public void testRun() throws Exception{
        throw new Exception("Fail");
    }
}
