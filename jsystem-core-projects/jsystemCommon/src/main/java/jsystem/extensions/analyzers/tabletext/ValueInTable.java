/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * Check that one of the fields of given table column is the expected value. for
 * example test that the port '0/1' (value) exist in the 'Port' (headerName)
 * column.
 * 
 * Port Status ------ -------- 0/1 Enable 0/2 Disable 0/3 -
 * 
 * @author guy.arieli
 * 
 */
public class ValueInTable extends AnalyzerParameterImpl {

	protected TTable table;

	protected String headerName;

	protected String value;

	protected boolean isRegExp = false;

	public ValueInTable(String headerName, String value) throws Exception {
		this.headerName = headerName;
		this.value = value;
	}

	public ValueInTable(String headerName, String value, boolean isRegxp) throws Exception {
		this.headerName = headerName;
		this.value = value;
		this.isRegExp = isRegxp;
	}

	public void analyze() {
		title = "ValueInTable: Header name:  " + headerName + " value: " + value;
		StringBuffer sb = new StringBuffer("Header name:  " + headerName + "\n" + "Value: " + value + "\n");
		try {
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
		status = false;
		for (int i = 0; i < totalNumberOfRows; i++) {
			String actualCell = table.getCell(i, columnNumber);

			if (isRegExp) {
				Pattern p = Pattern.compile("(" + value + ")");
				Matcher m = p.matcher(actualCell);
				status = m.find();
				if (status) {
					sb.append(testAgainst);
					message = sb.toString();
					message = message.replaceAll(actualCell, "<b>" + actualCell + "</b>");
					break;
				}
			} else {
				if (actualCell.equals(value)) {
					sb.append(testAgainst);
					message = sb.toString();
					status = true;
					break;
				}
			}
		}
	}

}
