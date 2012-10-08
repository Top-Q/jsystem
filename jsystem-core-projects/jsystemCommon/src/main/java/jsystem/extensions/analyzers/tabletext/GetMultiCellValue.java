/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import java.util.ArrayList;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * The same as TableCellValue but is able to check multi celles.
 * 
 * @author guy.arieli Example: For this table: Index Source IP Destination IP 18
 *         2.1.1.1 1.1.1.1 Use: new TableMultiCellValue("Index", 18, new
 *         String[] {"Source IP", "Destination IP"}, new String[] {"1.1.1.1",
 *         "2.2.2.2"})
 * 
 */
public class GetMultiCellValue extends AnalyzerParameterImpl {
	String keyHeader;

	String[] valueHeaders;

	String[] expectedValues;

	protected TTable table;

	private String foundValue = null;

	public GetMultiCellValue(String keyHeader, String[] valueHeaders, String[] expectedValues) throws Exception {
		this.keyHeader = keyHeader;
		this.valueHeaders = valueHeaders;
		this.expectedValues = expectedValues;
	}

	@Override
	public void analyze() {
		title = "GetMultiCellValue";
		if (valueHeaders.length != expectedValues.length) {
			message = "GetMultiCellValue: keys and expected arrays should be in the same size";
			status = false;
			return;
		}
		try {

			if (testAgainst instanceof TableRepository) {
				table = ((TableRepository) testAgainst).getTable();
			} else {
				table = new Table((String) testAgainst);
			}
		} catch (Exception e) {
			message = "GetMultiCellValue: unable to init table: " + e.getMessage();
			status = false;
			return;
		}
		int rowCount = table.getNumberOfRows();
		ArrayList<Integer> optionalRows = new ArrayList<Integer>();
		for (int i = 0; i < rowCount; i++) {
			optionalRows.add(Integer.valueOf(i));
		}
		try {
			for (int i = 0; i < valueHeaders.length; i++) {
				for (int j = 0; j < optionalRows.size(); j++) {
					if (!table.getCell(((Integer) optionalRows.get(j)).intValue(),
							table.getHeaderFieldIndex(valueHeaders[i])).equals(expectedValues[i])) {
						optionalRows.remove(j);
						j--;
					}
				}
			}
		} catch (Exception e) {
			message = "GetMultiCellValue: unable to init table: " + e.getMessage();
			return;
		}
		if (optionalRows.size() == 0) {
			message = "GetMultiCellValue: The expected value was not found! ";
			return;
		}
		try {
			foundValue = table.getCell(optionalRows.get(0).intValue(), table.getHeaderFieldIndex(keyHeader));
			message = "GetMultiCellValue: Cell Value: " + foundValue + "\n";
			status = true;
		} catch (Exception e) {
			title = "GetMultiCellValue: The expected value was not found! ";
		}
	}

	public String getFoundValue() {
		return foundValue;
	}
}
