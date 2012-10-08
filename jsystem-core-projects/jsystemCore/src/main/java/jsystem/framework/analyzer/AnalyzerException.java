/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.analyzer;

import junit.framework.AssertionFailedError;

/**
 * This exception is thrown if the analyzing gives negative results, or
 * if the analysis process failed.<p>
 * This exception extends the <code>AssertionFailedError</code> and will
 * be seen by the junit runners (like eclipse) as error (as opposed to failure).<p>
 * The exception message will be seen in the reports.<p>
 * 
 * @author Guy Arieli
 */
public class AnalyzerException extends AssertionFailedError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4309232045247843783L;

	public AnalyzerException() {
		super();
	}

	public AnalyzerException(String message) {
		super(message);
	}
}
