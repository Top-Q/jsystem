/*
 * Created on 20/04/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.sumextended;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.summary.Attribute;
import jsystem.extensions.report.html.summary.Chapter;
import jsystem.extensions.report.html.summary.HtmlSummaryReporter;
import jsystem.extensions.report.html.summary.Table;
import jsystem.extensions.report.html.summary.Tag;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.TestReporter;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.DateUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;

public class HtmlSummaryReporterExtentsion implements TestReporter, TestListener {
	private static Logger log = Logger.getLogger(HtmlSummaryReporter.class.getName());

	private File summaryFile;

	private Tag html;

	private Tag main;

	private Table summary;

	private HashMap<String, Chapter> chapters;

	private long totalTime;

	private int numberOfTests = 0;

	private int numberOfFails = 0;

	private DataReader hpr;

	long startTestTime = 0;

	boolean status = true;

	public HtmlSummaryReporterExtentsion() {
		this(new File(System.getProperty("user.dir")));
		File f = new File("summaryExtendded.html");
		setSummaryFile(f);
	}

	public HtmlSummaryReporterExtentsion(File summaryFile) {
		chapters = new HashMap<String, Chapter>();
		// setCurrentDirectory(currentDir);
		// summaryFile = new File(currentDir,"summary.html");
		setSummaryFile(summaryFile);
		html = new Tag("html");
		Tag head = new Tag("head");
		head.add(new Tag("link", "rel=stylesheet type=text/css href=./styles.css"));
		Tag title = new Tag("title");
		title.add("JSystem summary report");
		head.add(title);
		html.add(head);

		Tag body = new Tag("body");
		body.add(new Tag("H1", null, "JSystem summary report"));
		body.add(new Tag("p", false));
		body.add(new Tag("a", "href=index.html", "Detailed report"));
		body.add(new Tag("p", false));
		String link = SutFactory.getInstance().getSutInstance().getSetupLink();
		if (link != null) {
			body.add(new Tag("a", "href=\"" + link + "\"", "Setup"));
			body.add(new Tag("p", false));
		}
		html.add(body);
		main = new Tag("div", "align=left");
		main.add(new Tag("p", false));
		body.add(main);
		summary = new Table(new Object[0][]);

		main.add(summary);
		main.add(new Tag("p", false));

		main.add(getPropertiesTable());
		try {
			saveFile();
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to write file: " + summaryFile.getPath(), e);
		}

		String srcPath = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER);
		hpr = new HtmlPackageReader(srcPath);

	}

	public void init() {

	}

	public void endTest(String testName, String packageName, long runTime, String commant) {
		totalTime += runTime;
		updateSummaryTable(status);
		Chapter c = (Chapter) chapters.get(packageName);
		if (c == null) {
			String title = hpr.getTitle(packageName);
			if (title == null) {
				title = packageName;
			}
			String description = hpr.getDescription(packageName);
			Table t = new Table(new String[][] { { "Test&nbsp;name", "status", "Comment" } });
			c = new Chapter(title, description, t);
			chapters.put(packageName, c);
			main.add(c);
		}

		Table table = c.getTable();
		Tag row = new Tag("TR");
		Tag testCell = new Tag("TD");
		Tag statusCell = new Tag("TD", "ALIGN=center");
		Tag commantCell = new Tag("TD");
		// Tag link = new Tag("a", "href=" + file,testName);
		testCell.add(testName);
		row.add(testCell);
		if (status) {
			Attribute atrib = new Attribute("BGCOLOR", "");
			row.addAttribute(atrib);
			statusCell.add("pass");

		} else {
			Attribute atrib = new Attribute("BGCOLOR", "");
			row.addAttribute(atrib);
			statusCell.add("fail");
		}
		row.add(statusCell);
		if (commant == null) {
			commant = " ";
		}
		commantCell.add(commant);
		row.add(commantCell);
		table.add(row);

		try {
			saveFile();
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to save summary file", e);
		}

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

	private void saveFile() throws Exception {

		FileWriter writer = new FileWriter(summaryFile);
		writer.write(html.toString());
		writer.flush();
		writer.close();
	}

	private void updateSummaryTable(boolean isPass) {
		numberOfTests++;
		if (!isPass) {
			numberOfFails++;
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
		tableRow.add(new Tag("TD", null, "Running time"));
		tableRow.add(new Tag("TD", null, Long.toString((totalTime / 1000)) + " sec."));
		summary.add(tableRow);

	}

	private Tag getPropertiesTable() {
		Tag table = new Table(new Object[0][]);
		;
		Tag tableHeader = new Tag("TR");
		tableHeader.add(new Tag("TH", null, "Run properties"));
		tableHeader.add(new Tag("TH"));
		table.add(tableHeader);
		Tag tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "Data"));
		tableRow.add(new Tag("TD", null, DateUtils.getDate()));
		table.add(tableRow);
		tableRow = new Tag("TR");
		tableRow.add(new Tag("TD", null, "User"));
		tableRow.add(new Tag("TD", null, System.getProperty("user.name")));
		table.add(tableRow);
		File summaryProperties = new File("summary.properties");
		if (summaryProperties.exists()) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(summaryProperties));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Enumeration<?> enum1 = p.keys();
			while (enum1.hasMoreElements()) {
				String key = (String) enum1.nextElement();
				tableRow = new Tag("TR");
				tableRow.add(new Tag("TD", null, key));
				tableRow.add(new Tag("TD", null, p.getProperty(key)));
				table.add(tableRow);
			}
		}
		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.launcher.report.TestReporter#initReporterManager()
	 */
	public void initReporterManager() throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.launcher.report.TestReporter#asUI()
	 */
	public boolean asUI() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.launcher.report.TestReporter#report(java.lang.String,
	 *      java.lang.String, boolean, boolean)
	 */
	public void report(String title, String message, boolean isPass, boolean bold) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.launcher.report.TestReporter#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 *      java.lang.Throwable)
	 */
	public void addError(Test test, Throwable t) {
		status = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 *      junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test test, AssertionFailedError t) {
		status = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test test) {
		String testName;
		String testClass = test.getClass().getName();
		String packageName = StringUtils.getPackageName(testClass);
		testName = StringUtils.getClassName(testClass);
		String methodName = null;
		if (test instanceof TestCase) {
			methodName = ((TestCase) test).getName();
		}
		endTest(testName + "." + methodName, packageName, System.currentTimeMillis() - startTestTime, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test test) {
		startTestTime = System.currentTimeMillis();
		status = true;
	}

	public void report(String title, String message, int status, boolean bold) {
				
	}

}
