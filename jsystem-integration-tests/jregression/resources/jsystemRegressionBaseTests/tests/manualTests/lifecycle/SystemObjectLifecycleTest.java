package manualTests.lifecycle;

import junit.framework.SystemTestCase;

public class SystemObjectLifecycleTest extends SystemTestCase {
	
	private int numberOfSystemObjects;
	public int getNumberOfSystemObjects() {
		return numberOfSystemObjects;
	}
	public void setNumberOfSystemObjects(int numberOfSystemObjects) {
		this.numberOfSystemObjects = numberOfSystemObjects;
	}
	public SystemObjectLifecycleTest(){
		setFixture(LifecycleFixture.class);
	}
	public void setUp() throws Exception {
		for (int i = 0; i < numberOfSystemObjects;i++){
			system.getSystemObject("lifecycle"+i);
		}	
	}
	
	public void testEmptyTest() throws Exception {
		report.report("Running test ... ");
	}
}
