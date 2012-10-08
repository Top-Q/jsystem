package regression.generic.Tests;

import junit.framework.SystemTestCase;

public class FixtureAsParameterTests extends SystemTestCase {
	public String testFixture;

	// public String[] getTestFixtureOptions() {
	// return new String[] { "BasicFixture1", "BasicFixture2",
	// "AdvancedFixture1", "AdvancedFixture1_1",
	// "AdvancedFixture2", "AdvancedFixture2_1" };
	// }

	public String getTestFixture() {
		return testFixture;
	}

	public void setTestFixture(String testFixture) throws Exception {
		this.testFixture = testFixture;
		setFixture(chooseFixture(testFixture));
	}

	public void setUp() {
		report.step("FixtureAsParameterTests setUp");
	}

	public void testPass() {

	}

	public Class chooseFixture(String fixtureName) throws Exception {
		Class t;
		if (fixtureName.equalsIgnoreCase("root") || fixtureName.equals("")) {
			t = Class.forName("jsystem.framework.fixture.RootFixture");
		} else {
			try {
				t = Class.forName("regression.generic.fixturetree." + fixtureName);
			} catch (Exception e) {
				t = Class.forName("jsystem.framework.fixture.RootFixture");
			}
		}
		return t;
	}
}