package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class BasicFixture2 extends Fixture {

	public void setUp() {
		report.step("BasicFixture2 setUp");
	}

	public void tearDown() {
		report.step("BasicFixture2 tearDown");
	}

	public void failTearDown() {
		report.step("BasicFixture2 failTearDown");
	}
}
