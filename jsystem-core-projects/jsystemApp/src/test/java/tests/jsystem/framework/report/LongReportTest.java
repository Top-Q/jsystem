/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import junit.framework.SystemTestCase;

public class LongReportTest extends SystemTestCase {
	public void testLongReport(){
		for (int i = 0; i < 1000; i++){
			report.report("report " +i, "report message\n this is the report message. it's present inthe link from the report title", true);
		}
	}

}
