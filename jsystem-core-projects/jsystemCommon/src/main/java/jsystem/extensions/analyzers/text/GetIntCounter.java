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
public class GetIntCounter extends AnalyzeTextParameter {
	int counter;

	public GetIntCounter(String toFind) {
		super(toFind);
	}

	public void analyze() {
		Pattern p = Pattern.compile("(" + toFind + "\\s*[:\\.]+\\s*(\\d+)" + ")");
		Matcher m = p.matcher(testText);
		message = testText;
		if (!m.find()) {
			status = false;
			title = "GetIntCounter: Counter is not found: " + toFind;
			return;
		}
		// System.out.println("counter is: " + m.group(2));
		counter = Integer.parseInt(m.group(2));
		message = testText.replaceAll(m.group(1), "<b>" + m.group(1) + "</b>");
		title = "Get counter: " + toFind + " value: " + counter;
		status = true;
	}

	public int getCounter() {
		return counter;
	}

}
