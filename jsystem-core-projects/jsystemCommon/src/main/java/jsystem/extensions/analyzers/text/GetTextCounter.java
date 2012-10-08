/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This analyzer is not a classical analyzer. It's used to extruct the counter
 * value and be used int the test.
 * 
 * @author guy.arieli
 * 
 */
public class GetTextCounter extends AnalyzeTextParameter {
	String counter;

	protected boolean caseSensitive = true;

	public GetTextCounter(String toFind) {
		super(toFind);
	}

	public GetTextCounter(String toFind, boolean isCaseSensitive) {
		this(toFind);
		this.caseSensitive = isCaseSensitive;
	}

	public void analyze() {

		Pattern p = null;
		if (caseSensitive) {
			//p = Pattern.compile(toFind + "\\s*[:\\.]+(.+)");
			p = Pattern.compile(toFind + "\\s*[:\\.]+[ \\t\\x0B\\f]*(.*)");
			// [ \\t\\x0B\\f] is space characters without \r\n
			// we don't want to include \r\n for cases like this
			// a : 1
			// b :
			// c : 3 
			// if we will ask for b conter and we will use \s instead of [ \\t\\x0B\\f]
			// we will get c : 3 instead of empty counter
		} else {
			p = Pattern.compile(toFind + "\\s*[:\\.]+[ \\t\\x0B\\f]*(.*)",
					Pattern.CASE_INSENSITIVE);
		}
		Matcher m = p.matcher(testText);
		message = testText;
		if (!m.find()) {
			status = false;
			title = "GetTextCounter is not found: " + toFind;
			return;
		}
		counter = m.group(1);

		title = "Get counter: " + toFind + " value: " + counter;
		status = true;
	}

	public String getCounter() {
		return counter.trim();// return without whitespaces
	}

}
