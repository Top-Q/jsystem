package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class BasicFixture1 extends Fixture {

	public void setUp() {
		report.step("BasicFixture1 setUp");
	}

	public void tearDown() {
		report.step("BasicFixture1 tearDown");
	}

	public void failTearDown() {
		report.step("BasicFixture1 failTearDown");
	}
}
