/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import com.aqua.excel.ExcelFile;

import junit.framework.SystemTestCase;

public class ExcelFileAddSheet extends SystemTestCase {
	public void testAddSheetToFileWithoutWritingBetween() throws Exception {
		ExcelFile excelFile;
		excelFile = ExcelFile.getInstance("excelFileForTest", "First Sheet", true);
		excelFile.addSheet("Second Sheet");
		
		report.step("add row to the first sheet");
		excelFile.addRow("First Sheet", new String[]{"first row in the first sheet"});
		report.step("add row to the second sheet");
		excelFile.addRow("Second Sheet", new String[]{"first row in the second sheet"});
		
	}
	
	public void testAddSheetToFileWithWritingBetween() throws Exception {
		ExcelFile excelFile;
		excelFile = ExcelFile.getInstance("excelFileForTest", "First Sheet", true);
		report.step("add row to the first sheet");
		excelFile.addRow("First Sheet", new String[]{"first row in the first sheet"});
		
		excelFile.addSheet("Second Sheet");
		report.step("add row to the second sheet");
		excelFile.addRow("Second Sheet", new String[]{"first row in the second sheet"});
		
	}
	
	public void testAddSheetToFileAndMoveBetweenThem() throws Exception {
		ExcelFile excelFile;
		excelFile = ExcelFile.getInstance("excelFileForTest", "First Sheet", true);
		report.step("add row to the first sheet");
		excelFile.addRow("First Sheet", new String[]{"first row in the first sheet"});
		
		excelFile.addSheet("Second Sheet");
		report.step("add row to the second sheet");
		excelFile.addRow("Second Sheet", new String[]{"first row in the second sheet"});
		
		excelFile.setCurrentSheetName("First Sheet");
		report.step("add another row to the first sheet");
		excelFile.addRow("First Sheet", new String[]{"second row in the first sheet"});
		
		excelFile.setCurrentSheetName("Second Sheet");
		report.step("add another row to the Second sheet");
		excelFile.addRow("Second Sheet", new String[]{"second row in the second sheet"});
	}
}
