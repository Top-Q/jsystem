package regression.generic;

import jsystem.framework.fixture.Fixture;

public class BasicFixture extends Fixture {
	public void setUp(){
		report.step("BasicFixture setUp");
	}
	
	public void tearDown(){
		report.step("BasicFixture tearDown");
	}
	
	public void failTearDown(){
		report.step("BasicFixture failTearDown");
	}
}
