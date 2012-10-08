package manualTests.fixture1;

import junit.framework.SystemTestCase;

public class ManualFixtureTest extends SystemTestCase {

	public ManualFixtureTest(String name) {
		super(name);
		setFixture(AdvanceFixtureManual.class);
		setTearDownFixture(AdvanceFixtureManual.class);
	}
	
	public void setUp(){
		report.step("test test test: ManualFixtureTest - setup");
	}
	
	public void testThatFail() throws Exception{
		throw new Exception("test test test: ManualFixtureTest - test fail");
	}
	
	public void testThatAssert() throws Exception {
		throw new AssertionError("test test test: ManualFixtureTest - test assert");
	}
	
	public void tearDown(){
		report.step("test test test: ManualFixtureTest - tearDown");
	}

}
