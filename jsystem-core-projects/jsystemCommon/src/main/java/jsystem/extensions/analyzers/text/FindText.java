/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find a text in a string. Support regular expressions.
 * 
 * @author Guy Arieli
 */
public class FindText extends AnalyzeTextParameter {
	
	protected boolean isRegExp = false;

	protected boolean caseSensitive = true;

	protected int group = 1;
	
	protected String counter = null;

	/**
	 * 
	 * @param toFind
	 *            The string to find.
	 */
	public FindText(String toFind) {
		super(toFind);
	}

	/**
	 * 
	 * @param toFind
	 *            The string to find.
	 * @param isRegExp
	 *            is regular expression.
	 */
	public FindText(String toFind, boolean isRegExp) {
		this(toFind);
		this.isRegExp = isRegExp;
	}

	/**
	 * Find a text in the output string
	 * 
	 * @param toFind
	 *            The string to find.
	 * @param isRegExp
	 *            is regular expression.
	 */
	public FindText(String toFind, boolean isRegExp, boolean caseSensitive) {
		this(toFind, isRegExp);
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Find a text in the output string
	 * 
	 * @param toFind
	 *            The string to find.
	 * @param isRegExp
	 *            is regular expression.
	 * @param caseSensitive
	 *            is case sensitive
	 * @param group
	 *            group in pattern (java regular expression)
	 */
	public FindText(String toFind, boolean isRegExp, boolean caseSensitive, int group) {
		this(toFind, isRegExp, caseSensitive);
		this.group = group;
	}

	public void analyze() {
		if (testText == null) {
			title = "Text to analyze is null";
			status = false;
		}
		message = "Text to find: " + toFind + System.getProperty("line.separator") + System.getProperty("line.separator") + "Actual text: " + testText;
		String found = toFind;
		if (isRegExp) {

			Pattern p;
			if (caseSensitive) {
				p = Pattern.compile("(" + toFind + ")");
			} else {
				p = Pattern.compile("(" + toFind + ")", Pattern.CASE_INSENSITIVE);
			}
			Matcher m = p.matcher(testText);
			status = m.find();
			if (status) {
				found = m.group(group);
			}
		} else {
			if (caseSensitive) {
				status = (testText.indexOf(toFind) >= 0);
			} else {
				status = (testText.toLowerCase().indexOf(toFind.toLowerCase()) >= 0);
			}
		}
		if (status) {
			title = "The text <" + found + "> was found";
			message = message.replaceAll(toFind, "<b>" + found + "</b>");
			counter = found;
		} else {
			title = "The text <" + toFind + "> was not found";
		}
	}

	public String getCounter() {
		return counter;
	}
}
