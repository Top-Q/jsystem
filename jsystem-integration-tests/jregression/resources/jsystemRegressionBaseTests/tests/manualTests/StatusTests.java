package manualTests;

import regression.analyzersTests.Book;
import regression.analyzersTests.SimpleAnalyzer;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase;

public class StatusTests extends SystemTestCase {
	public Book bk;

	
	/**
	 * Test that  pass
	 */
	public void testPass() {
		report.report("This test status is pass");

	}
 
	/**
	 * Test that give as a warning
	 */
	public void testWithWarning() {
		report.report("warning title", null, Reporter.WARNING);
	}

	/**
	 * Test that fail and throw Exception
	 */
	public void testFailWithException() throws Exception {
		report.report("This test status is fail with Exception");
		throw new Exception("Some error found and throw Exception");
	}

	/**
	 * This test  fail analyze action.
	 */
	public void testFailedAnalyzeThrowException() {
		String bookName = "harry potter";
//		report.report("Text to find: " + "'all my sons' and "
//				+ "text against: '" + bookName + "'");
		//	bk.setTestAgainsObject(bk.getName());
		analyzer.setTestAgainstObject(bookName);
		analyzer.analyze(new SimpleAnalyzer("all my sons"));
		report.step("Got here");
	}
	
	/**
	 * This test  fail analyze action.
	 */
	public void testFailedAnalyzeNoException() {
		String bookName = "harry potter";
//		report.report("Text to find: " + "'all my sons' and "
//				+ "text against: '" + bookName + "'");
		//	bk.setTestAgainsObject(bk.getName());
		analyzer.setTestAgainstObject(bookName);
		analyzer.analyze(new SimpleAnalyzer("all my sons"),false,false);
		
		report.step("Got here");
	}

	/**
	 * This test  fail reporter
	 */
	public void testReportFailToPass() {
		report.report("this report should fail", false);
	}
}