/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.Fixture;

public class SimpleFixture extends Fixture{
    public int lastRunDirection = -1;
    public void setUp() throws Exception {
        lastRunDirection = Fixture.SETUP_DIRECTION;
    }

    public void tearDown() throws Exception {
        lastRunDirection = Fixture.TEARDOWN_DIRECTION;
    }

    public void failTearDown() throws Exception{
        lastRunDirection = Fixture.TEARDOWN_FAIL_DIRECTION;
    }

}
