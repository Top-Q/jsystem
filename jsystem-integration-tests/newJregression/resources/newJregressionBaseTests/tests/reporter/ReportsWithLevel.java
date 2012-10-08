package reporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.ReporterHelper;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

public class ReportsWithLevel extends SystemTestCase {

	public void testSimpleWithLink() throws Exception {
		report.report("Title - simple", "message --- ", false);
	}

	public void testSeeOtherSimpleReportFunctionality() {
		report.step("Verifying that step is working correctly");
		report.addLink("ling to ynet", "http://www.ynet.co.il");
		report.report("long<br>", "<b>internal</b>", Reporter.WARNING, true,
				true, false, false);
	}

	public void testSimpleWithLinkToFile() throws Exception {
		FileUtils.write("myFile.txt", "file message");
		File f = new File("myFile.txt");
		ReporterHelper.copyFileToReporterAndAddLink(report, f, "Link to file");
	}

	public void testLevelWithLinkToFile() throws Exception {
		report.startLevel("FileLevel", EnumReportLevel.CurrentPlace);
		FileUtils.write("myFile.txt", "file message");
		File f = new File("myFile.txt");
		ReporterHelper.copyFileToReporterAndAddLink(report, f, "Link to file");
		report.stopLevel();
	}

	public void testSimpleWithLinkToFileWereFileIsInLog() throws Exception {
		FileUtils.write("myFileinRootLog.txt", "file message");
		File f = new File("myFileinRootLog.txt");
		File destination = new File(new File(report.getCurrentTestFolder())
				.getParent(), f.getName());
		FileUtils.copyFile(f, destination);
		report.addLink("file", f.getName());
	}

	public void testLevelWithLinkToFileWereFileIsInLog() throws Exception {
		report.startLevel("FileLevel", EnumReportLevel.CurrentPlace);
		FileUtils.write("myFileinRootLog.txt", "file message");
		File f = new File("myFileinRootLog.txt");
		File destination = new File(new File(report.getCurrentTestFolder())
				.getParent(), f.getName());
		FileUtils.copyFile(f, destination);
		report.addLink("file", f.getName());
		report.stopLevel();
	}

	public void testSimpleWithLinkOneLevel() throws Exception {
		report
				.startLevel("Level one checking it",
						EnumReportLevel.CurrentPlace);
		report.report("Message 1 ", "level 1", true);
		report.stopLevel();
	}

	public void testReportLevelSimple() throws Exception {
		report.report("Hello world");
		report
				.startLevel("Level one checking it",
						EnumReportLevel.CurrentPlace);
		report.report("Message 1 ", "level 1", true);
		report
				.startLevel("Level two checking it",
						EnumReportLevel.CurrentPlace);
		report.report("Message 2 ", "level 2", true);
		report.report("Message 3 ", "level 3", true);
		report.stopLevel();
		report.report("Message 4 ", "level 1", true);
		report.stopLevel();
		report.report("Message 5 ", "level 0", true);
		report.report("Message 6 ", "level 0", true);
	}

	public void testReportLevelSimpleWithMainFrame() throws Exception {
		report.report("Hello world");
		report
				.startLevel("Level one checking it",
						EnumReportLevel.CurrentPlace);
		report.report("Message 1 ", "level 1", true);
		report
				.startLevel("Level two checking it",
						EnumReportLevel.CurrentPlace);
		report.report("Message 2 ", "level 2", true);
		report.report("Message 3 ", "level 3", true);
		report.startLevel("level 3", EnumReportLevel.MainFrame);
		report.report("Message 4 ", "level 3", true);
		report.report("Message 5 ", "level 3", true);
		report.stopLevel();
		report.report("Message 6 ", "level 1", true);
		report.report("Message 7 ", "level 0", true);
		report.report("Message 8 ", "level 0", true);
	}

	public void testReportLevelSimpleWithErrors() throws Exception {

		report.report("Hello world");
		report.startLevel("Level one checking it",EnumReportLevel.CurrentPlace);
		report.report("Message 1 ", "level 1", true);
		

		report.startLevel("Level two checking it",EnumReportLevel.CurrentPlace);
		report.report("Message 2 ", "level 2", true);
		report.report("Message 3 ", "level 3", false);
		report.stopLevel();

		
		report.report("I'm in level 1 again");

		report.startLevel("Another level", EnumReportLevel.CurrentPlace);
		report.report("yes error","errrr",Reporter.FAIL);
		report.stopLevel();

		report.startLevel("Third level", EnumReportLevel.CurrentPlace);
		report.report("wrning","babab",Reporter.WARNING);
		report.stopLevel();

		report.stopLevel();
	}

	public void testAddLink() throws Exception {
		String localDirectory = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.LOG_FOLDER)
				+ File.separator + "current" + File.separator;
		FileUtils.write("automation.txt","text");
		File source = new File("automation.txt");
		File destination = new File(localDirectory, "automation.txt");
		FileUtils.copyFile(source, destination);
		report.startLevel("This is add links test!", Reporter.MainFrame);
		report.addLink("add external link under level", source.getAbsolutePath());
		report.addLink("add link with level", "automation.txt");
		ReporterHelper.addLinkToExternalLocation(report, "title2","automation.txt");
		report.stopLevel();
	}

	public void testReportsInBuffer() throws IOException {
		report.report("Before buffering");
		report.startBufferingReports(true);
		report.report("Start Me!!!");
		report.startLevel("Level 1", Reporter.EnumReportLevel.CurrentPlace);
		report.report("Report 1");
		report.report("Report 2");
		report.stopLevel();
		report.report("report 3");
		report.stopBufferingReports();
		report.report("After buffering");
		List<ReportElement> re = report.getReportsBuffer();
		for (ReportElement r : re) {
			report.report(r);
		}
	}
	
	public void testReportsOnlyLevels() throws IOException {
		report.startLevel("new level", EnumReportLevel.CurrentPlace);
	}
	public void testReportsOneLevelOneError() throws IOException {
		report.startLevel("new level", EnumReportLevel.CurrentPlace);
		report.report("warn title","warn message",Reporter.WARNING);
		report.startLevel("another level", EnumReportLevel.CurrentPlace);
		report.report("error title","error message",Reporter.FAIL);
	}
	
	public void testFromWebsense() throws Exception {
		report.report("Report PASS. step before start level",Reporter.PASS);

        report.startLevel("Level 1", Reporter.CurrentPlace);

        report.report("Report PASS. step_1 in start level(1)",Reporter.PASS);

        report.startLevel("Level 2-0", Reporter.CurrentPlace);

        report.report("Report FAIL. step_1 in start level(2)",Reporter.FAIL);

        report.stopLevel();

        report.startLevel("Level 2-1", Reporter.CurrentPlace);

        report.report("Report PASS. step_2 in start level(2)",Reporter.PASS);

        report.stopLevel();

        report.startLevel("Level 2-2", Reporter.CurrentPlace);

        report.report("Report FAIL. step_3 in start level(2)",Reporter.FAIL);

        report.report("Report PASS. step_3(2) in start level(2)",Reporter.PASS);

        report.stopLevel();

        report.startLevel("Level 2-3", Reporter.CurrentPlace);

        report.report("Report PASS. step_4 in start level(2)",Reporter.PASS);

        report.report("Report FAIL. step_4(2) in start level(2)",Reporter.FAIL);

        report.stopLevel();

        report.startLevel("Level 2-4", Reporter.CurrentPlace);

        report.report("Report FAIL. step_5 in start level(2)",Reporter.FAIL);

        report.stopLevel();

        report.startLevel("Level 2-5", Reporter.CurrentPlace);

        report.report("Report PASS. step_6 in start level(2)",Reporter.PASS);

        report.stopLevel();

        report.report("Report PASS. step_2 in start level(1)",Reporter.PASS);

        report.stopLevel();
	}
}
