/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.reporter;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import jsystem.extensions.reporter.HtmlReporterUtils;
import jsystem.framework.TestProperties;
import jsystem.framework.graph.Graph;
import jsystem.framework.graph.GraphMonitorManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.ReporterHelper;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase4;

import org.junit.Test;

/**
 * Demonstrates reporter capabilities. Issues that are covered:
 * 
 * 1. Basic report usage - report - step 2. report with fail/warning 3.
 * failToPass, failToWarning 4. addLink 5. addGraph 6. leveling 7. Show message
 * 7. internal tests
 */
public class ReporterTest extends SystemTestCase4 {

	/**
	 * Demonstrates basic reporter methods.
	 */
	@Test
	public void reporterBasics() throws Exception {
		// Logs simple reporter message
		report.report("Report with only title");

		// Logs a report message with title and internal content with success
		// status
		report.report("Title", "Internal message", true);
	}

	/**
	 * Demonstrates reporter message which causes test to fail or to be in
	 * warning status.
	 */
	@Test
	public void reporterWithErrors() throws Exception {
		report.report("report with error", "internal message", Reporter.FAIL /**
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * Reporter.WARNING
		 */
		);
	}

	/**
	 * Demonstrates reporter message which causes test to fail with an
	 * exception.
	 */
	@Test
	public void reporterWithException() throws Exception {
		report.report("report with exception", new Exception("An error"));
	}

	/**
	 * Demonstrates step usage. <br>
	 * Steps are showed in bold format in the HTML reporter and Jsystem reporter
	 * tab.
	 * 
	 */
	@Test
	public void reporterWithSteps() throws Exception {
		report.step("1. This is the first step");
		report.report("Some regular report element");
		report.step("2. Another step");
		report.report("Another regular report element");
	}

	/**
	 * Demonstrates adding a link to a file to the reporter. The important issue
	 * to note here is that in order to make the reporter output self contained
	 * the file to which we want to add a link should be copied into the reports
	 * folder. To do that there is a utility class that copies the file to the
	 * reporter folder and adds a link to the file.
	 * 
	 */
	@Test
	@TestProperties(name = "Reporter with link to file", paramsInclude = { "" })
	public void reporterWithLinkToFile() throws Exception {
		final File txtFile = File.createTempFile("someTempFile", ".txt");
		String content = "This is the file content. This is the file content";
		org.apache.commons.io.FileUtils.write(txtFile, content);
		ReporterHelper.copyFileToReporterAndAddLink(report, txtFile, "Some text file");
		txtFile.delete();
	}

	/**
	 * As we have seen above, a report can cause a test to fail. In some cases
	 * the author of a test knows that a certain operation will submit a fail
	 * report but he doesn't want the test to fail. In order to prevent the test
	 * from failing the author can use the setFailToPass(true) method. Raising
	 * this flag signals the reporter service to conver all failure messages to
	 * success messages. Don't forget to turn of the flag at the end of the
	 * relevant code section.
	 */
	@Test
	public void reporterWithFailToPass() throws Exception {
		report.setFailToPass(true);
		try {
			report.report("fail to pass exception", new Exception("An error occured"));
			report.report("fail to pass flag", "internal message", false);
		} finally {
			report.setFailToPass(false);
		}
	}

	/**
	 * Demonstrates adding a link to the report.
	 */
	@Test
	public void reporterAddLink() throws Exception {
		report.addLink("My Link", "www.dummyLink.com");
	}

	/**
	 * Demonstrates report with graph.
	 */
	@Test
	public void reporterWithGraph() throws Exception {
		Graph graph = GraphMonitorManager.getInstance().allocateGraph(
				"Dummy graph of number of recieved packets in ping operation over time", "received packets");
		for (int i = 0; i < 10; i++) {
			int receive = new Random().nextInt();
			graph.add("receive", i, receive);
		}
		graph.show(report);
	}

	/**
	 * Screenshot is actually every image file that is copied to the report
	 * folder and in which the report is having a link to.
	 */
	@Test
	@TestProperties(name = "Report with screenshot", paramsInclude = { "" })
	public void reporterWithScreenshot() throws Exception {
		ReporterHelper.copyFileToReporterAndAddLink(report, takeScreenshot(), "Screenshot of your desktop");
	}

	private File takeScreenshot() throws Exception {
		Robot robot = new Robot();
		BufferedImage screenShot = robot
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		File screenshotFile = File.createTempFile("screenshot", "jpg");
		ImageIO.write(screenShot, "JPG", screenshotFile);
		return screenshotFile;

	}

	/**
	 * Showing different types of report elements.
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Test With Different Report Elements", paramsInclude = { "file", "str", "i", "date",
			"strArr" })
	public void reporterWithDifferentReportElements() throws Exception {
		report.report("Only title");
		report.report("Title with message that was successful", "message", true);
		report.report("Title with message which faile", "message", false);
		File txtFile = new File("myText.txt");
		txtFile.createNewFile();
		org.apache.commons.io.FileUtils.write(txtFile, "This is the file");
		ReporterHelper.copyFileToReporterAndAddLink(report, txtFile, "This is the link title");
		txtFile.delete();
		report.startLevel("Start level", 2);
		report.report("In level 1");
		report.report("In level 1", true);
		report.startLevel("Start level 2", 2);
		report.report("In level2");
		report.stopLevel();
		report.stopLevel();

	}

	/**
	 * Demonstration of the use of complex levels in the report. The important
	 * thing is that the levels receives the status of the report elements that
	 * are nested inside them.
	 */
	@Test
	@TestProperties(name = "Reporter with complex levels", paramsInclude = { "" })
	public void reporterWithComplexLevels() throws Exception {
		report.startLevel("Starting level 0", 2);
		try {
			report.report("Inside level 0");
			report.report("Faliure inside level 0", false);
			report.report("Message inside level 0", "Some kind of message", true);
			report.report("Inside level 0");
			report.startLevel("Starting level 1", 2);
			try {
				report.report("Warning inside level 1", 2);
				report.report("Message inside level 1 - Level with warning", "Some kind of message", 2);
				ReporterHelper.copyFileToReporterAndAddLink(report, takeScreenshot(), "Your screenshot");
				report.startLevel("Starting level 2", 2);
				try {
					report.report("Success inside level 2", true);
					report.report("Message inside level 2", "Some kind of message", true);
					report.report("Inside level 2");
					ReporterHelper.copyFileToReporterAndAddLink(report, takeScreenshot(), "Your screenshot");
				} finally {
					report.stopLevel();
				}

				report.report("Inside level 1");
			} finally {
				report.stopLevel();
			}
			report.report("Inside level 0");
		} finally {
			// We would like it in a finally block in case an exception is
			// thrown before the stop level happens.
			report.stopLevel();
		}
	}

	/**
	 * The report API enables the programmer to create an hierarchical report
	 * were each level contains a link to the next level. Using the leveling,
	 * the user can build cleaner and more focused reports.
	 */
	@Test
	public void reporterWithLeveling() throws Exception {
		report.startLevel("first level", Reporter.MainFrame);
		report.report("message in level 1");
		report.startLevel("second level", Reporter.CurrentPlace);
		report.report("message in level 2");
		report.startLevel("third level", Reporter.CurrentPlace);
		report.report("message in level 3");
		report.stopLevel();
		report.report("another message in level 2");
		report.stopLevel();
		report.report("another message in level 1");
		report.stopLevel();
		report.report("message in main report page");

	}

	/**
	 * This method shows how to pop up interactive messages to the user using
	 * the reporter API.
	 */
	@Test
	public void reporterWithInteractiveMessage() throws Exception {
		int res = report.showConfirmDialog("Confirm Dialog", "Continue test?", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
		report.step("user reply = " + res);

	}

	/**
	 * Demonstrates how to emulate a report of several tests using the reporter
	 * startReport API.
	 */
	@Test
	public void internalTests() throws Exception {
		for (int i = 0; i < 5; i++) {
			report.startReport("testInternalTest" + i, "");
			report.report("in internal " + i, "this is my message", true);
			report.endReport();
		}
	}

	/**
	 * This test demonstrates how to use the package list feature which was
	 * added in jsystem 5.5. The feature enables the developer to control the
	 * left area of the html report. This example shows how to put a link to a
	 * test and a link to jsystemtest.org in the upper area.
	 * 
	 * To make the example work, in jsystem.properties add the following
	 * property: html.package.list=package.properties Now run this test. When
	 * opening the html report you will see the in the left upper area of the
	 * report you can see the link to this test.
	 * 
	 */
	@Test
	public void reporterWithLinkToTest() throws Exception {
		// create reference to test.
		String currentFullFileName = HtmlReporterUtils.getCurrentTestFileName();
		File reportPath = new File(report.getCurrentTestFolder()).getParentFile();
		String ref = FileUtils.getRelativePath(new File(currentFullFileName), reportPath);
		ref = "./" + ref;

		// create properties file and add links
		Properties p = new Properties();
		p.setProperty("link to test", ref);
		p.setProperty("jsystem site", "http://jsystemtest.org");
		FileUtils.savePropertiesToFile(p, "package.properties");
	}

	/**
	 * Demonstrates custom html in the report
	 */
	@Test
	public void reporterWithCustomHtml() {
		report.report("<table><tr><td>cell 1</td><td>cell 2</td></tr></table>",
				"<table><tr><td>cell 1</td><td>cell 2</td></tr></table>", Reporter.PASS, false, true, false, false);
	}
}
