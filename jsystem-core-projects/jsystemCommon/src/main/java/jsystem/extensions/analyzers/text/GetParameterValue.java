/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This analyzer recieves a regular expression as it's parameter and finds the
 * requested value. This value can be retrieved by using the getValue() <br>
 * Usage Example: <i>GetParameterValue pv = new
 * GetParameterValue("\\[\\d+\\]\\s(\\d+)\\s*");</i>
 * 
 * @author Yehudit.Nadav
 * @author Keren.Kinsbursky
 * @author Ohad.Crystal
 * 
 */
public class GetParameterValue extends AnalyzeTextParameter {
	String counter;

	String regEx;

	public GetParameterValue(String regEx) {
		super("");
		this.regEx = regEx;
	}

	public void analyze() {
		Pattern p = null;
		p = Pattern.compile(regEx);
		Matcher m = p.matcher(testText);
		message = testText;
		if (!m.find()) {
			status = false;
			title = "GetParameterValue is not found: " + toFind;
			return;
		}
		counter = m.group(1);

		title = "Get parameter value: " + counter;
		status = true;
	}

	public String getValue() {
		return counter.trim();// return without whitespaces
	}

}
