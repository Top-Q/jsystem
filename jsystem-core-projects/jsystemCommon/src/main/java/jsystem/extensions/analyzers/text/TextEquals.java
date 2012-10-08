/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

/**
 * Analyze the input text to find full mach. Support regular expressions.
 * 
 * @author guy.arieli
 * 
 */
public class TextEquals extends AnalyzeTextParameter {
	
	protected boolean isRegExp = false;

	/**
	 * 
	 * @param toFind
	 *            The string to find.
	 */
	public TextEquals(String toFind) {
		this(toFind, false);
	}

	/**
	 * 
	 * @param toFind
	 *            The string to find.
	 * @param isRegExp
	 *            is regular expression.
	 */
	public TextEquals(String toFind, boolean isRegExp) {
		super(toFind);
		this.isRegExp = isRegExp;
	}

	public void analyze() {
		if (testText == null) {
			title = "Text to analyze is null";
			status = false;
		}
		message = "Text to find: " + toFind + "\n\nActual text: " + testText;
		if (isRegExp) {
			status = testText.matches(toFind);
		} else {
			status = (testText.equals(toFind));
		}
		if (status) {
			title = "Expected text equals Actual text";
		} else {
			title = "Expected text does not equal Actual text";
		}
	}

}
