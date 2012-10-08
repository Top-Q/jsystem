/*
 * Created on Apr 20, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

/**
 * the class will remove the whole line that starts with the startWith String
 * from the testText String
 * 
 * @author uri.koaz
 * 
 * 
 */
public class RemoveLines extends AnalyzeTextParameter {

	/**
	 * @param startWith
	 */
	public RemoveLines(String startWith) {
		super(startWith);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.analyzer.AnalyzerParameter#analyze()
	 */
	public void analyze() {
		title = "remove: " + toFind + " from result";
		String[] lines = testText.split("[\\r\\n]+");
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].startsWith(toFind)) {
				buf.append(lines[i] + "\r\n");
			}
		}
		analyzer.setTestAgainstObject(buf.toString());
		message = buf.toString();
		status = true;
	}

}
