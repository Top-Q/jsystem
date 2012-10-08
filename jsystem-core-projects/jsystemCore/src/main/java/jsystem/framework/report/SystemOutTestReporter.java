/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.IOException;

/**
 * Print the reporting to the standard output.
 * 
 * @author Guy Arieli
 */
public class SystemOutTestReporter implements TestReporter {
	public void initReporterManager() throws IOException {
	}

	public boolean asUI() {
		return false;
	}

	public void report(String title, String message, boolean isPass, boolean bold) {
		if (isPass) {
			System.out.println("\n" + title);
		} else {
			System.err.println("\n" + title);
		}
	}

	public String getName() {
		return "SystemOutTestReporter";
	}

	public void init() {

	}

	public void report(String title, String message, int status, boolean bold) {
		report(title, message, status == Reporter.PASS, bold);
		
	}
}
