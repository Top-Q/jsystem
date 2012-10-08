package regression.generic;

import junit.framework.SystemTestCase;
import jsystem.framework.TestProperties;

public class FlowControlTests extends SystemTestCase {

	private int varA;
	private int varB = 42;
	private String loopString = "a";
	private String expected;
	private String given = "not like expected"; // so default won't be as expected
	private String errorMessage;

	/**
	 * @params.include varA,varB
	 */
	@TestProperties(returnParam={"varA","varB"})
	public void testAddOneAndReturnParams() throws Exception{
		setVarB(getVarB()+1);
		setVarA(getVarA()+1);
	}

	/**
	 * @params.include LoopString
	 */
	@TestProperties(returnParam={"LoopString"})
	public void testAddStringAndReturnParams() throws Exception{
		setLoopString(getLoopString() + ";c");
	}
	
	/*
	 * @params.include varA
	 */
	public void testThatPass() {

	}

	/*
	 * @params.include varA
	 */
	public void testThatFail() throws Exception {
		throw new Exception("Test fail");
	}

	/*
	 * @params.include varA, errorMessage
	 */
	public void testThatAssert() throws Exception {
		throw new AssertionError(getErrorMessage());
	}
	/*
	 * @params.include expected, given
	 */
	public void testAssertValue() throws Exception {
		assertEquals(getExpected(), getGiven());
	}
	/**
	 * @params.include varA
	 */	
	public void testThatDoesntDoAnyThing() throws Exception{
		report.report("Doesn't do anything");
	}

	public int getVarA() {
		return varA;
	}

	public void setVarA(int varA) {
		this.varA = varA;
	}

	public int getVarB() {
		return varB;
	}

	public void setVarB(int varB) {
		this.varB = varB;
	}

	public String getLoopString() {
		return loopString;
	}

	public void setLoopString(String loopString) {
		this.loopString = loopString;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.given = given;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
