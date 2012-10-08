/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * This analyzer is used to retrieve the value of a table cell using 3
 * parameters: 1. The header of the key. 2. The key. 3. The header of the cell
 * required.
 * 
 * After calling the analyzer you should call to getCellValue method.
 * 
 * @author guy.arieli
 * 
 */
public class GetTableCellValue extends AnalyzerParameterImpl {
	String keyHeader;

	String keyFieldValue;

	String valueHeader;

	String cellValue;

	private int keyHeaderIndex = -1;

	protected TTable table;

	public GetTableCellValue(String keyHeader, String keyFieldValue, String valueHeader) {
		this.keyHeader = keyHeader;
		this.keyFieldValue = keyFieldValue;
		this.valueHeader = valueHeader;
	}

	public GetTableCellValue(int keyHeaderIndex, String keyFieldValue, String valueHeader) {
		this.keyHeaderIndex = keyHeaderIndex;
		this.keyFieldValue = keyFieldValue;
		this.valueHeader = valueHeader;
	}

	public void analyze() {
		title = "TableCellValue: KeyHeader:  " + keyHeader + " keyFieldValue: " + keyFieldValue + " valueHeader: "
				+ valueHeader;
		StringBuffer sb = new StringBuffer("KeyHeader:  " + keyHeader + "\nkeyFieldValue: " + keyFieldValue
				+ "\nvalueHeader: " + valueHeader + "\n");
		int rowNumber = 0;
		try {
			// table = new Table((String)testAgainst);
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
		try {
			if (keyHeaderIndex == -1)
				rowNumber = table.getFirstRowIndex(keyHeader, keyFieldValue);
			else
				rowNumber = table.getFirstRowIndex(keyHeaderIndex, keyFieldValue);

		} catch (Exception e) {
			sb.append("\r\nUnable to find key row: " + e.getMessage());
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		int columnNumber = 0;
		try {
			columnNumber = table.getHeaderFieldIndex(valueHeader);
		} catch (Exception e) {
			sb.append("\r\nUnable to header field: " + e.getMessage());
			sb.append(testAgainst);
			message = sb.toString();
			status = false;
			return;
		}
		cellValue = table.getCell(rowNumber, columnNumber);
		sb.append("Cell Value: " + cellValue + "\n");
		sb.append(testAgainst);
		message = sb.toString();
		status = true;
	}

	public String getCellValue() {
		return cellValue;
	}
}
