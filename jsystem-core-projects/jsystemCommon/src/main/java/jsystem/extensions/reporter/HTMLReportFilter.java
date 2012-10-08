/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.reporter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Part of implementation for method getCurrentTestFileName in class 
 * HtmlReporterUtils. 
 * 
 * This class filter file names by pattern of report.
 * 
 * @author optier
 *
 */
public class HTMLReportFilter implements FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		//filter for example: report6.html
		return name.matches("report\\d+.html");  
	}

}
