/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Check a counter value, considering the counter title.
 * 
 * Example:<br>
 * Inside IP address: ( counterType )<br>
 * IP Address: ( subCounter ) <br>
 * Default Gateway: <br>
 * Outside IP address: ( counterType )<br>
 * IP Address: ( subCounter ) <br>
 * Default Gateway: <br>
 * 
 * @author ohad.crystal
 */
public class CheckTextSubCounter extends AnalyzeTextParameter {
	String expected;

	String counterTitle;

	String subCounter;

	String counterValue = "";

	public CheckTextSubCounter(String counterTitle, String subCounter, String expected) {
		super(subCounter);// toFind
		this.counterTitle = counterTitle;
		this.expected = expected;
	}

	public void analyze() {

		Pattern p;
		Matcher m;

		message = testText;

		int counerTitleIndex = message.indexOf(counterTitle);

		if (counerTitleIndex == -1) {// Not Found
			status = false;
			title = "title " + counterTitle + "is not found";
			return;
		}

		message = message.substring(counerTitleIndex);
		p = Pattern.compile("(" + toFind + "\\s*:\\s*(.*))[\\r\\n]");
		m = p.matcher(message);

		if (!m.find()) {
			status = false;
			title = "Counter: >" + toFind + "< wasn't found";
			return;
		}

		String actual = m.group(2);
		actual = actual.trim();// trim white spaces and contorl chars
		status = expected.equals(actual);
		title = "Testing counter: " + toFind + " expected value: <" + expected + "> actual: <" + actual + ">";
		message = "CounterTitle: " + counterTitle + "\r\n" + "Counter name: " + toFind + "\r\n" + "Expected: "
				+ expected + "\r\n" + "Actual: " + actual + "\r\n\r\n"
				+ testText.replaceFirst(m.group(1), "<b>" + m.group(1) + "</b>");
	}
}
