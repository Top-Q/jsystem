/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.jython;

import java.io.PrintStream;
import java.io.PrintWriter;

import junit.framework.AssertionFailedError;

public class JythonFailedException extends AssertionFailedError {
	private static final long serialVersionUID = 1L;
	
	String errors;
	String message;
	
	public JythonFailedException(String message, String errors) {
		this.message = message;
		this.errors = errors;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		s.println(errors);
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		s.println(errors);
	}
}
