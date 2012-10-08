/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Analyzer for counters with textual value: <code> Version name: v_1.3.5</code>
 * 
 * @author guy.arieli
 * 
 */
public class CheckTextCounter extends AnalyzeTextParameter {
	String expected;

	protected boolean caseSensitive = true;

	public CheckTextCounter(String counter, String expected) {
		super(counter);
		this.expected = expected;
	}

	public CheckTextCounter(String toFind, String expected, boolean isCaseSensitive) {
		this(toFind, expected);
		this.caseSensitive = isCaseSensitive;
	}

	public void analyze() {
		Pattern p = null;
		// Pattern.compile("("+ toFind + "\\s*:\\s*(.*))[\\r\\n]");

		if (caseSensitive) {
			p = Pattern.compile("(" + toFind + "\\s*:\\s*(.*))[\\r\\n]");
		} else {
			p = Pattern.compile("(" + toFind + "\\s*:\\s*(.*))[\\r\\n]", Pattern.CASE_INSENSITIVE);
		}

		Matcher m = p.matcher(testText);
		if (!m.find()) {
			status = false;
			title = "Counter: >" + toFind + "< wasn't found";
			message = testText;
			return;
		}
		String actual = m.group(2);
		actual = actual.trim();// trim white spaces and contorl chars
		status = expected.equals(actual);
		title = "Testing counter: " + toFind + " expected value: <" + expected + "> actual: <" + actual + ">";
		message = "Counter name: " + toFind + "\r\n" + "Expected: " + expected + "\r\n" + "Actual: " + actual
				+ "\r\n\r\n" + testText.replaceAll(m.group(1), "<b>" + m.group(1) + "</b>");
	}
}
