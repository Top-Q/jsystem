package regression.generic;

import jsystem.framework.fixture.Fixture;

public class FixtureLoop1 extends Fixture {
	
	public FixtureLoop1(){
		super();
		setParentFixture(FixtureLoop2.class);
	}
	
	public void setUp() {
	}

	public void tearDown() {
	}

	public void failTearDown() {
	}

}
