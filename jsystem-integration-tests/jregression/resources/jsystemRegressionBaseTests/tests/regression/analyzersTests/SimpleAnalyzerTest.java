package regression.analyzersTests;

import junit.framework.SystemTestCase;



public class SimpleAnalyzerTest extends SystemTestCase {
	public Book bk;
	
	
	public void setUp() throws Exception{
		//analysisSut
		bk = (Book)system.getSystemObject("Book");
	 }

    /**
     * This test check good analyze action.
     */
	public void testPassAnalyze(){
		report.report("Text to find: " + "'all my sons' and " + "text against: '" + bk.getName() +"'");
		bk.setTestAgainsObject(bk.getName()); //sets test against text
		SimpleAnalyzer sA=new SimpleAnalyzer("all my sons");
		bk.analyze(sA); //check if the name of the book is all my sons
		assertEquals(true, sA.getStatus()); //check if the status is true
		
		report.step("The status function is: " + sA.getStatus());
	}
	
	/**
     * This test check failed analyze action.
     */
	public void testFailedAnalyze(){
		String bookName="harry potter";
		report.report("Text to find: " + "'all my sons' and " + "text against: '" + bookName +"'" );
		bk.setTestAgainsObject(bk.getName());
		SimpleAnalyzer sA=new SimpleAnalyzer(bookName);
		assertEquals(false,bk.isAnalyzeSuccess(sA)); //check if the analyze is failed 
		
		report.step("The status function is: " + sA.getStatus());
	}
	
	
	/**
     * This test check failed analyze action.
     */
	public void testCheckThrowTrue(){
		bk.setThrowException(true);
		String bookName="harry potter";
		report.report("Text to find: " + "'all my sons' and " + "text against: '" + bookName +"'" );
		bk.setTestAgainsObject(bk.getName());
		SimpleAnalyzer sA=new SimpleAnalyzer(bookName);
		
		report.step("The status function is: " + sA.getStatus());
	}
	
	
	/**
     * This test check failed analyze action.
     */
	public void testCheckThrowFalse(){
		bk.setThrowException(false);
		String bookName="harry potter";
		report.report("Text to find: " + "'all my sons' and " + "test against text: '" + bookName +"'" );
		bk.setTestAgainsObject(bk.getName());
		SimpleAnalyzer sA=new SimpleAnalyzer(bookName);
		bk.analyze(sA);

		report.step("The status function is: " + sA.getStatus());
	}
	
		 
	 
}
