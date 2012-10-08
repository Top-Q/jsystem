package flowcontrol;

import java.util.Random;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class ReturnParamWithLoopExample extends SystemTestCase4 {
	
	private String value;
	private int    currentValue;

	@Test
	@TestProperties(returnParam={"value"})
	public void getLoopValue() throws Exception{
		Random rnd = new Random();
		int number = rnd.nextInt(15);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0 ; i < number;i++){
			buffer.append(rnd.nextInt()).append(";");
		}
		setValue(buffer.toString());
	}

	@Test
	@TestProperties(paramsInclude={"currentValue"})
	public void printValue() throws Exception{
		report.report("Value is " + getCurrentValue());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

}
