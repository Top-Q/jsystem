/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.csv;

import jsystem.utils.StringUtils;

/**
 * The analyzer checks what is the minimum and maximum over the parsed column
 * 
 * @author lior.haklay
 */
public class CheckMinMaxAnalyzer extends BasicCsvAnalyzer {

	private double expectedMinimum;

	private double expectedMaximum;

	private double actualMin = 1000;

	private double actualMax = -1;

	private String[] colsToAn = null;

	/**
	 * @param expectedMin
	 * @param expectedMax
	 */
	public CheckMinMaxAnalyzer(double expectedMin, double expectedMax, String[] columnsToAnalyze) {
		super(columnsToAnalyze);
		this.expectedMinimum = expectedMin;
		this.expectedMaximum = expectedMax;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.avalanche.analyze.BasicCsvAnalyzer#process(java.lang.String[])
	 */
	public void process(String[] toAnalyze) throws Exception {
		double current = Double.parseDouble(toAnalyze[getColumnToAnalyzeIndex()]);

		if (actualMax < current)
			actualMax = current;
		if (actualMin > current)
			actualMin = current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.analyzer.AnalyzerParameter#analyze()
	 */
	public void analyze() {
		fileToAnalyze = (String) testAgainst;
		colsToAn = getColumnsToAnalyze();
		StringBuffer buf = new StringBuffer();
		String columnToAnalyzeName = colsToAn[getColumnToAnalyzeIndex()];
		try {
			loadFile();
			buf.append("Analyzed Column: " + columnToAnalyzeName);
			buf.append("\n");
			buf.append("Expected Min: " + expectedMinimum);
			buf.append("\n");
			buf.append("Actual Min: " + actualMin);
			buf.append("\n");
			buf.append("Expected Max: " + expectedMaximum);
			buf.append("\n");
			buf.append("Actual Max: " + actualMax);

			if ((actualMin < expectedMinimum || actualMax > expectedMaximum)) {
				title = "\nFail: Min value for " + columnToAnalyzeName + ": " + actualMin + "\nMax value for "
						+ columnToAnalyzeName + ": " + actualMax;
				status = false;
			} else {
				title = "\nSuccess: Min value for " + columnToAnalyzeName + ": " + actualMin + "\nMax value for "
						+ columnToAnalyzeName + ": " + actualMax;

				status = true;
			}
			message = buf.toString();
		} catch (Exception e) {
			title = "Fail to process File: " + fileToAnalyze + "...Please check columns to analyze names";
			status = false;
			message = StringUtils.getStackTrace(e);
		}
	}

	/**
	 * @return the actual max without an analyzer
	 */
	public double getActualMax() {
		return actualMax;
	}

	/**
	 * @return the actual min without an analyzer
	 */
	public double getActualMin() {
		return actualMin;
	}

}
