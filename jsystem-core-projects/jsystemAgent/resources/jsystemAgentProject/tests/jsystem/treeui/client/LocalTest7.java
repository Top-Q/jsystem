package jsystem.treeui.client;


import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

/**
 * This test increase the value in summary.property file.
 * 
 * @author Guy Chen
 * 
 */
public class LocalTest7 extends SystemTestCase {

	public LocalTest7() throws Exception {

	}

	/**
	 * increase the value in summary.property file.
	 */
	public void testIncreaseValue() throws Exception {
		for (int i = 0; i < 50; i++) {
			report.step("Increase value in summary.property file to be : " +  (i+1));
			Object value = Summary.getInstance().getProperty("Number");
			int counter = value == null ? 1 : Integer.parseInt(value.toString()) + 1;
			Summary.getInstance().setProperty("Number", "" + counter);
			sleep(1000);
		}
	}
}