package regression.generic.Tests;

import jsystem.framework.RunProperties;
import junit.framework.SystemTestCase;

/**
 * Simple test for testing jvm creation and distruction.
 */
public class RunModeCounter extends SystemTestCase {
	private static int counter;
	private int expectedCounterValue;
	private int numberToProgress = 1;
	public void testProgressCounter() throws Exception{
		counter+=numberToProgress;
		RunProperties.getInstance().setRunProperty("RunModeCounter", ""+counter);
	}
	
	public void testAssertExpectedCounterValue() throws Exception {
		String expected = RunProperties.getInstance().getRunProperty("RunModeCounter");
		expected = expected == null ? "0" :expected;
		assertEquals(expected,""+expectedCounterValue);
	}

	public int getExpectedCounterValue() {
		return expectedCounterValue;
	}

	public void setExpectedCounterValue(int expectedCounterValue) {
		this.expectedCounterValue = expectedCounterValue;
	}

	public int getNumberToProgress() {
		return numberToProgress;
	}

	public void setNumberToProgress(int numberToProgress) {
		this.numberToProgress = numberToProgress;
	}
}
