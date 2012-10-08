/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.IOException;

public class Link extends Report {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2859391394172493597L;

	String href;

	String data;

	String target;

	public Link(String href, String data, String target) {
		this.href = href;
		this.data = data;
		this.target = target;
	}

	public String toString() {
		return "<a href=\"" + href.replace('\\', '/') + "\" target=\"" + target + "\">" + data + "</a><br>\n";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.report.html.Report#toFile(java.lang.String,
	 *      jsystem.extensions.report.html.NameGenerator)
	 */
	public void toFile(NameGenerator generator) throws IOException {

	}

	public void toFile(String directory, NameGenerator generator) throws IOException {
		// TODO Auto-generated method stub

	}
}
