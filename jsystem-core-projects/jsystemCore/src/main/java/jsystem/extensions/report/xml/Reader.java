/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.xml;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.JSystemInnerTests;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.NamedTest;
import junit.framework.SystemTest;
import junit.framework.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The reader work in different thread that write to xml report.
 * 
 * @author guy.arieli
 * 
 */
public class Reader {
	private static Logger log = Logger.getLogger(Reader.class.getName());

	File reportFile;

	Document doc;

	Element currentTest;

	Element main;

	long lastUpdatedTime = 0;

	boolean isChange = false;

	private int testIndex;

	private Properties properties;

	private Properties executionProperties;

	public Reader(File reportFile, Document doc) throws Exception {
		this.reportFile = reportFile;
		this.doc = doc;

		init();
		main = doc.createElement("reports");
		main.setAttribute("status", "true");
		main.setAttribute("startTime",
				Long.toString(System.currentTimeMillis()));

		readSummaryAttributes();
		doc.appendChild(main);
		try {
			FileUtils.saveDocumentToFile(doc, reportFile);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to write to XML file", e);
			return;
		}
		properties = new Properties();
	}

	public void init() {
		testIndex = 0;
	}

	private boolean isInnerTest = false;

	public void startTest(TestInfo testInfo) {
		isInnerTest = JSystemInnerTests.isInnerTest(testInfo.className,
				testInfo.methodName);
		if (isInnerTest) {
			currentTest = null;
			return;
		}
		readElements();
		String packageName = StringUtils.getPackageName(testInfo.className);
		currentTest = doc.createElement("test");
		currentTest.setAttribute("testIndex", "" + testIndex);
		testIndex++;
		currentTest.setAttribute(
				"name",
				testInfo.methodName == null ? StringUtils
						.getClassName(testInfo.className) : StringUtils
						.getClassName(testInfo.className)
						+ "."
						+ testInfo.methodName);
		currentTest.setAttribute("status", "true");
		currentTest.setAttribute("startTime",
				Long.toString(System.currentTimeMillis()));
		currentTest.setAttribute("package", (packageName != null) ? packageName
				: "");
		if (testInfo.parameters != null) {
			currentTest.setAttribute("params", testInfo.parameters);
		}
		currentTest.setAttribute("count", Integer.toString(testInfo.count));
		main.appendChild(currentTest);
		isChange = true;
	}

	/**
	 * 
	 */
	void removeLastTest() {
		main.removeChild(currentTest);
	}

	public void endTest(Test test, int status) {

		if (currentTest == null) {
			if (!isInnerTest) {
				log.log(Level.WARNING,
						"Unable To Perform 'endTest' :current test ==null");
			}
			return;
		}

		if (test.getClass().getCanonicalName()
				.equals("junit.framework.JSystemJUnit4ClassRunner.TestInfo")) {
			String testId = ((NamedTest) test).getFullUUID();
			RunnerTest rTest = ScenariosManager.getInstance()
					.getCurrentScenario().getRunnerTestByFullId(testId);
			if (rTest != null) {
				test = rTest.getTest();
			}
		}

		if (test != null && test instanceof SystemTest) {
			SystemTest sTest = (SystemTest) test;

			currentTest.setAttribute("steps", sTest.getExecutedSteps());
			currentTest.setAttribute("failCause", sTest.getFailCause());
			currentTest.setAttribute("properties",
					StringUtils.propertiesToString(properties));
			String doc = sTest.getTestDocumentation();
			if (doc == null) {
				doc = "";
			}
			currentTest.setAttribute("documentaion", doc);

			currentTest.setAttribute("endTime",
					Long.toString(System.currentTimeMillis()));
			String stat = "true";
			if (status == Reporter.FAIL) {
				stat = "false";
			} else if (status == Reporter.WARNING) {
				stat = "warning";
			}
			currentTest.setAttribute("status", stat);
			readSummaryAttributes();
			if ("false".equals(JSystemProperties.getInstance()
					.getPreferenceOrDefault(
							FrameworkOptions.SAVE_REPORTERS_ON_RUN_END))) {
				readElements();
			}

			properties.clear();
		}
	}

	public void readElements() {
		try {
			FileUtils.saveDocumentToFile(doc, reportFile);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to write to XML file", e);
			return;
		}
		lastUpdatedTime = System.currentTimeMillis();
	}

	public void setCurrentTestAttribute(String name, String value) {
		if (currentTest != null) {
			currentTest.setAttribute(name, value);
		}
	}

	public void readSummaryAttributes() {
		Properties p = Summary.getInstance().getProperties();
		Enumeration<Object> enum1 = p.keys();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			try {
				main.setAttribute(key, p.getProperty(key));
			} catch (Throwable t) {
				log.log(Level.WARNING, "*******   Fail to set attribute: + "
						+ key + "   **********");
			}
		}
	}

	public void setSut(String sutName) {
		main.setAttribute("Setup", sutName);
	}

	public void setScenario(String scenarioName) {
		main.setAttribute("Scenario", scenarioName);
	}

	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void addExecutionProperty(String key, String value) {
		if (null == executionProperties) {
			main.setAttribute("Properties", "");
			return;
		}

		executionProperties.setProperty(key, value);
		main.setAttribute("Properties",
				StringUtils.propertiesToString(executionProperties));
	}
}
