/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework;

import junit.framework.SystemTestCase;

public class TestExample extends SystemTestCase {
	
	public void testExample() throws Exception{
		report.step("This is an example of test report");
		report.report("This is the first step", "step 1", true);
		sleep(2000);
		report.report("This is the second step", "step 2", true);
		//assertEquals(true,false);
		//throw new Exception("xxx");
	}
}
