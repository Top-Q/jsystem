/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Analyzer for int text counters. For example: <code>
 * In packets : 500
 * Out packets: 100
 * CRC errors : 0
 * </code>
 * The abouve text can be analyze the counter will be 'In packets', The expected
 * value will be 500. This class support deviation as well.
 * 
 * @author guy.arieli
 * 
 */
public class CheckCounter extends AnalyzeTextParameter {
	private int expectedValue;

	private int deviation = 0;

	private int actual = -1;

	public CheckCounter(String counter, int expectedValue, int deviation) {
		super(counter);
		this.expectedValue = expectedValue;
		this.deviation = deviation;
	}

	public CheckCounter(String counter, int expectedValue) {
		this(counter, expectedValue, 0);
	}

	public void analyze() {
		Pattern p = Pattern.compile("((" + toFind + ")\\s*[:\\.]+\\s*(\\d+))");
		Matcher m = p.matcher(testText);
		if (!m.find()) {
			status = false;
			title = "Counter: <" + toFind + "> wasn't found";
			message = testText;
			return;
		}
		actual = Integer.parseInt(m.group(3));
		if (actual - deviation > expectedValue || actual + deviation < expectedValue) {
			status = false;
		} else {
			status = true;
		}
		title = "Testing counter: <" + m.group(2) + "> expected value: " + expectedValue + " actual: " + actual;
		message = "Counter name: " + m.group(2) + "\r\n" + "Expected: " + expectedValue + "\r\n" + "Actual: " + actual
				+ "\r\n" + "Allowed Deviation: " + deviation + "\r\n\r\n"
				+ testText.replaceAll(toFind, "<b>" + m.group(2) + "</b>");
	}

	public int getCounter() {
		return actual;
	}
}
