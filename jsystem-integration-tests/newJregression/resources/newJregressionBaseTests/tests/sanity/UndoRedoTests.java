package sanity;

import org.junit.Test;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;

public class UndoRedoTests extends SystemTestCase4 {
	
	private String stringParameter = "";
	private int intParameter = 0;
	private double doubleParameter = 0.0;

	
	@Test
	public void testUndoStringChange(){
		report.report("test that only prints this line");
	}

	//===============================================================================
	public String getStringParameter() {
		return stringParameter;
	}


	public void setStringParameter(String stringParameter) {
		this.stringParameter = stringParameter;
	}

	public String[] getStringParameterOptions(){
		return new String[]{"1","2","3"};
	}

	public int getIntParameter() {
		return intParameter;
	}


	public void setIntParameter(int intParameter) {
		this.intParameter = intParameter;
	}


	public double getDoubleParameter() {
		return doubleParameter;
	}


	public void setDoubleParameter(double doubleParameter) {
		this.doubleParameter = doubleParameter;
	}

}
