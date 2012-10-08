package sanity;

import junit.framework.SystemTestCase4;

import org.junit.Test;

public class CurrentNewTest  extends SystemTestCase4  {

	
	@Test
	public void testCurrent(){
		report.report("testCurrent with parameter Current");
	}
	
	@Test
	public void testNew(){
		report.report("testNew with parameter New");
	}
}
