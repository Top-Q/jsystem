package manualTests.properties;

import jsystem.framework.RunProperties;
import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

/*******************************************************************************
 * In Order to check the RunProperties , the Runner must work on run.mode = 2.
 * Run all 3 tests, every test will clean the VM but the "testRanAmount"
 * property should remain.
 * 
 * @author uri.koaz
 * 
 ******************************************************************************/
public class RunPropertiesTests extends SystemTestCase {

	int runPropertyValue = 1;

	String runPropertyKey = "testRanAmount";

	@TestProperties(name = "create a<ul> property with key ${runPropertyKey} , value ${runPropertyValue}")
	public void testRunProperties1() throws Exception {

		report.step("Verfiy that the Property: " + runPropertyKey + " is not exists");

		assertEquals(null, RunProperties.getInstance().getRunProperty(runPropertyKey));

		RunProperties.getInstance().setRunProperty(runPropertyKey, Integer.toString(runPropertyValue));

		report.step("Verify \"" + runPropertyKey + "\"  = " + runPropertyValue);

		assertEquals(RunProperties.getInstance().getRunProperty(runPropertyKey), Integer.toString(runPropertyValue));
	}

	@TestProperties(name = "check a property with key ${runPropertyKey} , value ${runPropertyValue} exists")
	public void testRunProperties2() throws Exception {
		assertEquals(Integer.toString(runPropertyValue), RunProperties.getInstance().getRunProperty(runPropertyKey));
	}

	public int getRunPropertyValue() {
		return runPropertyValue;
	}

	public void setRunPropertyValue(int runPropertyValue) {
		this.runPropertyValue = runPropertyValue;
	}

	public String getRunPropertyKey() {
		return runPropertyKey;
	}

	public void setRunPropertyKey(String runPropertyKey) {
		this.runPropertyKey = runPropertyKey;
	}
}
