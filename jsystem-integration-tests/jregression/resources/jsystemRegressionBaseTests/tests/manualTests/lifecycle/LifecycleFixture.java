package manualTests.lifecycle;

import jsystem.framework.fixture.Fixture;

public class LifecycleFixture extends Fixture {
	
	public LifecycleFixture(){
		super();
	}
	
	public void setUp() throws Exception{
		if (SystemObjectForCheckingLifecycle.LIFECYCLE_FILE.exists() && !SystemObjectForCheckingLifecycle.LIFECYCLE_FILE.delete()){
			throw new Exception("Failed deleting lifecycle properties file");
		}
		report.report("Running fixure " + getClass().getName());
	}

	public void tearDown() {
	}

	public void failTearDown() {
	}

}
