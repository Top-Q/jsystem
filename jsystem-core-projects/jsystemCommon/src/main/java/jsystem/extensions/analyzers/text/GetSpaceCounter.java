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
public class GetSpaceCounter extends AnalyzeTextParameter {
	String counter;

	public GetSpaceCounter(String toFind) {
		super(toFind);
	}

	public void analyze() {
		Pattern p = Pattern.compile(toFind + "\\s+(.+)");
		Matcher m = p.matcher(testText);
		message = testText;
		if (!m.find()) {
			status = false;
			title = "GetSpaceCounter is not found: " + toFind;
			return;
		}
		counter = m.group(1);
		title = "Get counter: " + toFind + " value: " + counter;
		status = true;
	}

	public String getCounter() {
		return counter;
	}

}
