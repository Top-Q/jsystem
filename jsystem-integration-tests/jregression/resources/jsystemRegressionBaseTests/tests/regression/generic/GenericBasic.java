package regression.generic;

import junit.framework.SystemTestCase;

public class GenericBasic extends SystemTestCase {
	
	public void test1(){
		report.report("This is test no. 1 - Empty test");
	}
	
	public void test2(){
		report.report("This is test no. 2 - Empty test");
	}
	
	public void test3(){
		report.report("This is test no. 3 - Empty test");
	}
	
	/**
	 * Test that should pass
	 */
	public void testShouldPass(){
		report.report("This test should pass");
	}
	
	public void testFailWithError() throws Exception{
		report.report("This test should fail with error");
		throw new Exception("Some error found");
	}
	
	public void testThatRunFor10Sec(){
		sleep(10000);
	}
	
	public void testReportFor10Sec(){
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 10000){
			report.report("Generate reports for 10 sec.", message, true);
			sleep(500);
		}
	}
	
	private String message = "Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those provided by java.util.Hashtable.\n\n" +

	"The general contract of hashCode is:\n\n" +

	"    * Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application.\n" +
	"    * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result.\n" +
	"    * It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then calling the hashCode method on each of the two objects must produce distinct integer results. However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hashtables.\n\n" + 

	"As much as is reasonably practical, the hashCode method defined by class Object does return distinct integers for distinct objects. (This is typically implemented by converting the internal address of the object into an integer, but this implementation technique is not required by the JavaTM programming language.)\n";
}
