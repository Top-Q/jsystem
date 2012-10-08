/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * Check the headers of a table.
 * 
 * @author guy.arieli
 * 
 */
public class TableHeaders extends AnalyzerParameterImpl {

	String[] expectedHeaders;

	protected TTable table;

	public TableHeaders(String[] expectedHeaders) {
		this.expectedHeaders = expectedHeaders;
	}

	public void analyze() {
		title = "Analyzing table header";
		StringBuffer sb = new StringBuffer();

		try {
			if (testAgainst instanceof TableRepository) {
				table = ((TableRepository) testAgainst).getTable();
			} else {
				table = new Table((String) testAgainst);
			}

		} catch (Exception e) {
			sb.append("\r\nUnable to init table: " + e.getMessage());
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}

		for (int i = 0; i < expectedHeaders.length; i++) {
			try {
				int index = table.getHeaderFieldIndex(expectedHeaders[i]);
				if (index != i) {
					throw new Exception("value out of order: " + expectedHeaders[i]);
				}
			} catch (Exception e) {
				sb.append("\r\n" + e.getMessage());
				sb.append(testAgainst);
				message = sb.toString();
				status = false;
				return;
			}
		}
		status = true;
		message = (String) testAgainst;
	}
}
