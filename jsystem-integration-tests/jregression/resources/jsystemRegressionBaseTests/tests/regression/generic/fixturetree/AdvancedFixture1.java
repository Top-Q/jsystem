package regression.generic.fixturetree;

import jsystem.framework.fixture.Fixture;

public class AdvancedFixture1 extends Fixture {
	
	public AdvancedFixture1() {
		super();
		setParentFixture(BasicFixture1.class);
	}

	public void setUp() {
		report.step("AdvancedFixture1 setUp");
	}

	public void tearDown() {
		report.step("AdvancedFixture1 tearDown");
	}

	public void failTearDown() {
		report.step("AdvancedFixture1 failTearDown");
	}
}
