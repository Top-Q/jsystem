/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Get a counter value, considering the counter title.
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
public class GetTextSubCounter extends AnalyzeTextParameter {

	String counterTitle;

	String counterValue = "";

	public GetTextSubCounter(String counterType, String subCounter) {
		super(subCounter);
		this.counterTitle = counterType;
	}

	public void analyze() {
		Pattern p;
		Matcher m;

		message = testText;

		int counterTitleIndex = message.indexOf(counterTitle);

		if (counterTitleIndex == -1) {// Not Found
			status = false;
			title = "title " + counterTitle + "is not found";
			return;
		}

		message = message.substring(counterTitleIndex);
		p = Pattern.compile("(" + toFind + "\\s*:\\s*(.*))[\\r\\n]");
		m = p.matcher(message);

		if (!m.find()) {
			status = false;
			title = "GetTextSubCounter is not found: " + toFind;
			return;
		}

		counterValue = m.group(2);
		title = "Getting counter..." + "Counter Title: " + counterTitle + " Sub Couner: " + toFind + " Value: "
				+ counterValue;
		message = "CounterTitle: " + counterTitle + "\r\n" + "Counter name: " + toFind + "\r\n" + "Value: "
				+ counterValue + "\r\n\r\n" + testText.replaceFirst(m.group(1), "<b>" + m.group(1) + "</b>");
		status = true;
	}

	public String getCounter() {
		return counterValue.trim();// return without whitespaces
	}

}
