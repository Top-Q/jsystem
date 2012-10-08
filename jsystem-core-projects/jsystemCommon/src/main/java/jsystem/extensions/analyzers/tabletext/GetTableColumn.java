/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * This analyzer is used to retrieve a table column using the column header.
 * 
 * After calling the analyzer you should call to getColumn method.
 * 
 * @author guy.arieli
 * 
 */

public class GetTableColumn extends AnalyzerParameterImpl {
	String headerName;

	protected TTable table;

	String[] column;

	private int headerIndex = -1;

	public GetTableColumn(String headerName) throws Exception {
		this.headerName = headerName;
	}

	public GetTableColumn(int headerIndex) throws Exception {
		this.headerIndex = headerIndex;
	}

	public void analyze() {
		if (headerName != null)
			title = "GetTableColumn: header:  " + headerName;
		else
			title = "GetTableColumn: header index:  " + headerIndex;

		StringBuffer sb = new StringBuffer("header:  " + headerName + "\n");
		try {
			if (testAgainst instanceof TableRepository) {
				table = ((TableRepository) testAgainst).getTable();
			} else {
				table = new Table((String) testAgainst);
			}

		} catch (Exception e) {
			e.printStackTrace();
			sb.append("\r\nUnable to init table: " + e.getMessage());
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		try {
			if (headerIndex == -1)
				headerIndex = table.getHeaderFieldIndex(headerName);

			column = table.getColumn(headerIndex);
		} catch (Exception e) {
			sb.append("\r\nUnable to find key row: " + e.getMessage());
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		sb.append(testAgainst);
		message = sb.toString();
		status = true;

	}

	/**
	 * @return Returns the column.
	 */
	public String[] getColumn() {
		return column;
	}
}
