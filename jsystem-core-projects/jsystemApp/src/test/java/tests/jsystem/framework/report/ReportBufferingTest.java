/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import java.util.List;

import jsystem.framework.report.ReportElement;
import junit.framework.SystemTestCase;

public class ReportBufferingTest extends SystemTestCase {
	public void testReportBuffering(){
		/*
		 * Start to buffer report
		 */
		report.startBufferingReports();
		/*
		 * this report will not be send to the reporters
		 */
		report.step("Example to store report");
		/*
		 * Stop and get the report buffer
		 */
		report.stopBufferingReports();
		List<ReportElement> reports = report.getReportsBuffer();
		
		/*
		 * Clean the buffer
		 * If the user will not do it, it will be done automaticly
		 * in the end of the test.
		 */
		report.clearReportsBuffer();
		
		assertEquals(1, reports.size());
		/*
		 * Send the report back, now it will be send to the reporters
		 */
		report.report(reports.get(0));
	}
	
	public void testBufferedReportsTimes(){
		report.report("before buffer");
		report.startBufferingReports();
		report.report("Report1");
		report.step("This is a step");
		sleep(10000);
		report.stopBufferingReports();
		List<ReportElement>reports = report.getReportsBuffer();
		for(ReportElement re: reports){
			report.report(re);
		}
		report.report("After buffer");
		
	}
}
