/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import jsystem.utils.StringUtils;

/**
 * Analyzer that count text in the input string and compare it with the expected
 * result. Deviation is supported as well. Can return the actual count (so you
 * can use it to find the count and not to analyze it)
 * 
 * @author guy.arieli
 * 
 */
public class CountText extends AnalyzeTextParameter {
	protected int expectedCount = -1;

	protected int deviation = 0;

	private int actualCount = 0;

	private boolean isRegExp = false;

	public CountText(String toFind, int expectedCount) {
		super(toFind);
		this.expectedCount = expectedCount;
	}

	public CountText(String toFind, int count, int deviation) {
		this(toFind, count);
		this.deviation = deviation;
	}

	public CountText(String toFind, int count, int deviation, boolean isRegExp) {
		this(toFind, count, deviation);
		this.isRegExp = isRegExp;
	}

	public void analyze() {
		if (testText == null) {
			title = "Text to analyze is null";
			status = false;
		}
		if (isRegExp) {
			actualCount = StringUtils.countString(testText, toFind, isRegExp);
		} else {
			actualCount = StringUtils.countString(testText, toFind);
		}
		if (deviation > 0) {
			title = "The text: <" + toFind + "> is expected to be found "
					+ expectedCount + " deviation: " + deviation;
		} else {
			title = "The text: <" + toFind + "> is expected to be found "
					+ expectedCount;
		}
		if (actualCount - deviation > expectedCount
				|| actualCount + deviation < expectedCount) {
			status = false;
		} else {
			status = true;
		}

		message = "String to find: " + toFind + "\r\n" + "Expecte count: "
				+ expectedCount + "\r\n" + "Actual count:  " + actualCount
				+ "\r\n\r\n" + testText;
	}

	/**
	 * After execute the analyzer you can get the actual count
	 * 
	 * @return the actual count
	 */
	public int getActualCount() {
		return actualCount;
	}
}
