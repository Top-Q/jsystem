/*
 * Created on 26/05/2006
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.excel;


public class ExcelFile {
//	public enum EnumColor {
//		AQUA, AUTOMATIC, BLACK, BLUE, BLUE_GREY, BRIGHT_GREEN, BROWN, CORAL, CORNFLOWER_BLUE, DARK_BLUE, DARK_GREEN, DARK_RED, DARK_TEAL, DARK_YELLOW, GOLD, GREEN, GREY_25_PERCENT, GREY_40_PERCENT, GREY_50_PERCENT, GREY_80_PERCENT, INDIGO, LAVENDER, LEMON_CHIFFON, LIGHT_BLUE, LIGHT_CORNFLOWER_BLUE, LIGHT_GREEN, LIGHT_ORANGE, LIGHT_TURQUOISE, LIGHT_YELLOW, LIME, MAROON, OLIVE_GREEN, ORANGE, ORCHID, PALE_BLUE, PINK, PLUM, RED, ROSE, ROYAL_BLUE, SEA_GREEN, SKY_BLUE, TAN, TEAL, TURQUOISE, VIOLET, WHITE, YELLOW;
//	}
//
//	public static final int FORMAT_HEADER = 0;
//
//	public static final int FORMAT_1 = 1;
//
//	public static final int FORMAT_2 = 2;
//
//	String fileName = null;
//
//	boolean append = true;
//
//	File xlsFile = null;
//
//	int[] nextRowIndex = new int[] { 0 };
//
//	String[] sheets = new String[] { "JSystem" };
//
//	/**
//	 * Used to get an instance of "ExcelFile", mainSheetName will be "JSystem"
//	 * 
//	 * @param fileName
//	 *            - the name of the file - if not ends with ".xls" - will add
//	 *            it, if allready exists - will create a new file with the same
//	 *            name and ".backup" as extansion"
//	 * @param append
//	 * @param inLog
//	 *            - true if the file should use the log files directory as the
//	 *            base directory
//	 * @return an instance of ExcelFile
//	 * @throws Exception
//	 */
//	public static ExcelFile getInstance(String fileName, boolean append,
//			boolean inLog) throws Exception {
//		return getInstance(fileName, "JSystem", append, inLog);
//	}
//
//	/**
//	 * Used to get an instance of "ExcelFile", the file will be created with the
//	 * log files directory as the base directory mainSheetName will be "JSystem"
//	 * 
//	 * @param fileName
//	 *            - the name of the file - if not ends with ".xls" - will add
//	 *            it, if allready exists - will create a new file with the same
//	 *            name and ".backup" as extansion"
//	 * @param append
//	 * @return an instance of ExcelFile
//	 * @throws Exception
//	 */
//	public static ExcelFile getInstance(String fileName, boolean append)
//			throws Exception {
//		return getInstance(fileName, append, true);
//	}
//
//	/**
//	 * Used to get an instance of "ExcelFile"
//	 * 
//	 * @param fileName
//	 *            - the name of the file - if not ends with ".xls" - will add
//	 *            it, if allready exists - will create a new file with the same
//	 *            name and ".backup" as extansion"
//	 * @param mainSheetName
//	 *            - name of the first sheet the document will hold
//	 * @param append
//	 * @param inLog
//	 *            - true if the file should use the log files directory as the
//	 *            base directory
//	 * @return an instance of ExcelFile
//	 * @throws Exception
//	 */
//	public static ExcelFile getInstance(String fileName, String mainSheetName,
//			boolean append, boolean inLog) throws Exception {
//		ExcelFile excel = new ExcelFile(fileName, mainSheetName, append, inLog);
//		return excel;
//	}
//
//	/**
//	 * Used to get an instance of "ExcelFile", the file will be created with the
//	 * log files directory as the base directory
//	 * 
//	 * @param fileName
//	 *            - the name of the file - if not ends with ".xls" - will add
//	 *            it, if allready exists - will create a new file with the same
//	 *            name and ".backup" as extansion"
//	 * @param mainSheetName
//	 *            - name of the first sheet the document will hold
//	 * @param append
//	 * @return an instance of ExcelFile
//	 * @throws Exception
//	 */
//	public static ExcelFile getInstance(String fileName, String mainSheetName,
//			boolean append) throws Exception {
//		return getInstance(fileName, mainSheetName, append, true);
//	}
//
//	/**
//	 * adds a new sheet and set the given row in the fisrt row
//	 * 
//	 * @param sheetName
//	 *            name of the new sheet
//	 * @param row
//	 */
//	public void addSheet(String sheetName, Object[] row) {
//		addAndGetNewSheetNameIndex(sheetName);
//		if (row != null) {
//			try {
//				addRow(sheetName, row);
//			} catch (Exception e) {
//				Logger log = Logger.getLogger(ExcelFile.class.getName());
//
//				log.log(Level.SEVERE, "Cant add Sheet");
//			}
//		}
//	}
//
//	/**
//	 * adds a new sheet
//	 * 
//	 * @param sheetName
//	 *            name of the new sheet
//	 */
//	public void addSheet(String sheetName) {
//		addSheet(sheetName, null);
//	}
//
//	/**
//	 * sets the current working sheet, if the sheet does not exist - it will
//	 * throw an Exception
//	 * 
//	 * @param sheetName
//	 *            name of the sheet
//	 * @throws Exception
//	 *             if the requested sheet does not exist
//	 */
//	public void setCurrentSheetName(String sheetName) throws Exception {
//		setCurrentSheetName(sheetName, false);
//	}
//
//	/**
//	 * sets the current working sheet if the sheet does not exist and the
//	 * "appendSheetIfNotExist" parameter is false - it will throw an Exception
//	 * if it is true it will add a new sheet with the given name and will set it
//	 * as the working sheet.
//	 * 
//	 * @param sheetName
//	 *            name of the sheet
//	 * @param appendSheetIfNotExist
//	 *            true to add a new sheet if it does not exist, false for
//	 *            existing sheet only
//	 * @throws Exception
//	 *             if the requested sheet does not exist and the
//	 *             "appendSheetIfNotExist" parameter is false
//	 */
//	public void setCurrentSheetName(String sheetName,
//			boolean appendSheetIfNotExist) throws Exception {
//		int oldMainSheetNextRowIndex = nextRowIndex[0];
//		String oldMainSheetName = sheets[0];
//		int newIndex;
//		try {
//			newIndex = getSheetNameIndex(sheetName); // is sheet does not
//			// exist - it will throw
//			// an Exception
//		} catch (Exception e) {
//			if (appendSheetIfNotExist) {
//				newIndex = addAndGetNewSheetNameIndex(sheetName);
//			} else {
//				throw e;
//			}
//		}
//		int newSheetCurrentIndex = nextRowIndex[newIndex];
//
//		sheets[newIndex] = oldMainSheetName;
//		nextRowIndex[newIndex] = oldMainSheetNextRowIndex;
//
//		sheets[0] = sheetName;
//		nextRowIndex[0] = newSheetCurrentIndex;
//		initLastRowIndex();
//	}
//
//	/**
//	 * add a new header row to the current position of the end of the main sheet
//	 * 
//	 * @param header
//	 *            String array contains the header data
//	 * @throws Exception
//	 */
//	public void addHeader(String[] header) throws Exception {
//		addRow(header, FORMAT_HEADER);
//	}
//
//	/**
//	 * add a new header row to the current position of the end of the given
//	 * sheet
//	 * 
//	 * @param sheetName
//	 *            sheet name to add to
//	 * @param header
//	 *            String array contains the header data
//	 * @throws Exception
//	 */
//	public void addHeader(String sheetName, String[] header) throws Exception {
//		addRow(sheetName, header, FORMAT_HEADER);
//	}
//
//	/**
//	 * add a new row to the current position of the end of the main sheet
//	 * 
//	 * @param row
//	 *            String array contains the row data
//	 * @throws Exception
//	 */
//	public void addRow(Object[] row) throws Exception {
//		addRow(row, -1);
//	}
//
//	/**
//	 * add a new row to the current position of the end of the given sheet
//	 * 
//	 * @param sheetName
//	 *            sheet name to add to
//	 * @param row
//	 *            String array contains the row data
//	 * @throws Exception
//	 */
//	public void addRow(String sheetName, Object[] row) throws Exception {
//		addRow(sheetName, row, -1);
//	}
//
//	/**
//	 * add a new row to the requested position (row index) of the main sheet
//	 * 
//	 * @param row
//	 *            String array contains the row data
//	 * @param rrow
//	 *            requested row index to set the new row in
//	 * @return HSSFRow object - the new row added
//	 * @throws Exception
//	 */
//	public HSSFRow addRow(Object[] row, int rrow) throws Exception {
//		return addRow(sheets[0], row, rrow);
//	}
//
//	/**
//	 * add a new row to the requested position (row index) of the requested
//	 * sheet
//	 * 
//	 * @param sheetName
//	 *            name of the sheet to add the row in
//	 * @param row
//	 *            String array contains the row data
//	 * @param rrow
//	 *            requested row index to set the new row in
//	 * @return HSSFRow object - the new row added
//	 * @throws Exception
//	 */
//	public HSSFRow addRow(String sheetName, Object[] row, int rrow)
//			throws Exception {
//		int sheetIndex = getSheetNameIndex(sheetName);
//		int rowFormat;
//		if (rrow != -1) {
//			rowFormat = rrow;
//		} else {
//			rowFormat = nextRowIndex[sheetIndex];
//		}
//		HSSFWorkbook workbook = getWorkbook(sheets[sheetIndex]);
//		HSSFSheet sheet = workbook.getSheet(sheets[sheetIndex]);
//		HSSFRow xrow = null;
//		xrow = sheet.getRow(nextRowIndex[sheetIndex]);
//		if (xrow == null) {
//			xrow = sheet.createRow(nextRowIndex[sheetIndex]);
//		}
//
//		HSSFFont font = workbook.createFont();
//		HSSFCellStyle cellStyle = workbook.createCellStyle();
//		short backColor;
//		short textColor;
//		if (rowFormat == FORMAT_HEADER) { // header
//			backColor = HSSFColor.DARK_RED.index;
//			textColor = HSSFColor.WHITE.index;
//			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//		} else if (rowFormat % 2 == 0) {
//			backColor = HSSFColor.GREY_25_PERCENT.index;
//			textColor = HSSFColor.BLACK.index;
//		} else {
//			backColor = HSSFColor.WHITE.index;
//			textColor = HSSFColor.BLACK.index;
//		}
//		cellStyle.setFillBackgroundColor(backColor);
//		cellStyle.setFillForegroundColor(backColor);
//		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		font.setColor(textColor);
//		// Create the style
//		cellStyle.setFont(font);
//
//		for (short i = 0; i < row.length; i++) {
//			// System.out.println("Coulm width: " +
//			// sheet.getColumnWidth((short)i));
//			HSSFCell cell = xrow.createCell(i);
//			if (row[i] == null) {
//				continue;
//			}
//			int csize = 8;
//			if (row[i] instanceof Double) {
//				double d = ((Double) row[i]).doubleValue();
//				csize = Double.toString(d).length();
//				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//				cell.setCellValue(d);
//			} else {
//				csize = row[i].toString().length();
//				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
////				HSSFRichTextString hSSFRichTextString = new HSSFRichTextString(row[i].toString());
//				cell.setCellValue(new HSSFRichTextString(row[i].toString()));//setCellValue(row[i].toString());
//			}
//			cell.setCellStyle(cellStyle);
//			short rwidth = sheet.getColumnWidth(i);
//			if (csize > rwidth) {
//				sheet.setColumnWidth(i, (short) ((csize + 1) * 256));
//			}
//		}
//		writeToFile(workbook);
//		nextRowIndex[sheetIndex]++;
//		return xrow;
//	}
//
//	/**
//	 * returns the current sheets' workbook
//	 * 
//	 * @return the current sheets' workbook
//	 * @throws Exception
//	 */
//	private HSSFWorkbook getWorkbook() throws Exception {
//		return getWorkbook(sheets[0]);
//	}
//
//	@SuppressWarnings("finally")
//	private HSSFWorkbook getWorkbook(String sheetName) throws Exception {
//		FileInputStream fis = null;
//		HSSFWorkbook wb = null;
//		try {
//			fis = new FileInputStream(xlsFile);
//		} catch (Exception e) {
//
//			wb = new HSSFWorkbook();
//			try {
//				if (wb.getSheet(sheetName) == null) {
//					wb.createSheet(sheetName);
//				}
//			} catch (Exception ex) {
//				wb.createSheet(sheetName);
//			}
//			return wb;
//		}
//		try {
//			POIFSFileSystem fs = new POIFSFileSystem(fis);
//			wb = new HSSFWorkbook(fs);
//			fis.close();
//			fis = null;
//			fs = null;
//
//			HSSFSheet s = wb.getSheet(sheetName);
//			if (s == null) {
//				wb.createSheet(sheetName);
//			}
//		} catch (Exception e) {
//			wb.createSheet(sheetName);
//		} finally {
//			if (fis != null)
//				fis.close();
//			fis = null;
//
//			return wb;
//		}
//
//	}
//
//	/**
//	 * returns the current main sheet of the excel file
//	 * 
//	 * @return HSSFSheet represents the current main sheet
//	 * @throws Exception
//	 */
//	public HSSFSheet getSheet() throws Exception {
//		HSSFWorkbook workbook = getWorkbook(sheets[0]);
//		try {
//			HSSFSheet s = workbook.getSheet(sheets[0]);
//			if (s == null) {
//				workbook.createSheet(sheets[0]);
//			}
//		} catch (Exception e) {
//			workbook.createSheet(sheets[0]);
//		}
//		writeToFile(workbook);
//		return workbook.getSheet(sheets[0]);
//	}
//
//	/**
//	 * Returns weather sheet exists or not.<br>
//	 * NOTE: this function will create the sheet if does not exist! Instead, use
//	 * <i>sheetExists()</i> which does not created the missing sheet.
//	 * 
//	 * @param sheet
//	 * @deprecated will be removed in future releases!
//	 */
//	public boolean isSheetExists(String sheet) throws Exception {
//		HSSFWorkbook workbook = getWorkbook(sheet);
//		try {
//			HSSFSheet s = workbook.getSheet(sheet);
//			if (s == null) {
//				return false;
//			}
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * Returns weather sheet exists or not.
//	 * 
//	 * @param sheet
//	 *            sheet name
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean sheetExists(String sheet) throws Exception {
//		HSSFWorkbook workbook = getWorkbook();
//		try {
//			HSSFSheet s = workbook.getSheet(sheet);
//			if (s == null) {
//				return false;
//			}
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * returns the requested sheet of the excel file
//	 * 
//	 * @param sheetName
//	 *            sheet name to return
//	 * @return HSSFSheet represents the current requested sheet
//	 * @throws Exception
//	 */
//	public HSSFSheet getSheet(String sheetName) throws Exception {
//		HSSFWorkbook workbook = getWorkbook(sheetName);
//		try {
//			HSSFSheet s = workbook.getSheet(sheetName);
//			if (s == null) {
//				workbook.createSheet(sheetName);
//			}
//		} catch (Exception e) {
//			workbook.createSheet(sheetName);
//		}
//		writeToFile(workbook);
//		return workbook.getSheet(sheetName);
//	}
//
//	private void writeToFile(HSSFWorkbook workbook) throws Exception {
//		FileOutputStream fileOut = new FileOutputStream(xlsFile, false);
//		workbook.write(fileOut);
//		fileOut.close();
//	}
//
//	/**
//	 * returns the next row index of the main sheet
//	 * 
//	 * @return last row index as int
//	 */
//	public int getNextRowIndex() {
//		return getNextRowIndex(sheets[0]);
//	}
//
//	/**
//	 * sets the next row index of the main sheet
//	 * 
//	 * @param nextRowIndex
//	 *            new position for the row-index
//	 */
//	public void setNextRowIndex(short nextRowIndex) {
//		setNextRowIndex(sheets[0], nextRowIndex);
//	}
//
//	/**
//	 * returns the next row index of the requested sheet
//	 * 
//	 * @param sheet
//	 *            sheet name to return its row index
//	 * @return last row index as int
//	 */
//	public int getNextRowIndex(String sheet) {
//		int index;
//		try {
//			index = getSheetNameIndex(sheet);
//		} catch (Exception e) {
//			index = 0;
//		}
//		return nextRowIndex[index];
//	}
//
//	/**
//	 * sets the next row index of the requested sheet
//	 * 
//	 * @param sheet
//	 *            sheet name to set its row index
//	 * @param nextRowIndex
//	 *            new position for the row-index
//	 */
//	public void setNextRowIndex(String sheet, short nextRowIndex) {
//		int index;
//		try {
//			index = getSheetNameIndex(sheet);
//		} catch (Exception e) {
//			index = 0;
//		}
//		this.nextRowIndex[index] = nextRowIndex;
//	}
//
//	/**
//	 * returns a cell's content from the current sheets row as String the
//	 * returned value is String value of the cell content.
//	 * 
//	 * @param rowIndex
//	 *            index of the row within the sheet - start from "0"
//	 * @param cellIndex
//	 *            index of the cell within the sheet - start from "0"
//	 * @return
//	 * @throws Exception
//	 */
//	public String getCellContent(int rowIndex, int cellIndex) throws Exception {
//		return getCellContent(sheets[0], rowIndex, cellIndex);
//	}
//
//	/**
//	 * returns a cell's content from the given sheet and row as String the
//	 * returned value is String value of the cell content.
//	 * 
//	 * @param sheetName
//	 *            to return the cell content from
//	 * @param rowIndex
//	 *            index of the row within the sheet - start from "0"
//	 * @param cellIndex
//	 *            index of the cell within the sheet - start from "0"
//	 * @return
//	 * @throws Exception
//	 */
//	public String getCellContent(String sheetName, int rowIndex, int cellIndex)
//			throws Exception {
//		String[] content = getRowContent(sheetName, rowIndex);
//		return content[cellIndex];
//	}
//
//	/**
//	 * returns the whole row's content from the current sheet as Strings the
//	 * returned value is an array of the cells content of the row.
//	 * 
//	 * @param rowIndex
//	 *            index of the row within the sheet - start from "0"
//	 * @return String[] contains the Row content as String
//	 * @throws Exception
//	 */
//	public String[] getRowContent(int rowIndex) throws Exception {
//		return getRowContent(sheets[0], rowIndex);
//	}
//
//	/**
//	 * returns the whole row's content from the given sheet as Strings the
//	 * returned value is an array of the cells content of the row.
//	 * 
//	 * @param sheetName
//	 *            to return the row content from
//	 * @param rowIndex
//	 *            index of the row within the sheet - start from "0"
//	 * @return String[] contains the Row content as String
//	 * @throws Exception
//	 */
//	public String[] getRowContent(String sheetName, int rowIndex)
//			throws Exception {
//		String[][] content = getSheetContent(sheetName);
//		return content[rowIndex];
//	}
//
//	/**
//	 * returns the whole current-sheet's content as Strings the returned value
//	 * is an array of the rows content - which are also a String array of the
//	 * cells content of each row.
//	 * 
//	 * @return String[][] contains the current-Sheet content as String
//	 * @throws Exception
//	 */
//	public String[][] getSheetContent() throws Exception {
//		return getSheetContent(sheets[0]);
//	}
//
//	/**
//	 * returns the whole sheet's content as Strings the returned value is an
//	 * array of the rows content - which are also a String array of the cells
//	 * content of each row.
//	 * 
//	 * @param sheetName
//	 *            to return the content from
//	 * @return String[][] contains the Sheet content as String
//	 * @throws Exception
//	 */
//	public String[][] getSheetContent(String sheetName) throws Exception {
//		HSSFSheet sheet = getSheet(sheetName);
//		if (sheet == null) {
//			return null;
//		}
//		String[][] content = new String[sheet.getLastRowNum() + 1][];
//		for (short i = 0; i < content.length; i++) {
//			HSSFRow row = sheet.getRow(i);
//			if (row != null && row.getLastCellNum() >= 0) {
//				content[i] = new String[row.getLastCellNum() + 1];
//				for (short j = 0; j <= row.getLastCellNum(); j++) {
//					HSSFCell cell = row.getCell(j);
//					if (cell != null
//							&& cell.getCellType() == HSSFCell.CELL_TYPE_STRING
//							&& cell.getRichStringCellValue().getString() != null) {
//						content[i][j] = cell.getRichStringCellValue().getString();
//					} else if (cell != null
//							&& cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//						content[i][j] = "" + cell.getNumericCellValue();
//					} else if (cell != null
//							&& cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
//						content[i][j] = "" + cell.getBooleanCellValue();
//					} else {
//						content[i][j] = "";
//					}
//				}
//			} else {
//				content[i] = new String[] {};
//			}
//		}
//		return content;
//	}
//
//	/**
//	 * gets the number of the rows in the main sheet
//	 * 
//	 * @return int - number of rows
//	 * @throws Exception
//	 */
//	public int getNumOfRows() throws Exception {
//		return getNumOfRows(sheets[0]);
//	}
//
//	/**
//	 * gets the number of the rows in the desired sheet
//	 * 
//	 * @param sheetName
//	 *            sheet name
//	 * @return int - number of rows
//	 * @throws Exception
//	 */
//	public int getNumOfRows(String sheetName) throws Exception {
//		HSSFSheet sheet = getSheet(sheetName);
//		if (sheet == null) {
//			return 0;
//		}
//		return sheet.getLastRowNum() + 1;
//	}
//
//	/**
//	 * gets the number of the cells in the desired row in the sheet
//	 * 
//	 * @param rowIndex
//	 *            row index starts from "0" in the sheet
//	 * @return int - number of cells
//	 * @throws Exception
//	 */
//	public int getNumOfCells(int rowIndex) throws Exception {
//		return getNumOfCells(sheets[0], rowIndex);
//	}
//
//	/**
//	 * gets the number of the cells in the desired row in the desired sheet
//	 * 
//	 * @param sheetName
//	 *            sheet name
//	 * @param rowIndex
//	 *            row index starts from "0" in the sheet
//	 * @return int - number of cells
//	 * @throws Exception
//	 */
//	public int getNumOfCells(String sheetName, int rowIndex) throws Exception {
//		HSSFSheet sheet = getSheet(sheetName);
//		if (sheet == null || (sheet.getLastRowNum() < rowIndex || rowIndex < 0)) {
//			return 0;
//		}
//		return sheet.getRow(rowIndex).getLastCellNum() + 1;
//	}
//
//	/**
//	 * for a cell in the given sheet : returns if the cells content is a boolean
//	 * content note that the cell-type must be explicitly modified to "boolean"
//	 * 
//	 * @param sheetName
//	 *            sheet name to look in
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a boolean
//	 *         content
//	 */
//	public boolean isBooleanCell(String sheetName, int rowNum, int cellNum) {
//		try {
//			HSSFCell cell = getCell(sheetName, rowNum, cellNum);
//			cell.getBooleanCellValue();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	/**
//	 * for a cell in the given sheet : returns if the cells content is a numeric
//	 * content note that the cell-type must be explicitly modified to "numeric"
//	 * 
//	 * @param sheetName
//	 *            sheet name to look in
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a numeric
//	 *         content
//	 */
//	public boolean isNumericCell(String sheetName, int rowNum, int cellNum) {
//		try {
//			HSSFCell cell = getCell(sheetName, rowNum, cellNum);
//			try {
//				cell.getBooleanCellValue();
//			} catch (Exception e) {
//				cell.getNumericCellValue();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	/**
//	 * for a cell in the given sheet : returns if the cells content is a String
//	 * content
//	 * 
//	 * @param sheetName
//	 *            sheet name to look in
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a String
//	 *         content
//	 */
//	public boolean isStringCell(String sheetName, int rowNum, int cellNum) {
//		try {
//			HSSFCell cell = getCell(sheetName, rowNum, cellNum);
//			cell.getRichStringCellValue().getString();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	/**
//	 * for a cell in the main sheet : returns if the cells content is a boolean
//	 * content note that the cell-type must be explicitly modified to "boolean"
//	 * 
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a boolean
//	 *         content
//	 */
//	public boolean isBooleanCell(int rowNum, int cellNum) {
//		return isBooleanCell(sheets[0], rowNum, cellNum);
//	}
//
//	/**
//	 * for a cell in the main sheet : returns if the cells content is a numeric
//	 * content. note that the cell-type must be explicitly modified to "numeric"
//	 * 
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a numeric
//	 *         content
//	 */
//	public boolean isNumericCell(int rowNum, int cellNum) {
//		return isNumericCell(sheets[0], rowNum, cellNum);
//	}
//
//	/**
//	 * for a cell in the main sheet : returns if the cells content is a String
//	 * content
//	 * 
//	 * @param rowNum
//	 *            row index start from 0
//	 * @param cellNum
//	 *            cell index start from 0
//	 * @return true if the cell is a valid cell and if its content is a String
//	 *         content
//	 */
//	public boolean isStringCell(int rowNum, int cellNum) {
//		return isStringCell(sheets[0], rowNum, cellNum);
//	}
//
//	/**
//	 * returns a cell if exist or null
//	 * 
//	 * @param sheetName
//	 *            name of the sheet to look in
//	 * @param rowNum
//	 *            row index starts from 0
//	 * @param cellNum
//	 *            cell index starts from 0
//	 * @return HSSFCell or null
//	 * @throws Exception
//	 */
//	protected HSSFCell getCell(String sheetName, int rowNum, int cellNum)
//			throws Exception {
//		HSSFSheet sheet = getSheet(sheetName);
//		if (sheet == null || rowNum > sheet.getLastRowNum()
//				|| sheet.getRow(rowNum) == null
//				|| cellNum > sheet.getRow(rowNum).getLastCellNum()) {
//			return null;
//		}
//		return sheet.getRow(rowNum).getCell((short) cellNum);
//	}
//
//	private int getSheetNameIndex(String sheetName) throws Exception {
//		for (int i = 0; i < sheets.length; i++) {
//			if (sheets[i].equals(sheetName))
//				return i;
//		}
//		throw new Exception("The requested sheet \"" + sheetName
//				+ "\" does not exist. ");
//	}
//
//	private int addAndGetNewSheetNameIndex(String sheetName) {
//		int newSize = sheets.length + 1;
//		String[] oldNames = sheets;
//		int[] oldIndexes = nextRowIndex;
//		sheets = new String[newSize];
//		nextRowIndex = new int[newSize];
//		for (int i = 0; i < newSize - 1; i++) {
//			sheets[i] = oldNames[i];
//			nextRowIndex[i] = oldIndexes[i];
//		}
//		sheets[newSize - 1] = sheetName;
//		nextRowIndex[newSize - 1] = 0;
//		return newSize - 1;
//	}
//
//	private String testName() {
//		String currentTestFolder = SystemTestCase.report.getCurrentTestFolder();
//		String root = JSystemProperties.getInstance().getPreference(
//				FrameworkOptions.LOG_FOLDER)
//				+ "\\current\\";
//		return currentTestFolder.substring(currentTestFolder.indexOf(root)
//				+ root.length());
//	}
//
//	private ExcelFile(String fileName, String mainSheetName, boolean append,
//			boolean inLog) throws Exception {
//		sheets[0] = mainSheetName;
//		if (!fileName.toLowerCase().endsWith(".xls")) {
//			fileName = fileName + ".xls";
//		}
//		File fileFile = new File(testName() + "\\" + fileName);
//		if (fileFile.length() > 0) {
//			FileUtils.copyFile(testName() + "\\" + fileName, fileName
//					+ ".backup");
//		}
//
//		this.fileName = fileName;
//		this.append = append;
//		init(inLog);
//	}
//
//	private void init(boolean inLog) throws Exception {
//		if (inLog) {
//			xlsFile = new File(new File(SystemTestCase.report
//					.getCurrentTestFolder()).getAbsoluteFile(), fileName);
//		} else {
//			xlsFile = new File(fileName);
//		}
//
//		if (append && xlsFile.exists()) {
//			initLastRowIndex();
//		} else {
//			xlsFile.mkdirs();
//			if (xlsFile.exists()) {
//				xlsFile.delete();
//			}
//		}
//	}
//
//	private void init() throws Exception {
//
//		xlsFile = new File(fileName);
//
//		initLastRowIndex();
//
//	}
//
//	private void initLastRowIndex() throws Exception {
//		HSSFWorkbook wb = getWorkbook(sheets[0]);
//		HSSFSheet sheet = wb.getSheet(sheets[0]);
//		// nextRowIndex = sheet.getRow(0).getLastCellNum() + 1;
//		while (true) {
//			HSSFRow row = sheet.getRow(nextRowIndex[0]);
//			if (row == null) {
//				return;
//			}
//			HSSFCell cell = row.getCell((short) 0);
//			if (cell == null) {
//				return;
//			}
//			nextRowIndex[0]++;
//		}
//	}
//
//	public void show() throws Exception {
//		show(SystemTestCase.report, this.fileName);
//	}
//
//	public void show(Reporter reporter) throws Exception {
//		show(reporter, this.fileName);
//	}
//
//	public void show(Reporter reporter, String title) throws Exception {
//		if (!(this.fileName.toLowerCase().endsWith(".xls"))) {
//			this.fileName += ".xls";
//		}
//		reporter.reportHtml(title, "<iframe src=\"" + this.fileName
//				+ "\" width=\"100%\" height=\"500\"></iframe>", true);
//	}
//
//	public String[] getRowAsStringArray(String[] key) throws Exception {
//		return getRowAsStringArray(sheets[0], key);
//	}
//
//	public String[] getRowAsStringArray(String sheet, String[] key)
//			throws Exception {
//		String[] objRow = null;
//		HSSFRow row = getRow(sheet, key);
//		if (row != null) {
//			objRow = new String[row.getLastCellNum()];
//			for (short c = 0; c < row.getLastCellNum(); c++) {
//				if (row.getCell(c).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//					objRow[c] = Double.toString(row.getCell(c)
//							.getNumericCellValue());
//				} else {
//					objRow[c] = row.getCell(c).getRichStringCellValue()
//							.toString();
//				}
//			}
//		}
//		return objRow;
//	}
//
//	public HSSFRow getRow(String[] key) throws Exception {
//		HSSFRow row = null;
//		HSSFSheet sheet = getSheet();
//		int r = nextRowIndex[0];
//		for (; r <= sheet.getLastRowNum(); r++) {
//			row = sheet.getRow(r);
//			if (row == null) {
//				continue;
//			}
//			short k;
//			for (k = 0; k < key.length; k++) {
//				HSSFCell cell = row.getCell(k);
//				if (!cell.getRichStringCellValue().toString().equalsIgnoreCase(
//						key[k])) {
//					break;
//				}
//			}
//			if (k == key.length) {
//				nextRowIndex[0] = (r + 1);
//				return row;
//			}
//		}
//		nextRowIndex[0] = (r + 1);
//		return null;
//	}
//
//	public HSSFRow getRow(String sheetName, String[] key) throws Exception {
//		int index = getSheetNameIndex(sheetName);
//		HSSFRow row = null;
//		HSSFSheet sheet = getSheet();
//		int r = nextRowIndex[index];
//		for (; r <= sheet.getLastRowNum(); r++) {
//			row = sheet.getRow(r);
//			if (row == null) {
//				continue;
//			}
//			short k;
//			for (k = 0; k < key.length; k++) {
//				HSSFCell cell = row.getCell(k);
//				if (!cell.getRichStringCellValue().toString().equalsIgnoreCase(
//						key[k])) {
//					break;
//				}
//			}
//			if (k == key.length) {
//				nextRowIndex[index] = (r + 1);
//				return row;
//			}
//		}
//		nextRowIndex[index] = (r + 1);
//		return null;
//	}
//
//	public void setCellFormula(HSSFCell cell, String targetSheetName,
//			String targetCell, String targetSheetLabel) {
//		String linkFormula = "HYPERLINK(\"#" + targetSheetName + "!"
//				+ targetCell + "\", \"" + targetSheetLabel + "\")";
//		cell.setCellFormula(linkFormula);
//	}
//
//	/**
//	 * public constructor that allow to append sheets to existing excel file
//	 * 
//	 * @param fileName
//	 * @param append
//	 * @throws Exception
//	 */
//	public ExcelFile(String fileName, boolean append) throws Exception {
//		if (!fileName.toLowerCase().endsWith(".xls")) {
//			fileName = fileName + ".xls";
//		}
//
//		FileInputStream fis = null;
//
//		try {
//			xlsFile = new File(fileName);
//
//			fis = new FileInputStream(fileName);
//
//			POIFSFileSystem fs = new POIFSFileSystem(fis);
//			HSSFWorkbook wb = new HSSFWorkbook(fs);
//
//			sheets[0] = wb.getSheetName(0);
//			fis.close();
//			fis = null;
//			fs = null;
//		}
//
//		catch (Exception e) {
//
//			if (fis != null)
//				fis.close();
//
//			this.fileName = fileName;
//			this.append = append;
//			init();
//			throw e;
//		}
//
//	}
//
//	/**
//	 * public constructor that allow to extends this class and also create excel
//	 * file in every directory
//	 * 
//	 * @param fileName
//	 * @param mainSheetName
//	 * @throws Exception
//	 */
//	public ExcelFile(String fileName, String mainSheetName) throws Exception {
//
//		sheets[0] = mainSheetName;
//		if (!fileName.toLowerCase().endsWith(".xls")) {
//			fileName = fileName + ".xls";
//		}
//
//		this.fileName = fileName;
//		append = false;
//		xlsFile = new File(fileName);
//		xlsFile.mkdirs();
//		if (xlsFile.exists()) {
//			xlsFile.delete();
//		}
//	}
//
//	public static enum UIElement {
//		FillBackgroundColor, FillForegroundColor, TextColor
//	}
//
//	/**
//	 * allow to change column width
//	 * 
//	 * @param sheetIndex
//	 * @param column
//	 * @param width
//	 * @throws Exception
//	 */
//	public void setColumnWidth(int sheetIndex, int column, int width)
//			throws Exception {
//		HSSFWorkbook workbook = getWorkbook(sheets[sheetIndex]);
//		HSSFSheet sheet = workbook.getSheet(sheets[sheetIndex]);
//		sheet.setColumnWidth((short) column, (short) ((width + 1) * 256));
//		writeToFile(workbook);
//	}
//
//	/**
//	 * allow to change Background,Foreground and text color
//	 * 
//	 * @param element
//	 * @param color
//	 * @param sheetIndex
//	 * @param rowInd
//	 * @throws Exception
//	 */
//	public void setRowUIColor(UIElement element, EnumColor color,
//			int sheetIndex, int rowInd) throws Exception {
//
//		HSSFWorkbook workbook = getWorkbook(sheets[sheetIndex]);
//		HSSFSheet sheet = workbook.getSheet(sheets[sheetIndex]);
//		HSSFRow xrow = null;
//		xrow = sheet.getRow(nextRowIndex[sheetIndex]);
//		if (xrow == null) {
//			xrow = sheet.createRow(nextRowIndex[sheetIndex]);
//		}
//
//		HSSFRow row = sheet.getRow(nextRowIndex[sheetIndex] - 1);
//		for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
//			HSSFCell cell = row.getCell((short) i);
//			HSSFCellStyle cellStyle = cell.getCellStyle();
//
//			if (element == UIElement.TextColor) {
//				HSSFFont font = workbook.createFont();
//
//				font.setColor(getExcelColor(color));
//
//				cellStyle.setFont(font);
//			} else if (element == UIElement.FillBackgroundColor) {
//
//				cellStyle.setFillBackgroundColor(getExcelColor(color));
//			} else if (element == UIElement.FillForegroundColor) {
//
//				cellStyle.setFillForegroundColor(getExcelColor(color));
//			}
//			cell.setCellStyle(cellStyle);
//
//			writeToFile(workbook);
//		}
//	}
//
//	private short getExcelColor(EnumColor color) {
//		switch (color) {
//		case AQUA:
//			return HSSFColor.AQUA.index;
//
//		case BLACK:
//			return HSSFColor.BLACK.index;
//		case BLUE:
//			return HSSFColor.BLUE.index;
//		case BLUE_GREY:
//			return HSSFColor.BLUE_GREY.index;
//		case BRIGHT_GREEN:
//			return HSSFColor.BRIGHT_GREEN.index;
//		case BROWN:
//			return HSSFColor.BROWN.index;
//		case CORAL:
//			return HSSFColor.CORAL.index;
//		case CORNFLOWER_BLUE:
//			return HSSFColor.CORNFLOWER_BLUE.index;
//		case DARK_BLUE:
//			return HSSFColor.DARK_BLUE.index;
//		case DARK_GREEN:
//			return HSSFColor.DARK_GREEN.index;
//		case DARK_TEAL:
//			return HSSFColor.DARK_TEAL.index;
//		case DARK_YELLOW:
//			return HSSFColor.DARK_TEAL.index;
//		case GOLD:
//			return HSSFColor.GOLD.index;
//		case GREY_40_PERCENT:
//			return HSSFColor.GREY_40_PERCENT.index;
//		case GREY_50_PERCENT:
//			return HSSFColor.GREY_50_PERCENT.index;
//		case GREY_80_PERCENT:
//			return HSSFColor.GREY_80_PERCENT.index;
//		case INDIGO:
//			return HSSFColor.INDIGO.index;
//		case LAVENDER:
//			return HSSFColor.LAVENDER.index;
//		case LEMON_CHIFFON:
//			return HSSFColor.LEMON_CHIFFON.index;
//		case LIGHT_BLUE:
//			return HSSFColor.LIGHT_BLUE.index;
//		case LIGHT_CORNFLOWER_BLUE:
//			return HSSFColor.LIGHT_CORNFLOWER_BLUE.index;
//		case LIGHT_GREEN:
//			return HSSFColor.LIGHT_GREEN.index;
//		case LIGHT_ORANGE:
//			return HSSFColor.LIGHT_ORANGE.index;
//		case LIGHT_TURQUOISE:
//			return HSSFColor.LIGHT_TURQUOISE.index;
//		case LIGHT_YELLOW:
//			return HSSFColor.LIGHT_YELLOW.index;
//		case LIME:
//			return HSSFColor.LIME.index;
//		case MAROON:
//			return HSSFColor.MAROON.index;
//		case OLIVE_GREEN:
//			return HSSFColor.OLIVE_GREEN.index;
//		case ORANGE:
//			return HSSFColor.ORANGE.index;
//		case ORCHID:
//			return HSSFColor.ORCHID.index;
//		case PALE_BLUE:
//			return HSSFColor.PALE_BLUE.index;
//		case PINK:
//			return HSSFColor.PINK.index;
//		case PLUM:
//			return HSSFColor.PLUM.index;
//		case RED:
//			return HSSFColor.RED.index;
//		case ROSE:
//			return HSSFColor.ROSE.index;
//		case ROYAL_BLUE:
//			return HSSFColor.ROYAL_BLUE.index;
//		case SEA_GREEN:
//			return HSSFColor.SEA_GREEN.index;
//		case SKY_BLUE:
//			return HSSFColor.SKY_BLUE.index;
//		case TAN:
//			return HSSFColor.TAN.index;
//		case TEAL:
//			return HSSFColor.TEAL.index;
//		case TURQUOISE:
//			return HSSFColor.AQUA.index;
//		case VIOLET:
//			return HSSFColor.VIOLET.index;
//		case WHITE:
//			return HSSFColor.WHITE.index;
//		case YELLOW:
//			return HSSFColor.YELLOW.index;
//
//		default:
//			return 0;
//
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//		ExcelFile excel = new ExcelFile(
//				"C:\\Program Files\\Apache Software Foundation\\Tomcat 5.5\\webapps\\reports\\d.xls",
//				true);
//		String sheetName = "sheet" + System.currentTimeMillis();
//		excel.addSheet(sheetName);
//		excel.addHeader(new String[] { "String", "ddd" });
//	}
}
