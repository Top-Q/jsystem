/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.IOException;

/**
 *	Simple Line break html component 
 * 
 */
public class LineBreak extends Report {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8842344953920085638L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.report.html.Report#toFile(java.lang.String,
	 *      jsystem.extensions.report.html.NameGenerator)
	 */
	public void toFile(NameGenerator generator) throws IOException {
	}

	public String toString() {
		return "<br>\n";
	}

	public void toFile(String directory, NameGenerator generator) throws IOException {
		// TODO Auto-generated method stub

	}

}
