package regression.generic;

import junit.framework.SystemTestCase;


public class StringArrayParameters extends SystemTestCase {

	private String[] array={"15","10","1979"};
	
	public void testStringArrayParameters(){
		report.step("first vlaue in the array is : "+array[0]);
		report.step("second vlaue in the array is : "+array[1]);
		report.step("third vlaue in the array is : "+array[2]);
	}

	public String[] getArray() {
		return array;
	}

	public void setArray(String[] array) {
		this.array = array;
	}
}
