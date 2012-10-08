/*
 * Created on Sep 14, 2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author guy.arieli
 * 
 */
public abstract class TableBasic implements TTable {
	protected String stringTable = null;

	protected String[] lines = null;

	protected int[] fieldsOffset = null;

	protected int numberOfFields = -1;

	protected String[] header1 = null;

	protected String[] header2 = null;

	protected String[][] cells = null;

	/**
	 * Create a table instance from a cli string input.
	 * 
	 * @param stringTable
	 *            the string to be analyze into table structure
	 * @throws Exception
	 */
	public TableBasic(String stringTable) throws Exception {
		this.stringTable = stringTable;
		initTable(stringTable);
	}

	public TableBasic() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getNumberOfRows()
	 */
	public int getNumberOfRows() {
		return cells.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getNumberOfColumns()
	 */
	public int getNumberOfColumns() {
		return numberOfFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getCell(int, int)
	 */
	public String getCell(int row, int col) {
		if (col >= numberOfFields || row >= getNumberOfRows()) {
			return null;
		}
		return cells[row][col];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getRow(int)
	 */
	public String[] getRow(int row) {
		if (row >= getNumberOfRows()) {
			return null;
		}
		return cells[row];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getColumn(int)
	 */
	public String[] getColumn(int col) {
		int numberOfRows = getNumberOfRows();
		String[] returnArray = new String[numberOfRows];
		for (int i = 0; i < numberOfRows; i++) {
			returnArray[i] = getCell(i, col);
		}
		return returnArray;
	}

	protected void initLines() {
		lines = stringTable.split("[\\r\\n]+");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#findFieldInRow(java.lang.String,
	 *      int)
	 */
	public int findFieldInRow(String fieldName, int lineIndex) {
		String[] rowArray = getRow(lineIndex);
		for (int i = 0; i < rowArray.length; i++) {
			if (rowArray[i].equals(fieldName)) {
				return i;
			}
		}
		return -1;
	}

	protected abstract void initHeaders() throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getHeaders()
	 */
	public String[] getHeaders() throws Exception {

		return header1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getHeaderFieldIndex(java.lang.String)
	 */
	public abstract int getHeaderFieldIndex(String fieldName) throws Exception;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < getNumberOfRows(); i++) {
			for (int j = 0; j < getNumberOfColumns(); j++) {
				sb.append(getCell(i, j));
				if (j != getNumberOfColumns() - 1) {
					sb.append(",");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getTableString()
	 */
	public String getTableString() {
		return stringTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getFirstRowIndex(java.lang.String,
	 *      java.lang.String)
	 */
	public int getFirstRowIndex(String headerField, String cellValue) throws Exception {
		int colIndex = getHeaderFieldIndex(headerField);
		String[] col = getColumn(colIndex);
		for (int i = 0; i < col.length; i++) {
			if (cellValue.equals(col[i])) {
				// if(cellValue.compareToIgnoreCase(col[i])==0){
				return i;
			}
		}
		throw new Exception("Row: " + headerField + " doesn't contain field: " + cellValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.extensions.analyzers.tabletext.TTable#getFirstRowIndex(int,
	 *      java.lang.String)
	 */
	public int getFirstRowIndex(int colIndex, String cellValue) throws Exception {
		String[] col = getColumn(colIndex);
		for (int i = 0; i < col.length; i++) {
			if (cellValue.equals(col[i])) {
				// if(cellValue.compareToIgnoreCase(col[i])==0){
				return i;
			}
		}
		throw new Exception("Row index: " + colIndex + " doesn't contain field: " + cellValue);
	}

	public boolean isRealKeyHeader(String keyHeader, Object testAgainst) {
		
		 // to find it out we put the first column in a hashset. 
		 // If all the values in our table's first column are different,
	     //  then the size of the hashset will be the same as the size of our table's first column.
	     //  If the size is equal than it is a table with keyheader, otherwise it is without keyheader. 

		boolean isRealKeyHeader;
		GetTableColumn gtc = null;
		try {
			gtc = new GetTableColumn(keyHeader);
		} catch (Exception e) {
			// throwable = e;
			isRealKeyHeader = false;
			return isRealKeyHeader;
		}
		gtc.setTestAgainst(testAgainst);
		gtc.analyze();
		String[] keyHeaders = gtc.getColumn();
		Set<String> myHashSet = new HashSet<String>();
		for (int i = 0; i < keyHeaders.length; i++) {
			myHashSet.add(keyHeaders[i]);
		}
		if (myHashSet.size() == keyHeaders.length)
			isRealKeyHeader = true;
		else
			isRealKeyHeader = false;

		return isRealKeyHeader;
	}

}
