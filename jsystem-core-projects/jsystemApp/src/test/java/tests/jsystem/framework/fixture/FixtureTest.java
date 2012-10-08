/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.RootFixture;
import junit.framework.SystemTestCase;

public class FixtureTest extends SystemTestCase{
    SimpleFixture fixture = null;
    public void setUp(){
        fixture = new SimpleFixture();
    }
    public void testParent() {
        assertEquals("Parent should be null", fixture.getParentFixture(),RootFixture.class);
    }

    public void testRunSetup() throws Throwable {
        fixture.run(Fixture.SETUP_DIRECTION);
        assertEquals(fixture.lastRunDirection,Fixture.SETUP_DIRECTION);
    }
    public void testRunTearDown() throws Throwable {
        fixture.run(Fixture.TEARDOWN_DIRECTION);
        assertEquals(fixture.lastRunDirection,Fixture.TEARDOWN_DIRECTION);
    }
    public void testRunFailTearDown() throws Throwable{
        fixture.run(Fixture.TEARDOWN_FAIL_DIRECTION);
        assertEquals(fixture.lastRunDirection,Fixture.TEARDOWN_FAIL_DIRECTION);
    }
}
