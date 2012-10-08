/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This analyzer is used to analyze counter in the following format: <code>
 * In packets ...................... 77
 * </code>
 * 
 * @author guy.arieli
 * 
 */
public class CheckDotTextCounter extends AnalyzeTextParameter {
	String expected;

	public CheckDotTextCounter(String counter, String expected) {
		super(counter);
		this.expected = expected;
	}

	public void analyze() {
		Pattern p = Pattern.compile("(" + toFind + "\\s*\\.+\\s*(.+))[\\r\\n]");
		Matcher m = p.matcher(testText);
		if (!m.find()) {
			status = false;
			title = "Counter: <" + toFind + "> wasn't found";
			message = testText;
			return;
		}
		String actual = m.group(2);
		actual = actual.trim();// trim white spaces and contorl chars
		status = actual.matches(expected);
		title = "Testing counter: " + toFind + " expected value: <" + expected + "> actual: <" + actual + ">";
		message = "Counter name: " + toFind + "\r\n" + "Expected: " + expected + "\r\n" + "Actual: " + actual
				+ "\r\n\r\n" + testText.replaceAll(m.group(1), "<b>" + m.group(1) + "</b>");
	}
}
