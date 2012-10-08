/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * Basic .CSV file analyzer
 * 
 * @author guy.arieli
 */
public abstract class BasicCsvAnalyzer extends AnalyzerParameterImpl {

	protected String fileToAnalyze = null;

	protected String[] columnsToAnalyze = null;

	protected int[] columnsToAnalyzeIndexs;

	protected String[] header = null;

	/**
	 * The Header line String Key is assigned by default to "Seconds Elapsed"
	 */
	protected String headerLineStrKey = "Seconds Elapsed";

	/**
	 * The column index that is used as a reference, default: 0
	 */
	protected int columnReferenceIndex = 0;

	/**
	 * The column to analyze index (index the position out of the
	 * columnsToAnalyze[], default is the 2nd column(), while the first one is
	 * usually the reference column, default: 1
	 */
	protected int columnToAnalyzeIndex = 1;

	/**
	 * Mark the start of the block to analyze, by the start cell value in the
	 * reference column Default: -1 (for starting at the first line)
	 */
	protected long startCellValue = -1;

	/**
	 * Mark the end of the block to analyze, by the start cell value in the
	 * reference column Default: -1 (for ending at the last line)
	 */
	protected long endCellValue = -1;

	/**
	 * Default constructor
	 */
	public BasicCsvAnalyzer() {
		super();
	}

	/**
	 * 
	 * @param columnsToAnalyze
	 *            the column to analyze index 
	 */
	public BasicCsvAnalyzer(String[] columnsToAnalyze) {
		this.columnsToAnalyze = columnsToAnalyze;
	}

	/**
	 * The method loads the .csv file, reads the headers and retrieves the
	 * appropriate columns data from the file
	 * 
	 * @throws Exception
	 */
	protected void loadFile() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToAnalyze)));
		String line = null;
		boolean headerFound = false;
		boolean startProcess = false;
		try {
			// while the file isn't empty
			while ((line = reader.readLine()) != null) {
				String[] lineCells = line.split("\\,");
				if (!headerFound) {

					// if the specific Header Tag was found over all the line
					// cells
					if (isHeaderLine(lineCells)) {
						header = lineCells;
						headerFound = true;
					} else { // continue to the next line
						continue;
					}

					columnsToAnalyzeIndexs = new int[columnsToAnalyze.length];
					for (int i = 0; i < columnsToAnalyzeIndexs.length; i++) {
						columnsToAnalyzeIndexs[i] = -1;
					}
					// initialize ColumnsToAnalyzeIndex Vector
					for (int i = 0; i < columnsToAnalyze.length; i++) {
						for (int j = 0; j < header.length; j++) {
							if (columnsToAnalyze[i].equals(header[j])) {
								columnsToAnalyzeIndexs[i] = j;
								break;
							}
						}
					}
				} else {
					String[] toAnalyze = getColumn(lineCells);
					if (toAnalyze == null) {
						break;
					}
					// get the section to Analyze between the Start & End values
					if (!startProcess) {
						if (isStartProcess(toAnalyze)) {
							startProcess = true;
						}
					}
					if (isEndProcess(toAnalyze)) {
						break;
					}
					// Calculate the desired calculation
					if (startProcess) {
						process(toAnalyze);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
	}

	/**
	 * The method gets the values of the columns to be analyzed according to the
	 * right headers found
	 * 
	 * @param line
	 * @return the vector of data that should be analyzed
	 */
	private String[] getColumn(String[] line) {
		String[] toAnalyze = new String[columnsToAnalyzeIndexs.length];
		for (int i = 0; i < columnsToAnalyzeIndexs.length; i++) {
			if (columnsToAnalyzeIndexs[i] >= line.length) {
				return null;
			}
			toAnalyze[i] = line[columnsToAnalyzeIndexs[i]];
		}
		return toAnalyze;
	}

	public String getFileToAnalyze() {
		return fileToAnalyze;
	}

	public void setFileToAnalyze(String fileToAnalyze) {
		this.fileToAnalyze = fileToAnalyze;
	}

	public boolean isHeaderLine(String[] line) throws Exception {
		if (line.length > 0) {
			return getHeaderLineStrKey().equals(line[0]);
		}
		return false;
	}

	public abstract void process(String[] toAnalyze) throws Exception;

	/**
	 * @param line
	 *            the line to check
	 * @return true in case the retrived line line is marked as the start of the
	 *         block we want to analyze false otherwise
	 */
	public boolean isStartProcess(String[] line) {
		try {
			if (startCellValue > 0) {
				if (startCellValue > Long.parseLong(line[columnReferenceIndex])) {
					return false;
				}
			}
		} catch (Throwable t) {
		}
		return true;
	}

	/**
	 * @param line
	 *            the line to check
	 * @return true in case the retrived line line is marked as the end of the
	 *         block we want to analyze false otherwise
	 */
	public boolean isEndProcess(String[] line) {
		try {
			if (endCellValue > 0) {
				if (endCellValue < Long.parseLong(line[columnReferenceIndex])) {
					return true;
				}
			}
		} catch (Throwable t) {
		}
		return false;
	}

	public String[] getColumnsToAnalyze() {
		return columnsToAnalyze;
	}

	public void setColumnsToAnalyze(String[] columnToAnalyze) {
		this.columnsToAnalyze = columnToAnalyze;
	}

	public int getColumnReferenceIndex() {
		return columnReferenceIndex;
	}

	public void setColumnReferenceIndex(int columnReferenceIndex) {
		this.columnReferenceIndex = columnReferenceIndex;
	}

	public int getColumnToAnalyzeIndex() {

		return columnToAnalyzeIndex;
	}

	public void setColumnToAnalyzeIndex(int columnToAnalyzeIndex) {
		this.columnToAnalyzeIndex = columnToAnalyzeIndex;
	}

	public String getHeaderLineStrKey() {
		return headerLineStrKey;
	}

	public void setHeaderLineStrKey(String headerLineStrKey) {
		this.headerLineStrKey = headerLineStrKey;
	}

	public long getStartCellValue() {
		return startCellValue;
	}

	public void setStartCellValue(long startCellValue) {
		this.startCellValue = startCellValue;
	}

	public long getEndCellValue() {
		return endCellValue;
	}

	public void setEndCellValue(long endCellValue) {
		this.endCellValue = endCellValue;
	}
}
