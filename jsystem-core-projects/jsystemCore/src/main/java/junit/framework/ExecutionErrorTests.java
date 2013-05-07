package junit.framework;

import org.junit.Test;

import jsystem.framework.report.Reporter;

public class ExecutionErrorTests extends SystemTestCase4{
	
	/**
	 * This test is used when we get NoTestsRemainException, while initialize test during run of scenario.
	 * In this case the wanted test is replaced with this test.
	 * Usage can be found in: junit.framework.JUnit4TestAdapterForJSystem.run(TestResult)
	 */
	@Test
	public void testNotFound(){
		report.report("Test not found", Reporter.WARNING);
	
	}
}
