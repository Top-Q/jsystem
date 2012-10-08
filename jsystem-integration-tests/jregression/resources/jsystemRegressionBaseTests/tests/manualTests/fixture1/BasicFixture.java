package manualTests.fixture1;

import jsystem.framework.fixture.Fixture;

public class BasicFixture extends Fixture {
	
	public void setUp() {
		report.step("BasicFixtureManual setUp");
	}

	public void tearDown() {
		report.step("BasicFixtureManual tearDown");
	}

	public void failTearDown() {
		report.step("BasicFixtureManual failTearDown");
	}

}
