/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Table is used to analyze tables that are recieved from cli. The table string
 * should be in the following format:
 * 
 * Header1 Header2 ------- ------- key1 value1 key2 value2
 * 
 * 
 * @author guy.arieli
 * 
 */
public class Table extends TableBasic {
	private int seperatorLineIndex;

	/**
	 * Create a table instance from a cli string input.
	 * 
	 * @param stringTable
	 *            the string to be analyze into table structure
	 * @throws Exception
	 */
	public Table(String stringTable) throws Exception {
		super(stringTable);
	}

	public int getNumberOfRows() {
		return lines.length - seperatorLineIndex - 1;
	}

	public String getCell(int row, int col) {
		if (col >= numberOfFields || row >= getNumberOfRows()) {
			return null;
		}
		String[] fields = getLineAsFields(row + seperatorLineIndex + 1);
		if (fields.length <= col) {
			return null;
		} else {
			return fields[col];
		}
	}

	public String[] getRow(int row) {
		return getLineAsFields(row + seperatorLineIndex + 1);
	}

	private String getSperatorLine() {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].indexOf("---") >= 0 && lines[i].matches(".*[ -]*")) {
				seperatorLineIndex = i;
				return lines[i];
			}
		}
		return null;
	}

	protected void initFieldsOffset() throws Exception {
		String sepLine = getSperatorLine();
		if (sepLine == null) {
			throw new Exception("The seperation line couldn't be found");
		}
		Pattern p = Pattern.compile("(-+)");
		Matcher m = p.matcher(sepLine);
		int startIndex = 0;
		int[] fields = new int[100];
		int i = 0;
		while (m.find(startIndex)) {
			fields[i] = m.start();
			startIndex = m.end();
			i++;
		}
		numberOfFields = i;
		if (numberOfFields <= 0) {
			throw new Exception("no fields were found in seperation line");
		}
		fieldsOffset = new int[numberOfFields];
		System.arraycopy(fields, 0, fieldsOffset, 0, numberOfFields);
	}

	public String[][] getAllLinesWithFields(String[] fields) {
		Vector<String[]> v = new Vector<String[]>();
		boolean notFound = false;
		for (int i = 0; i < getNumberOfRows(); i++) {
			for (int j = 0; j < fields.length; j++) {
				if (findFieldInRow(fields[j], i) < 0) {
					notFound = true;
					break;
				}
			}
			if (notFound) {
				notFound = false;
			} else {
				v.addElement(getRow(i));
			}
		}
		String[][] toReturn = new String[v.size()][];
		for (int i = 0; i < v.size(); i++) {
			toReturn[i] = v.elementAt(i);
		}
		return toReturn;
	}

	public int findFieldInRow(String fieldName, int lineIndex) {
		String[] rowArray = getRow(lineIndex);
		for (int i = 0; i < rowArray.length; i++) {
			if (rowArray[i].equals(fieldName)) {
				return i;
			}
		}
		return -1;
	}

	protected void initHeaders() throws Exception {
		if (seperatorLineIndex >= 2) {
			header1 = getLineAsFields(seperatorLineIndex - 2);
		}
		if (seperatorLineIndex >= 1) {
			header2 = getLineAsFields(seperatorLineIndex - 1);
		} else {
			throw new Exception("No header was found");
		}
	}

	public int getHeaderFieldIndex(String fieldName) throws Exception {
		for (int i = 0; i < numberOfFields; i++) {
			if (header1 != null && fieldName.equals(header1[i])) {
				return i;
			}
			if (header2 != null && fieldName.equals(header2[i])) {
				return i;
			}
			if (header1 != null && header2 != null
					&& fieldName.equals(header1[i] + " " + header2[i])) {
				return i;
			}
			if (header1 != null && header2 != null
					&& fieldName.equals(header1[i] + "/" + header2[i])) {
				return i;
			}
			if (header1 != null
					&& header2 != null
					&& header1[i] != null
					&& fieldName.equals(header1[i].replace('/', ' ')
							+ header2[i])) {
				return i;
			}
		}
		throw new Exception("Header field: " + fieldName + " wasn't found\r\n");
	}

	public void initTable(String tableString) throws Exception {
		this.stringTable = tableString;
		initLines();
		initFieldsOffset();
		initHeaders();
	}

	protected String[] getLineAsFields(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lines.length) {
			return null;
		}
		String[] resultArray = new String[numberOfFields];
		for (int i = 0; i < numberOfFields; i++) {
			int from = fieldsOffset[i];
			int to = -1;
			if (i == numberOfFields - 1) {
				to = lines[lineIndex].length();
			} else {
				to = fieldsOffset[i + 1];
			}

			if (to > lines[lineIndex].length()) {
				to = lines[lineIndex].length();
			}
			if (from >= lines[lineIndex].length()) {
				resultArray[i] = "";
			} else {
				resultArray[i] = lines[lineIndex].substring(from, to).trim();
			}
		}
		return resultArray;
	}
	// /* (non-Javadoc)
	// * @see
	// jsystem.extensions.analyzers.tabletext.TTable#getAllLinesWithFields(java.lang.String[])
	// */
	// public String[][] getAllLinesWithFields(String[] fields){
	// Vector v = new Vector();
	// boolean notFound = false;
	// for (int i = 0; i < getNumberOfRows(); i++) {
	// for (int j = 0; j < fields.length; j++) {
	// if (findFieldInRow(fields[j], i) < 0) {
	// notFound = true;
	// break;
	// }
	// }
	// if (notFound) {
	// notFound = false;
	// } else {
	// v.addElement(getRow(i));
	// }
	// }
	// String[][] toReturn = new String[v.size()][];
	// for (int i = 0; i < v.size(); i++) {
	// toReturn[i] = (String[])v.elementAt(i);
	// }
	// return toReturn;
	// }

}
