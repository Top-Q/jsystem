package regression.generic;

import jsystem.framework.fixture.RootFixture;
import junit.framework.SystemTestCase;

public class FixtureTest extends SystemTestCase {
	public FixtureTest() {
		super();
		setFixture(AdvanceFixture.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp() {
		report.step("FixtureTest setUp");
	}

	public void testThatPass() {

	}

	public void testThatFail() throws Exception {
		throw new Exception("Test fail");
	}

	public void testThatAssert() throws Exception {
		throw new AssertionError("Test fail");
	}

	public void tearDown() {
		report.step("FixtureTest tearDown");
	}
}
