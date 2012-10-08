/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import junit.framework.SystemTestCase;

public class InternalTestTest extends SystemTestCase {

	public void testBasic(){
		report.startReport("myInternalTest", null);
		report.step("Steps in myInternalTest");
		report.endReport();
		report.startReport("myInternalTest2", null);
		report.step("Steps in myInternalTest2");
		report.endReport();
		report.startReport("myInternalTest3", null);
		report.step("Steps in myInternalTest3");
		//report.report("xxx","yyy", false);
		report.endReport();
	}
}
