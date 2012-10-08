/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * Check that all the table cells in a spesific column equal to the expected.
 * The column is selected using it header.
 * 
 * @author guy.arieli
 * 
 */
public class AllHeaderValuesInTable extends AnalyzerParameterImpl {

	protected TTable table;

	protected String headerName;

	protected String expectetValue;

	protected boolean isRegExp = false;

	public AllHeaderValuesInTable(String headerName, String expectetValue) throws Exception {
		this.headerName = headerName;
		this.expectetValue = expectetValue;
		status = true;
	}

	public AllHeaderValuesInTable(String headerName, String expectetValue, boolean isRegxp) throws Exception {
		this.headerName = headerName;
		this.expectetValue = expectetValue;
		this.isRegExp = isRegxp;
		status = true;
	}

	public void analyze() {
		title = "AllHeaderValuesInTable: Header name:  " + headerName + " expected value: " + expectetValue;
		StringBuffer sb = new StringBuffer("Header name:  " + headerName + "\n" + "Expected Value: " + expectetValue
				+ "\n");
		try {
			// table = new Table((String)testAgainst);
			if (testAgainst instanceof TableRepository) {
				table = ((TableRepository) testAgainst).getTable();
			} else {
				table = new Table((String) testAgainst);
			}

		} catch (Exception e) {
			sb.append("Unable to init table: " + e.getMessage() + "\n");
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		int columnNumber = 0;
		try {
			columnNumber = table.getHeaderFieldIndex(headerName);
		} catch (Exception e) {
			sb.append("Unable to header field: " + e.getMessage() + "\n");
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		int totalNumberOfRows = table.getNumberOfRows();
		status = true;
		for (int i = 0; i < (totalNumberOfRows - 1); i++) {
			String actualCell = table.getCell(i, columnNumber);

			if (isRegExp) {
				Pattern p = Pattern.compile("(" + expectetValue + ")");
				Matcher m = p.matcher(actualCell);
				status = m.find();

				if (!status) {

					sb.append("vlaue: " + actualCell + " is different than: " + expectetValue + "\r\n");
					break;
				}
			} else {
				if (!actualCell.equals(expectetValue)) {
					status = false;
					sb.append("vlaue: " + actualCell + " is different than: " + expectetValue + "\r\n");
					break;
				}
			}
		}
		sb.append(testAgainst);
		message = sb.toString();

	}

}
