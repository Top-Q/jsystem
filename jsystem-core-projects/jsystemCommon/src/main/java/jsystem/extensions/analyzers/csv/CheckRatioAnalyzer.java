/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.csv;

import java.text.DecimalFormat;

import jsystem.utils.StringUtils;

/**
 * The class checks what is the ratio between 2 given columns
 * 
 * @author lior.haklay
 * 
 */
public class CheckRatioAnalyzer extends BasicCsvAnalyzer {

	private double expectedRatio;

	private double actualRatio = -1;

	private double deviation = 0.0;

	private double totalColumnA = 0;

	private double totalColumnB = 0;

	private int ratioColumnToAnalyzeIndex = 2;

	/**
	 * @param expectedRatio
	 * @param deviation
	 * @param columnsToAnalyze
	 */
	public CheckRatioAnalyzer(double expectedRatio, double deviation, String[] columnsToAnalyze) {
		super(columnsToAnalyze);
		this.expectedRatio = expectedRatio;
		this.deviation = deviation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.csv.BasicCsvAnalyzer#process(java.lang.String[])
	 */
	public void process(String[] toAnalyze) throws Exception {

		totalColumnA += Double.parseDouble(toAnalyze[getColumnToAnalyzeIndex()]);
		totalColumnB += Double.parseDouble(toAnalyze[getRatioColumnToAnalyzeIndex()]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.analyzer.AnalyzerParameter#analyze()
	 */
	public void analyze() {
		fileToAnalyze = (String) testAgainst;
		String[] colsToAn = getColumnsToAnalyze();
		StringBuffer buf = new StringBuffer();
		String columnToAnalyzeNameColumnA = colsToAn[getColumnToAnalyzeIndex()];
		String columnToAnalyzeNameColumnB = colsToAn[getRatioColumnToAnalyzeIndex()];
		try {
			loadFile();
			actualRatio = (totalColumnB / totalColumnA) * 100;
			// Converting the ratio to 5 digits after the .
			DecimalFormat df = new DecimalFormat("0.000000");

			buf.append("Analyzed Column A : " + columnToAnalyzeNameColumnA);
			buf.append("\n");
			buf.append("Analyzed Column B : " + columnToAnalyzeNameColumnB);
			buf.append("\n");
			buf.append("Expected Ratio: " + expectedRatio);
			buf.append("\n");
			buf.append("Actual Ratio: " + actualRatio);
			buf.append("\n");
			buf.append("Deviation: " + deviation);

			if (actualRatio - deviation > expectedRatio || actualRatio + deviation < expectedRatio) {
				title = "\nFail: Ratio value for " + columnToAnalyzeNameColumnB + "\nAND\n"
						+ columnToAnalyzeNameColumnA + ": " + df.format(actualRatio) + "%";

				status = false;
			} else {
				title = "\nSuccess: Ratio value for " + columnToAnalyzeNameColumnB + "\nAND\n "
						+ columnToAnalyzeNameColumnA + ": " + df.format(actualRatio) + "%";

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
	 * @return the actual actual ratio without an analyzer
	 */
	public double getActualRatio() {
		return actualRatio;
	}

	// ------- Setters & getters

	public int getRatioColumnToAnalyzeIndex() {
		return ratioColumnToAnalyzeIndex;
	}

	public void setRatioColumnToAnalyzeIndex(int ratioColumnToAnalyzeIndex) {
		this.ratioColumnToAnalyzeIndex = ratioColumnToAnalyzeIndex;
	}

}
