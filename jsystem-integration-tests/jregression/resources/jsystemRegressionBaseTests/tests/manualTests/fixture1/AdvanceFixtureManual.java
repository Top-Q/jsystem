package manualTests.fixture1;


import jsystem.framework.fixture.Fixture;

public class AdvanceFixtureManual extends Fixture {

	public AdvanceFixtureManual() {
		super();
		setParentFixture(BasicFixture.class);
	}
	
	public void setUp(){
		report.step("AdvanceFixtureManual setUp");
	}
	
	public void tearDown(){
		report.step("AdvanceFixtureManual tearDown");
	}
	
	public void failTearDown(){
		report.step("AdvanceFixtureManual failTearDown");
	}

}
