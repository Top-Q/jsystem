package sanity;

import junit.framework.SystemTestCase4;

import org.junit.Test;

public class TestSubScenariosParametersChange extends SystemTestCase4 {
	
	private String testString = null;
	private static int counter = 1;
	public TestSubScenariosParametersChange() {
		super();
	}

	@Test
	public void SimpleTest(){
		
	}
	
	@Test
	public void writeParameterToRunProperties()throws Exception{
		report.report("testString is: "+testString);
		runProperties.setRunProperty("testString"+counter, testString);
		counter++;
	}
	
	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}
	
	
}
