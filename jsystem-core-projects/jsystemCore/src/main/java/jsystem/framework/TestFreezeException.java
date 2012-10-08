/*
 * Created on 19/04/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

/**
 * If thrown the tests execution will halt. It's used in cases you would like to
 * stop the test execution without tearDown.
 * 
 * @author guy.arieli
 * 
 */
public class TestFreezeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5819835959014879817L;

	public TestFreezeException() {
		super();
	}

	public TestFreezeException(String msg) {
		super(msg);
	}
	public TestFreezeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public String getMessage(){
		if(getCause() != null){
			return getCause().getMessage();
		}
		return super.getMessage();
	}
	public StackTraceElement[] getStackTrace(){
		if(getCause() != null){
			return getCause().getStackTrace();
		}
		return super.getStackTrace();
	}
}
