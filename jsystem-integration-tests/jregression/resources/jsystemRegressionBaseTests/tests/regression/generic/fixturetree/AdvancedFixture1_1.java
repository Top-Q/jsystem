package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class AdvancedFixture1_1 extends Fixture {

	public AdvancedFixture1_1() {
		super();
		setParentFixture(AdvancedFixture1.class);
	}

	public void setUp() {
		report.step("AdvancedFixture1_1 setUp");
	}

	public void tearDown() {
		report.step("AdvancedFixture1_1 tearDown");
	}

	public void failTearDown() {
		report.step("AdvancedFixture1_1 failTearDown");
	}
}
