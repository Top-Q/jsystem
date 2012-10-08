package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class AdvancedFixture2_1 extends Fixture {

	public AdvancedFixture2_1() {
		super();
		setParentFixture(AdvancedFixture2.class);
	}

	public void setUp() {
		report.step("AdvancedFixture2_1 setUp");
	}

	public void tearDown() {
		report.step("AdvancedFixture2_1 tearDown");
	}

	public void failTearDown() {
		report.step("AdvancedFixture2_1 failTearDown");
	}
}
