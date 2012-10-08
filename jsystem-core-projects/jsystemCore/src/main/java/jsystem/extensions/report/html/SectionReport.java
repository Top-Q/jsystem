/*
 * Created on Nov 12, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

/**
 * @author guy.arieli
 * 
 */
public class SectionReport extends TestReport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1773729658626044580L;

	boolean isStart;

	public SectionReport(boolean start) {
		this.isStart = start;
	}

	public String toString() {
		if (isStart) {
			return START_SECTION;
		} else {
			return END_SECTION;
		}
	}

	public static final String START_SECTION = "<table width=\"90%\" border=\"1\" cellspacing=\"2\" cellpadding=\"2\" style=\"'border-collapse:collapse;border:none;mso-border-alt:solid black .75pt;mso-padding-alt:0in 5.4pt 0in 5.4pt'\"><tr><td>\n";

	public static final String END_SECTION = "</td></tr></table>\n";

}
