package regression.generic;

import jsystem.framework.fixture.Fixture;

public class AdvanceFixture extends Fixture {
	
	public AdvanceFixture(){
		super();
		setParentFixture(BasicFixture.class);
	}
	
	public void setUp(){
		report.step("AdvanceFixture setUp");
	}
	
	public void tearDown(){
		report.step("AdvanceFixture tearDown");
	}
	
	public void failTearDown(){
		report.step("AdvanceFixture failTearDown");
	}

}
