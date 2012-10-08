package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class AdvancedFixture2 extends Fixture {
	public AdvancedFixture2() {
		super();
		setParentFixture(BasicFixture2.class);
	}

	public void setUp() {
		report.step("AdvancedFixture2 setUp");
	}

	public void tearDown() {
		report.step("AdvancedFixture2 tearDown");
	}

	public void failTearDown() {
		report.step("AdvancedFixture2 failTearDown");
	}
}
