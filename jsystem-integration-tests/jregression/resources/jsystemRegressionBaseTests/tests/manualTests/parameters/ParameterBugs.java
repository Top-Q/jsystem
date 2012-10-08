package manualTests.parameters;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class ParameterBugs extends SystemTestCase {
	int simple = 99999;

	int test = 111111;

	String examplenew = "Parameterbgbgbgb";

	String hello = "aaabgbgb";

	/**
	 * checking parameters go out as expected
	 */
	@TestProperties(name = "check that you can configure all parameters bug 1056")
	public void testForExample() {
		report.report("Hi. This is my first test to write!");
	}

	public String getExamplenew() {
		return examplenew;
	}

	public void setExamplenew(String example) {
		this.examplenew = example;
	}

	public int getSimple() {
		return simple;
	}

	public void setSimple(int simple) {
		this.simple = simple;
	}

	public String getAbcd() {
		return hello;
	}

	public void setAbcd(String abcd) {
		this.hello = abcd;
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}

}
