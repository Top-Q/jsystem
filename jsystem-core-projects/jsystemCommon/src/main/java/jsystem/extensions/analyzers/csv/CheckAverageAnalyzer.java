/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.csv;

import java.text.DecimalFormat;

import jsystem.utils.StringUtils;

/**
 * Check average generic analyzer
 * 
 * @author guy.arieli & ohad.crystal
 */
public class CheckAverageAnalyzer extends BasicCsvAnalyzer {

	private double expectedAverage;

	private double actualAverage = -1;

	private double deviation = 0.0;

	private double total = 0.0;

	private int numberOfLines = 0;

	private String[] colsToAn = null;

	/**
	 * @param expectedAvg
	 * @param deviation
	 */
	public CheckAverageAnalyzer(double expectedAvg, double deviation, String[] columnsToAnalyze) {
		super(columnsToAnalyze);
		this.expectedAverage = expectedAvg;
		this.deviation = deviation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.avalanche.analyze.AvalancheCsvAnalyzer#process(java.lang.String[])
	 */
	public void process(String[] toAnalyze) throws Exception {
		// Accumulate the total value
		total += Double.parseDouble(toAnalyze[getColumnToAnalyzeIndex()]);
		// count the number of lines
		numberOfLines++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.avalanche.analyze.AvalancheCsvAnalyzer#analyze()
	 */
	public void analyze() {
		title = "";
		fileToAnalyze = (String) testAgainst;
		colsToAn = getColumnsToAnalyze();
		StringBuffer buf = new StringBuffer();
		String columnToAnalyzeName = colsToAn[getColumnToAnalyzeIndex()];
		try {
			loadFile();
			DecimalFormat df = new DecimalFormat("0.00");

			actualAverage = total / numberOfLines;

			buf.append("Analyzed Column: " + columnToAnalyzeName);
			buf.append("\n");
			buf.append("Expected Average: " + expectedAverage);
			buf.append("\n");
			buf.append("Actual Average: " + actualAverage);
			buf.append("\n");
			buf.append("Deviation: " + deviation);

			if (actualAverage - deviation > expectedAverage || actualAverage + deviation < expectedAverage) {
				title = "Fail: Average value for " + columnToAnalyzeName + ": " + df.format(actualAverage);

				status = false;
			} else {
				title = "Success: Average value for " + columnToAnalyzeName + ": " + df.format(actualAverage);

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
	 * @return the actual average without analyzer
	 */
	public double getActualAverage() {
		return actualAverage;
	}

}
