/*
 * Created on 28/12/2004
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html.summary;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.HtmlTestReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.JSystemInnerTests;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.SortedProperties;
import jsystem.utils.StringUtils;

/**
 * @author dvguser
 * 
 */
public class HtmlSummaryReporter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4606545218717848144L;

	private static Logger log = Logger.getLogger(HtmlSummaryReporter.class.getName());

	private File summaryFile;

	private File currentDirectory;

	private Tag html;

	private Tag main;

	private Table summary;

	private Table properties;

	private Table testsStatistics;

	private HashMap<String, Chapter> chapters;

	private HashMap<String, TestStatistics> testsStatisticsHash;

	private long totalTime;

	private int numberOfTests = 0;

	private int numberOfFails = 0;

	private int numberOfPasses = 0;

	int testCounter = 0;

	boolean summaryDisable = false;

	public HtmlSummaryReporter(File currentDir) {
		chapters = new HashMap<String, Chapter>();
		testsStatisticsHash = new HashMap<String, TestStatistics>();
		String sd = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_SUMMARY_DISABLE);
		if (sd != null && sd.equalsIgnoreCase("true")) {
			summaryDisable = true;
		}
		setCurrentDirectory(currentDir);
		summaryFile = new File(currentDir, "summary.html");
		html = new Tag("html");
		Tag head = new Tag("head");
		head.add(new Tag("link", "rel=stylesheet type=text/css href=./default.css"));
		Tag title = new Tag("title");
		title.add("JSystem summary report");
		head.add(title);
		html.add(head);

		Tag body = new Tag("body");
		body.add(new Tag("H1", null, "JSystem summary report"));
		
		String link = null;
		if (link != null) {
			if (HtmlTestReporter.isImage(link)) {
				try {
					File setupFile = new File(currentDir, "setup.html");
					FileWriter writer = new FileWriter(setupFile);
					writer.write("<html><body><img src=\"" + link + "\"></body></html>");
					writer.close();
					body.add(new Tag("a", "href=\"setup.html\"", "sut: "
							+ SutFactory.getInstance().getSutInstance().getSetupName()));
				} catch (Exception e) {
				}
			} else {
				body.add(new Tag("a", "href=\"" + link + "\"", "sut: "
						+ SutFactory.getInstance().getSutInstance().getSetupName()));
			}
			body.add(new Tag("p", false));
		}
		html.add(body);
		main = new Tag("div", "align=left");
		main.add(new Tag("p", false));
		body.add(main);
		summary = new Table(new Object[0][]);
		main.add(summary);

		main.add(new Tag("p", false));
		properties = new Table(new Object[0][]);
		getPropertiesTable();
		main.add(properties);

		testsStatistics = new Table(new Object[0][]);
		main.add(new Tag("p", false));
		main.add(testsStatistics);
		try {
			saveFile();
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to write file: " + summaryFile.getPath(), e);
		}

	}

	public void endTest(String testName, String packageName, String file, int isPass, long runTime) {
		testCounter++;
		totalTime += runTime;
		boolean updateStatistics = !JSystemInnerTests.isInnerTestByPackageSeperator(packageName, testName);
		if (updateStatistics){
			updateSummaryTable(isPass);
		}
		getPropertiesTable();
		if (!summaryDisable) {
			if (updateStatistics){
				updateStatisticsTable(packageName, testName, isPass);
			}
			
			Chapter c = (Chapter) chapters.get(packageName);
			if (c == null) {
				Table t = new Table(new String[][] { { "Test&nbsp;name", "status" } });
				c = new Chapter(packageName, null, t);
				chapters.put(packageName, c);
				main.add(c);
			}

			Table table = c.getTable();
			Tag row = new Tag("TR");
			Tag testCell = new Tag("TD");
			Tag statusCell = new Tag("TD", "ALIGN=center");
			Tag link = new Tag("a", "href=" + file, testCounter + " " + testName);
			testCell.add(link);
			row.add(testCell);
			/*
			 * the status column in the "manualTests" from the log file
			 * 00ff00 - green, ff0000 - red, Report.warningColor - orenge
			 */
			if (isPass == Reporter.PASS) {
				statusCell.add("<span class=\"" + "test_summary_pass" + "\">pass</span>");
			} else if (isPass == Reporter.FAIL) {
				statusCell.add("<span class=\"" + "test_summary_erro" + "\">fail</span>");
			} else {
				statusCell.add("<span class=\"" + "test_summary_warn" + "\">warning</span>");
			}
			row.add(statusCell);
			table.add(row);

		}
		if ("false".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SAVE_REPORTERS_ON_RUN_END))){
			try {
				saveFile();
			} catch (Exception e) {
				log.log(Level.WARNING, "fail to save summary file", e);
			}
		}

	}

	/**
	 * @return Returns the currentDirectory.
	 */
	public File getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * @param currentDirectory
	 *            The currentDirectory to set.
	 */
	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	/**
	 * @return Returns the summaryFile.
	 */
	public File getSummaryFile() {
		return summaryFile;
	}

	/**
	 * @param summaryFile
	 *            The summaryFile to set.
	 */
	public void setSummaryFile(File summaryFile) {
		this.summaryFile = summaryFile;
	}

	public void saveFile() throws Exception {

		FileWriter writer = new FileWriter(summaryFile);
		writer.write(html.toString());
		writer.flush();
		writer.close();
		writer = null;
	}

	/**
	 * Update Summary statistics table (Fail\Pass\Warning)
	 * 
	 * @param isPass Test status value
	 */
	private void updateSummaryTable(int isPass) {
		numberOfTests++;
		if (isPass == Reporter.FAIL) {
			numberOfFails++;
		} else if (isPass == Reporter.PASS) {
			numberOfPasses++;
		}
		summary.clear();
		Tag tableRow = new Tag("TR");
		tableRow.add(new Tag("TH", null, "General statistics"));
		tableRow.add(new Tag("TH"));
		summary.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "Number of tests"));
		tableRow.add(new Tag("TD", null, Integer.toString(numberOfTests)));
		summary.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "Number of fails"));
		tableRow.add(new Tag("TD", null, Integer.toString(numberOfFails)));
		summary.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "Number of warnings"));
		tableRow.add(new Tag("TD", null, Integer.toString(numberOfTests - (numberOfPasses + numberOfFails))));
		summary.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "Running time"));
		long seconds = totalTime / 1000;
		
		tableRow.add(new Tag("TD", null, StringUtils.formatTimeToString(seconds) + " (HH:MM:SS)"));
		summary.add(tableRow);
	}

	/**
	 * Update statistics table
	 * 
	 * @param className
	 * @param testName
	 * @param status PASS\FAIL\WARNING
	 */
	private void updateStatisticsTable(String className, String testName, int status) {
		TestStatistics stat = (TestStatistics) testsStatisticsHash.get(className + ";" + testName);
		if (stat == null) {
			stat = new TestStatistics();
			stat.setClassName(className);
			stat.setMethodName(testName);
		}
		switch (status) {
		case Reporter.FAIL:
			stat.addFailCount();
			break;
		case Reporter.PASS:
			stat.addPassCount();
			break;
		case Reporter.WARNING:
			stat.addWarningCount();
			break;
		}
		testsStatisticsHash.put(className + ";" + testName, stat);
		testsStatistics.clear();
		Tag tableRow = new Tag("TR");
		tableRow.add(new Tag("TH", null, "Tests statistics"));
		tableRow.add(new Tag("TH"));
		tableRow.add(new Tag("TH"));
		tableRow.add(new Tag("TH"));
		testsStatistics.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "test name"));
		tableRow.add(new Tag("TD", null, "passed"));
		tableRow.add(new Tag("TD", null, "failed"));
		tableRow.add(new Tag("TD", null, "warning"));
		testsStatistics.add(tableRow);

		Iterator<TestStatistics> iter = testsStatisticsHash.values().iterator();
		while (iter.hasNext()) {
			TestStatistics ts = iter.next();
			tableRow = new Tag("TR");
			tableRow.add(new Tag("TD", null, ts.getMethodName()));
			tableRow.add(new Tag("TD", null, Integer.toString(ts.getPassCount())));
			tableRow.add(new Tag("TD", null, Integer.toString(ts.getFailCount())));
			tableRow.add(new Tag("TD", null, Integer.toString(ts.getWarningCount())));
			testsStatistics.add(tableRow);
		}
	}

	private void getPropertiesTable() {
		// Tag table = new Table(new Object[0][]);
		properties.clear();
		;
		Tag tableHeader = new Tag("TR");
		tableHeader.add(new Tag("TH", null, "Run properties"));
		tableHeader.add(new Tag("TH"));
		properties.add(tableHeader);
		Properties p = Summary.getInstance().getProperties();
		SortedProperties tmpProps = new SortedProperties(p);
		Tag tableRow = null;
		Enumeration<Object> enum1 = tmpProps.keys();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			tableRow = new Tag("TR");
			tableRow.add(new Tag("TD", null, key));
			tableRow.add(new Tag("TD", null, p.getProperty(key)));
			properties.add(tableRow);
		}
	}
}

class TestStatistics implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8521955095941091741L;

	String className;

	String methodName;

	int passCount = 0;

	int failCount = 0;

	int warningCount = 0;

	public boolean equals(Object o) {
		if (!(o instanceof TestStatistics)) {
			return false;
		}
		TestStatistics ts = (TestStatistics) o;
		return (ts.getClassName().equals(className) && ts.getMethodName().equals(methodName));
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getFailCount() {
		return failCount;
	}

	public void addFailCount() {
		failCount++;
	}

	public int getWarningCount() {
		return warningCount;
	}

	public void addWarningCount() {
		warningCount++;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getPassCount() {
		return passCount;
	}

	public void addPassCount() {
		passCount++;
	}
}