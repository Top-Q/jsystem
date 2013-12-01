package junit.framework;

import org.junit.Test;

public class ExecutionErrorTests extends SystemTestCase4{
	
	/**
	 * This test is used when we get NoTestsRemainException, while initialize test during run of scenario.
	 * In this case the wanted test is replaced with this test.
	 * Usage can be found in: junit.framework.JUnit4TestAdapterForJSystem.run(TestResult)
	 */
	@Test
	public void testNotFound() throws Exception{
		throw new Exception("Test not found");
	}
}
