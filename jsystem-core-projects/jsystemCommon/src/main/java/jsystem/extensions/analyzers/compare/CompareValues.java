/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.compare;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * The Analyzer knows to compare objects values. For example: <br>
 * <br>
 * <b>dev.analyze(new CompareValues(4));</b><br>
 * <br>
 * 
 * I`m assuming that the actual value is Integer too and my expected value have
 * to be an object.<br>
 * <br>
 * 
 * More examples:<br>
 * <br>
 * 
 * <b>dev.analyze(new CompareValues("aaaa"));</b><br>
 * <br>
 * 
 * <b>dev.analyze(new CompareValues(new Boolean(true)));</b><br>
 * <br>
 * 
 * @author Uri.Koaz
 * 
 */
public class CompareValues extends AnalyzerParameterImpl {
	Object expected;

	public CompareValues(Object expected) {
		this.expected = expected;
	}

	public void analyze() {
		status = false;
		Object actual = testAgainst;
		title = "Compare Values Analyzer, actual = " + actual + " expected = " + expected;

		if (this.expected.equals(actual)) {
			status = true;
		}
	}
}
