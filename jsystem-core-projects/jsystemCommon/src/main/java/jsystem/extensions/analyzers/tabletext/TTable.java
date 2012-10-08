/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

public interface TTable {

	/**
	 * Init the table
	 * 
	 * @param tableString
	 *            the table string
	 * @throws Exception
	 */
	public abstract void initTable(String tableString) throws Exception;

	/**
	 * Get the number of rows in the table
	 * 
	 * @return table row count
	 */
	public abstract int getNumberOfRows();

	/**
	 * Get the number of columns in the table
	 * 
	 * @return table columns count
	 */
	public abstract int getNumberOfColumns();

	/**
	 * get a table cell
	 * 
	 * @param row
	 *            row index
	 * @param col
	 *            column index
	 * @return the call as String null if not exit
	 */
	public abstract String getCell(int row, int col);

	/**
	 * Get a table row
	 * 
	 * @param row
	 *            the table row index
	 * @return table row
	 */
	public abstract String[] getRow(int row);

	/**
	 * Get a table column
	 * 
	 * @param col
	 *            the table column index
	 * @return array of the column cells
	 */
	public abstract String[] getColumn(int col);

	// public abstract String[][] getAllLinesWithFields(String[] fields);

	public abstract int findFieldInRow(String fieldName, int lineIndex);

	public abstract String[] getHeaders() throws Exception;

	public abstract int getHeaderFieldIndex(String fieldName) throws Exception;

	public abstract int getFirstRowIndex(String headerField, String cellValue) throws Exception;

	public abstract int getFirstRowIndex(int colIndex, String cellValue) throws Exception;

	public abstract String getTableString();

	/**
	 * 
	 * @param keyHeader
	 * @param testAgainst
	 * @return if key header table is primary key or not. it checks all the
	 *         fields in specific column, if they are all different this column
	 *         can use as primary key else it not and it will return fasle
	 */
	public boolean isRealKeyHeader(String keyHeader, Object testAgainst);
}