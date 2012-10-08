/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.fixture;

import junit.framework.SystemTestCase;
import junit.framework.TestResult;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.fixture.Fixture;
import jsystem.utils.ResultCollector;

import java.util.ArrayList;

public class FixtureManagerTest extends SystemTestCase{
    FixtureManager fixtureManager = null;
    TestResult result = null;
    public void setUp(){
        fixtureManager = FixtureManager.getInstance();
        fixtureManager.initFixtureModel();
        result =  new TestResult();
        ResultCollector.collector.clearResult();
    }
  public void testFoundFixture1() throws Throwable{
        fixtureManager.goTo(Fixture1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1");
        }
        assertEquals(
                "Fixture1-setUp>>",
                ResultCollector.collector.getResult());
    }
    public void testDownToRootAndUp() throws Throwable{
        fixtureManager.initFixtureModel();
        ResultCollector.collector.clearResult();
        fixtureManager.goTo(Fixture1_1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1_1");
        }
        String r = ResultCollector.collector.getResult();
        System.out.println(r);
        assertEquals(
               "Fixture1-setUp>>Fixture1_1-setUp>>",
                r);
        fixtureManager.goTo(Fixture2.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture2");
        }
        assertEquals(
                "Fixture1_1-tearDown>>Fixture1-tearDown>>Fixture2-setUp>>",
                ResultCollector.collector.getResult() );
    }
    public void testDownToRoot() throws Throwable{
        fixtureManager.initFixtureModel();
        fixtureManager.goTo(Fixture1_1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1_1");
        }
        assertEquals(
               "Fixture1-setUp>>Fixture1_1-setUp>>",
                ResultCollector.collector.getResult());
        fixtureManager.goTo(RootFixture.getInstance().getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo root");
        }
        assertEquals(
                "Fixture1_1-tearDown>>Fixture1-tearDown>>",
                ResultCollector.collector.getResult() );
    }
    public void testDownAndUp() throws Throwable{
        fixtureManager.initFixtureModel();
        fixtureManager.goTo(Fixture1_1_1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1_1_1");
        }
        ResultCollector.collector.clearResult();
        fixtureManager.goTo(Fixture1_1_2.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1_1_2");
        }
        assertEquals(
                "Fixture1_1_1-tearDown>>Fixture1_1_2-setUp>>",
                ResultCollector.collector.getResult() );
    }
    public void testFailTearDown() throws Throwable{
        fixtureManager.initFixtureModel();
        fixtureManager.goTo(Fixture1_1_1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to goTo Fixture1_1_1");
        }
        ResultCollector.collector.clearResult();
        fixtureManager.failTo(Fixture1.class.getName());
        if (result.shouldStop() || !result.wasSuccessful()) {
            fail("Fail to failTo Fixture1");
        }
        assertEquals(
                "Fixture1_1_1-tearDown>>Fixture1_1-failTearDown>>",
                ResultCollector.collector.getResult() );
    }
    public void testNotFixtureClass() throws Throwable{
        fixtureManager.initFixtureModel();
        try {
            fixtureManager.goTo(FixtureSuite.class.getName());
        } catch (Exception e) {
            return;
        }
        fail("Exception was expected");
    }

    public void testFixturesTree() throws Throwable{
        fixtureManager.initFixtureModel();
        ArrayList<Fixture> list = fixtureManager.getAllChildrens(new Fixture1());
        for (int index = 0; index < list.size(); index++){
            Fixture currentFixture = list.get(index);
            if (currentFixture.getName().equals("tests.jsystem.framework.fixture.Fixture1_1")){
                return;
            }
        }
        fail("Fixture Fixture1_1 wasn't found");
    }


}
