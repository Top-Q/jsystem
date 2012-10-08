package regression.generic;

import jsystem.framework.fixture.Fixture;

public class FixtureLoop2 extends Fixture {
	
	public FixtureLoop2(){
		super();
		setParentFixture(FixtureLoop1.class);
	}
	
	public void setUp() {
	}

	public void tearDown() {
	}

	public void failTearDown() {
	}

}
