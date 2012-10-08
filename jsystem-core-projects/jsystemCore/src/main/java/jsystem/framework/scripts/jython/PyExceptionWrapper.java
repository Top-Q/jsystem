/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.jython;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.python.core.PyException;

/**
 * This class wraps the exceptions thrown by the Jython interpreter
 * so that it's toString and printStackTrace methods return readable
 * values. Those are the functions the reporter uses to show
 * what went wrong during a test.
 * 
 * @author Gooli
 * 
 */

public class PyExceptionWrapper extends PyException {
	private static final long serialVersionUID = 1L;

	String message;
	String stackTrace;

	public PyExceptionWrapper(PyException e) {
		message = String.format("%s: %s", e.type.__str__(), e.value.toString());
		stackTrace = e.toString();
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
		s.println(stackTrace);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		s.println(stackTrace);
	}
}
