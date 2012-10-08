/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckSpaceCounter extends AnalyzeTextParameter {

	String expected;

	public CheckSpaceCounter(String counter, String expected) {
		super(counter);
		this.expected = expected;
	}

	public void analyze() {
		Pattern p = Pattern.compile("(" + toFind + "\\s+(.*))[\\r\\n]");
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
