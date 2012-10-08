package jsystem.treeui.client;

import junit.framework.SystemTestCase;

public class LocalTest4 extends SystemTestCase {

	public LocalTest4() throws Exception {

	}

	/**
	 * Test that throw exception on purpose
	 */
	public void testThatThrowException() throws Exception {
		throw new Exception("Test that throw exception on purpose");
	}
}
