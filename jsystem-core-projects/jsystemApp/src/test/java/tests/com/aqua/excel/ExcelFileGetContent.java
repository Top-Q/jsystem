/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import junit.framework.SystemTestCase;

import com.aqua.excel.ExcelFile;

public class ExcelFileGetContent extends SystemTestCase {
	String fileName = "excel1";
	String sheetName = "sheet1";
	Test test;

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	//@TestProperties(group={"aaa","ddd","eee"})
	public void testGetSheetContent() throws Exception {
		String[][] write = new String[][] { { "00", "01", "02" }, { "10" }, {},
				{ "30", "31", "32", "33", "34", "35", "36", "37", "38", "39" }, { "40", "41" },
				{ "50", "51", "52", "53", "54", "55", "56", "57", "58" }, { "60", "61", "62", "63", "64", "65", "66" }, {},
				{ "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" } };

		setExcelFileContent(write, null);
		sleep(5000);
		
		ExcelFile excelFile = ExcelFile.getInstance(fileName, true);
		
		int num = excelFile.getNumOfRows();
		report.report("Write Data Has "+write.length+" Rows, Actual Rows Found  : " + num, (num == write.length));
		
		for(int i=0; i<write.length; i++){
			num = excelFile.getNumOfCells(i);
			report.report("Write row no."+i+" Has "+write[i].length+" Elements, Elements Found  : " + num, (num == write[i].length));
		}
		
		String[][] read = excelFile.getSheetContent();
		
		for(int i=0; i<write.length; i++){
			if(write[i] !=null){
				StringBuffer sbWrite = new StringBuffer();
				StringBuffer sbRead = new StringBuffer();
				for(int j=0; j<write[i].length; j++){
					sbWrite.append(write[i][j] + ";");
					sbRead.append(read[i][j] + ";");
				}
				report.report("write : "+sbWrite.toString()+", read: "+sbRead.toString(),(sbWrite.toString().compareTo(sbRead.toString()) == 0));
			}else if(read[i]!=null){
				report.report("Write row no."+i+" is null but read row no."+i+" is not null", false);
			}
		}
		if(write.length != read.length){
			report.report("read and write data are not equal", false);
		}
	}
	
	public void testUseTest(){
		if(test!= null){
			report.report("S1: " + test.getS1());
			report.report("L1: " + test.getL1());
		}
	}
	StringBuffer buf = new StringBuffer();
	public StringBuffer getBuf() {
		return buf;
	}

	public void setBuf(StringBuffer buf) {
		this.buf = buf;
	}
	String value1;

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	//@TestProperties(group={"fff","zzz","eee"})
	public void testGetSheetContentForSpecificSheet() throws Exception {
		String[][] write = new String[][] { { "00", "01", "02" }, { "10" }, {},
				{ "30", "31", "32", "33", "34", "35", "36", "37", "38", "39" }, { "40", "41" },
				{ "50", "51", "52", "53", "54", "55", "56", "57", "58" }, { "60", "61", "62", "63", "64", "65", "66" }, {},
				{ "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" } };

		setExcelFileContent(write, sheetName);
		sleep(5000);
		
		ExcelFile excelFile = ExcelFile.getInstance(fileName, true);

		int num = excelFile.getNumOfRows(sheetName);
		report.report("Write Data Has "+write.length+" Rows, Actual Rows Found  : " + num, (num == write.length));
		
		for(int i=0; i<write.length; i++){
			num = excelFile.getNumOfCells(sheetName, i);
			report.report("Write row no."+i+" Has "+write[i].length+" Elements, Elements Found  : " + num, (num == write[i].length));
		}
		
		String[][] read = excelFile.getSheetContent(sheetName);
		
		for(int i=0; i<write.length; i++){
			if(write[i] !=null){
				StringBuffer sbWrite = new StringBuffer();
				StringBuffer sbRead = new StringBuffer();
				for(int j=0; j<write[i].length; j++){
					sbWrite.append(write[i][j] + ";");
					sbRead.append(read[i][j] + ";");
				}
				report.report("write : "+sbWrite.toString()+", read: "+sbRead.toString(),(sbWrite.toString().compareTo(sbRead.toString()) == 0));
			}else if(read[i]!=null){
				report.report("Write row no."+i+" is null but read row no."+i+" is not null", false);
			}
		}
		if(write.length != read.length){
			report.report("read and write data are not equal", false);
		}
	}

	public void testGetRowContent() throws Exception {
		String[][] write = new String[][] { { "00", "01", "02" }, { "10" }, {},
				{ "30", "31", "32", "33", "34", "35", "36", "37", "38", "39" }, { "40", "41" },
				{ "50", "51", "52", "53", "54", "55", "56", "57", "58" }, { "60", "61", "62", "63", "64", "65", "66" }, {},
				{ "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" } };

		setExcelFileContent(write, null);
		sleep(5000);
		
		ExcelFile excelFile = ExcelFile.getInstance(fileName, true);
		StringBuffer sbWrite;
		StringBuffer sbRead;
		
		for(int i=0; i<write.length; i++){
			sbWrite = new StringBuffer();
			sbRead = new StringBuffer();
			String[] read = excelFile.getRowContent(i);
			for(int j=0; j<write[i].length; j++){
				sbWrite.append(write[i][j] + ";");
				sbRead.append(read[j] + ";");
			}
			report.report("Row No."+i+" : write : "+sbWrite.toString()+", read: "+sbRead.toString(),(sbWrite.toString().compareTo(sbRead.toString()) == 0));
		}
	}
	
	public void testGetRowContentForSpecificSheet() throws Exception {
		String[][] write = new String[][] { { "00", "01", "02" }, { "10" }, {},
				{ "30", "31", "32", "33", "34", "35", "36", "37", "38", "39" }, { "40", "41" },
				{ "50", "51", "52", "53", "54", "55", "56", "57", "58" }, { "60", "61", "62", "63", "64", "65", "66" }, {},
				{ "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" } };

		setExcelFileContent(write, sheetName);
		sleep(5000);
		
		ExcelFile excelFile = ExcelFile.getInstance(fileName, true);
		StringBuffer sbWrite;
		StringBuffer sbRead;
		
		for(int i=0; i<write.length; i++){
			sbWrite = new StringBuffer();
			sbRead = new StringBuffer();
			String[] read = excelFile.getRowContent(sheetName, i);
			for(int j=0; j<write[i].length; j++){
				sbWrite.append(write[i][j] + ";");
				sbRead.append(read[j] + ";");
			}
			report.report("Row No."+i+" : write : "+sbWrite.toString()+", read: "+sbRead.toString(),(sbWrite.toString().compareTo(sbRead.toString()) == 0));
		}
	}
	
	protected void setExcelFileContent(String[][] rows, String sheet) throws Exception {
		if(sheet == null){
			ExcelFile excelFile = ExcelFile.getInstance(fileName, true);
			for (int i = 0; i < rows.length; i++) {
				excelFile.addRow(rows[i]);
			}
		}else{
			ExcelFile excelFile = ExcelFile.getInstance(fileName, sheet, true);
			excelFile.addSheet(sheet);
			for (int i = 0; i < rows.length; i++) {
				excelFile.addRow(sheet, rows[i]);
			}
		}

		
	}
}
