package regression.report;

import java.io.File;
import java.io.File;
import java.io.IOException;
import java.util.List;

import jsystem.extensions.analyzers.text.CountText;
import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.RemoveLines;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.ReporterHelper;
import jsystem.framework.report.Summary;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;

public class ReporterTestsToPublish extends SystemTestCase {
	public void setUp() throws Exception {
	  }

	  public void testGetClassName() throws Exception {
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	    report.addProperty("testGetClassName", "1");

	    File file = new File("c:\\work\\project\\src\\com\\xxx\\MyClass.class");
	    File root = new File("c:\\work\\project\\src\\");
	    assertEquals("Fail to extract class name", "com.xxx.MyClass", StringUtils.getClassName(file.getPath(), root
	        .getPath()));
	    report.addProperty("testGetClassName_end", "2");
	    report.addProperty("ThirdProperty1", "3123");
	  }

	  /**
	   * Demonstrates how to add a test property. Tests properties will later be
	   * shown in the reports web application in the custom report builder. The
	   * user can select properties for his custom report. Make sure you keep
	   * properties values parsable to int/float/double This will enable creating
	   * nice graphs when exporting report to excel sheet.
	   */
	  public void testAddProperty() throws Exception {

	    report.addProperty("packet-loss", "0.05");
	    report.addProperty("bandwidth", "30");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  /**
	   * Link properties are properties which link to a report file or to a
	   * general URL.
	   */
	  public void testLinkProperties() throws Exception {
	    // demonstrates usage of utility method that copies the file
	    // into reports folder and adds a property link to it
	    ReporterHelper.copyFileToReporterAndAddLinkProperty(report, new File("jsystem.properties"), // file
	                                                  // that
	                                                  // will
	                                                  // be
	                                                  // copied
	                                                  // to
	        // test's report folder
	        "linkToFile", // name of property
	        "jsystem properties"); // link's title

	    // demonstrates a utility method that assumes file was
	    // copied into the report folder and just adds a property link to it
	    ReporterHelper.addLinkProperty(report, "jsystem.properties", // name
	                                    // of
	                                    // the
	                                    // file
	                                    // that
	                                    // was
	                                    // copied
	                                    // to
	        // the report folder
	        "linkToFileWithoutCopy", // name of property
	        "jsystem properties title"); // link's title

	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "linkToOneSports", // name
	                                              // of
	                                              // property
	        "one sports"); // link's title

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void test() throws IOException {

	    report.addProperty("test", "3");
	    // report.report("fail007",false);

	    report.startLevel("Level1", Reporter.MainFrame);

	    report.step("inside level1");

	    report.stopLevel();

	    report.report("out", false);

	    report.stopLevel();

	    report.startLevel("Level2", Reporter.MainFrame);

	    report.step("inside level2");

	    report.startLevel("Level3", Reporter.CurrentPlace);

	    report.step("inside level3");

	    report.stopLevel();

	    report.report("fail", false);
	    report.stopLevel();
	    report.addProperty("testend", "4");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStam1() throws IOException {

	    report.addProperty("testStam1", "5");
	    report.startLevel("Level1", Reporter.MainFrame);

	    report.step("inside level1");

	    report.stopLevel();

	    report.report("out", false);

	    report.startLevel("Level2", Reporter.MainFrame);

	    report.report("inside level2", false);

	    report.stopLevel();
	    report.addProperty("testStam1End", "6");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void test1() throws IOException {
	    report.addProperty("test1", "7");
	    report.startLevel("Level1", Reporter.CurrentPlace);

	    report.step("inside level1");

	    report.stopLevel();

	    report.startLevel("Level2", Reporter.CurrentPlace);

	    report.step("inside level2");

	    report.stopLevel();

	    report.startLevel("Level3", Reporter.CurrentPlace);

	    report.step("inside level3");

	    report.stopLevel();

	    report.startLevel("Level4", Reporter.CurrentPlace);

	    report.step("inside level4");

	    report.stopLevel();
	    for (int i = 0; i < 10; i++) {
	      report.report("report " + i, true);
	      report.addProperty("ThirdProperty1", "3123");
	      report.addProperty("FourhProperty", "4567");
	    }

	    report.startLevel("Level5", Reporter.CurrentPlace);

	    report.step("inside level5");
	    report.report("fail", false);
	    report.stopLevel();

	    report.report("fail", false);

	    report.addProperty("test1End", "7.9");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStam() throws IOException {

	    report.addProperty("testStam", "7.99");
	    report.startLevel("first level", Reporter.MainFrame);
	    report.startLevel("second level", Reporter.CurrentPlace);
	    report.startLevel("third level", Reporter.CurrentPlace);
	    String path = report.getCurrentTestFolder() + "\\";
	    long time = System.currentTimeMillis();
	    String fileName = "TestingLongBuffer_" + time + ".txt";
	    FileUtils.write(path + fileName, "Nizan Testing");
	    report.step(fileName);
	    report.addLink("Check This file out:", fileName);
	    report.addProperty("testStamEnd", "10");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testOne() throws IOException {
	    report.addProperty("testOne", "11");

	    report.startLevel("first level", Reporter.MainFrame);

	    report.setFailToWarning(true);
	    report.report("inside level4", false);
	    report.setFailToWarning(false);

	    report.stopLevel();
	    report.addProperty("testOneEnd", "12");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void test11() throws Exception {

	    report.addProperty("test11", "12");

	    MySystemObject mso = new MySystemObject();
	    report.startLevel("l1", Reporter.CurrentPlace);
	    report.startLevel("l2", Reporter.CurrentPlace);

	    report.stopLevel();
	    report.stopLevel();

	    report.startLevel("l3", Reporter.CurrentPlace);
	    report.stopLevel();

	    report.startLevel("l4", Reporter.CurrentPlace);

	    // report.startLevel("l5", Reporter.CurrentPlace);
	    // report.report("fail",false);
	    mso.setTestAgainsObject(new String("fff"));
	    mso.analyze(new FindText("s"));
	    // report.stopLevel();
	    // report.stopLevel();
	    report.addProperty("test11End", "13");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testObject() throws IOException {
	    report.addProperty("testObject", "14");
	    MySystemObject mso = new MySystemObject();

	    report.startLevel("l3", Reporter.MainFrame);
	    // report.stopLevel();

	    report.startLevel("l4", Reporter.MainFrame);

	    // report.startLevel("l5", Reporter.c);
	    // report.report("fail",false);
	    mso.setTestAgainsObject(new String("fff"));
	    mso.analyze(new FindText("s"));
	    report.addProperty("testObject", "15");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  class MySystemObject extends SystemObjectImpl {

	  }

	  public void testReportBuffering() {
	    report.addProperty("testReportBuffering", "15.6");
	    /*
	     * Start to buffer report
	     */
	    report.startBufferingReports();
	    /*
	     * this report will not be send to the reporters
	     */
	    report.step("Example to store report");
	    /*
	     * Stop and get the report buffer
	     */
	    report.stopBufferingReports();
	    List<ReportElement> reports = report.getReportsBuffer();

	    /*
	     * Clean the buffer If the user will not do it, it will be done
	     * automaticly in the end of the test.
	     */
	    report.clearReportsBuffer();

	    assertEquals(1, reports.size());
	    /*
	     * Send the report back, now it will be send to the reporters
	     */
	    report.report(reports.get(0));
	    report.addProperty("testReportBufferingEnd", "16.6");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testBufferedReportsTimes() {
	    report.addProperty("testBufferedReportsTimes", "16.7");
	    report.report("before buffer");
	    report.startBufferingReports();
	    report.report("Report1");
	    report.step("This is a step");
	    sleep(10000);
	    report.stopBufferingReports();
	    List<ReportElement> reports = report.getReportsBuffer();
	    for (ReportElement re : reports) {
	      report.report(re);
	      report.addProperty("ThirdProperty1", "3123");
	      report.addProperty("FourhProperty", "4567");
	    }
	    report.report("After buffer");
	    report.addProperty("testBufferedReportsTimesEnd", "17.7");

	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testReport() {

	    report.addProperty("testReport", "18.7");
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report.addProperty("testReport", "19.7");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testReport2() {

	    report.addProperty("testReport2", "19.7");
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report
	        .report(
	            "kds; lksj l;kj al;kj l;asdfj ;klsdjf ;klsdfj klsd",
	            " dsl;f kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,sd alk ; sdflaj",
	            true);
	    report.addProperty("testReport2", "1123.7");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testSummary() throws Exception {
	    report.addProperty("testSummary", "21.7");
	    Summary.getInstance().setVersion("xxx");
	    report.addProperty("testSummaryEnd", "221.7");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testReporting() throws Exception {
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.ynet.co.il", "testReporting", // name
	                                              // of
	                                              // property
	        "one testReporting"); // link's title
	    report.addProperty("testReporting", "123.2");
	    report.report("testing1");
	    report.report("testing", "xxxxxx", true);
	    report.report("testing", new Exception("xxxx"));
	    report.addProperty("testReporting", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  CountText find = null;
	  String text = "JSystem is a framework for writing and running automated tests, based on JUnit. Its main goal is to support automation of functional/system testing. See our home page for more information.";

	  public void testSimpleCount() throws Exception {
	    ReporterHelper.addLinkProperty(report, "http://www.walla.co.il", "testSimpleCount", // name
	                                              // of
	                                              // property
	        "one testReporting"); // link's title

	    find = new CountText("framework", 1);
	    find.setTestAgainst(text);
	    find.analyze();
	    assertTrue("The word: framework should be found once", find.getStatus());
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testCount() {

	    report.addProperty("testCount", "200.2");
	    find = new CountText("is", 2);
	    find.setTestAgainst(text);
	    find.analyze();
	    assertTrue("The word: 'is' should be found twice", find.getStatus());
	    report.addProperty("testCountEnd", "201.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testDeviation() {
	    report.addProperty("testDeviation", "202.2");
	    find = new CountText("is", 1, 1);
	    find.setTestAgainst(text);
	    find.analyze();
	    assertTrue("The word: 'is' should be found twice", find.getStatus());
	    report.addProperty("testDeviationEnd", "203.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testDeviation2() {
	    report.addProperty("testDeviation2", "202.2");
	    find = new CountText("is", 4, 1);
	    find.setTestAgainst(text);
	    find.analyze();
	    assertFalse("The word: 'is' should be found twice", find.getStatus());
	    report.addProperty("testDeviation2End", "203.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testLineRemovel() {
	    report.addProperty("testLineRemovel", "203.2");
	    String text = "xxxx\nyyyyy\ndddd";
	    DummyObject dummy = new DummyObject();
	    dummy.setTestAgainsObject(text);
	    dummy.analyze(new FindText("yyyy"));
	    dummy.analyze(new RemoveLines("yyy"));
	    dummy.analyze(new TextNotFound("yyyy"));
	    report.addProperty("End", "203.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  class DummyObject extends SystemObjectImpl {

	  }

	  TextNotFound find1 = null;
	  String text1 = "JSystem is a framework for writing and running automated tests, based on JUnit. Its main goal is to support automation of functional/system testing. See our home page for more information.";

	  public void testSimpleFind() {
	    report.addProperty("testSimpleFind", "203.2");
	    find1 = new TextNotFound("frameworkx");
	    find1.setTestAgainst(text);
	    find1.analyze();
	    assertTrue("The text 'frameworkx' shouldn't be bound", find.getStatus());
	    report.addProperty("testSimpleFindEnd", "2043.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testSimpleFindNegative() {
	    report.addProperty("testSimpleFindNegative", "203.2");
	    find1 = new TextNotFound("framework");
	    find1.setTestAgainst(text);
	    find1.analyze();
	    assertFalse("The text 'framework' should be bound", find.getStatus());
	    report.addProperty("testSimpleFindNegative1", "2234.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testRun() throws Exception {
	    throw new Exception("Fail");
	  }

	  public void testExample() throws Exception {
	    report.addProperty("testExample", "203.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    sleep(2000);
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testExampleEnd", "203.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");

	  }

	  public void newProperty1() throws Exception {
	    report.addProperty("newProperty1", "111.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStdability1", // name
	                                              // of
	                                              // property
	        "testStdability1"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEnd1", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability2() throws Exception {
	    report.addProperty("newProperty2", "112.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStdability2", // name
	                                              // of
	                                              // property
	        "testStdability2"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd2", "112.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability3() throws Exception {
	    report.addProperty("newProperty3", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStadbility3", // name
	                                              // of
	                                              // property
	        "testStdability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd2", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability4() throws Exception {
	    report.addProperty("newProperty4", "114.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStadbility4", // name
	                                              // of
	                                              // property
	        "testStdbility4"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd4", "114.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability5() throws Exception {
	    report.addProperty("newProperty3", "114.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability3", // name
	                                              // of
	                                              // property
	        "testSdtability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd4", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability6() throws Exception {
	    report.addProperty("newProperty6", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStadbility6", // name
	                                              // of
	                                              // property
	        "testStdability6"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd6", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability7() throws Exception {
	    report.addProperty("newProperty7", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability7", // name
	                                              // of
	                                              // property
	        "testStdability7"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd7", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability8() throws Exception {
	    report.addProperty("newProperty8", "118.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability8", // name
	                                              // of
	                                              // property
	        "testStddability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd8", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability9() throws Exception {
	    report.addProperty("newProperty9", "193.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability9", // name
	                                              // of
	                                              // property
	        "testSdtability9"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd9", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability10() throws Exception {
	    report.addProperty("newProperty10", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability10", // name
	                                              // of
	                                              // property
	        "testSdtability10"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd10", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability11() throws Exception {
	    report.addProperty("newProperty11", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability11", // name
	                                              // of
	                                              // property
	        "tesdtStability11"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd11", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability12() throws Exception {
	    report.addProperty("testStability12", "1112.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability12", // name
	                                              // of
	                                              // property
	        "testdStability12"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd12", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability13() throws Exception {
	    report.addProperty("newProperty13", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability3", // name
	                                              // of
	                                              // property
	        "tesdtStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd13", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability14() throws Exception {
	    report.addProperty("newProperty14", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3", // name
	                                              // of
	                                              // property
	        "testdStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd14", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability15() throws Exception {
	    report.addProperty("newProperty3", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability3", // name
	                                              // of
	                                              // property
	        "tesdtStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd2", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability16() throws Exception {
	    report.addProperty("newProperty16", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability3", // name
	                                              // of
	                                              // property
	        "testdStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd16", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability17() throws Exception {
	    report.addProperty("newProperty18", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability3", // name
	                                              // of
	                                              // property
	        "tesdtStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd18", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability18() throws Exception {
	    report.addProperty("newProperty3", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability3", // name
	                                              // of
	                                              // property
	        "testdStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd2", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability19() throws Exception {
	    report.addProperty("newProperty3", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3", // name
	                                              // of
	                                              // property
	        "tesdtStability3"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd2", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability20() throws Exception {
	    report.addProperty("newProperty20", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability20", // name
	                                              // of
	                                              // property
	        "tesdtStability20"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd20", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability21() throws Exception {
	    report.addProperty("newProperty21", "2113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability21", // name
	                                              // of
	                                              // property
	        "tesdtStability21"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd21", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability22() throws Exception {
	    report.addProperty("newProperty22", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability22", // name
	                                              // of
	                                              // property
	        "tedstStability22"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd22", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability23() throws Exception {
	    report.addProperty("newProperty23", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStdability23", // name
	                                              // of
	                                              // property
	        "testStdability23"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd23", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability24() throws Exception {
	    report.addProperty("newProperty24", "11243.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability24", // name
	                                              // of
	                                              // property
	        "testSdtability24"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd24", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability25() throws Exception {
	    report.addProperty("newProperty25", "125.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability25", // name
	                                              // of
	                                              // property
	        "tesdtStability25"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd25", "125.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability26() throws Exception {
	    report.addProperty("newProperty26", "26.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability26", // name
	                                              // of
	                                              // property
	        "tesdtStability26"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd26", "1263.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void newProperty27() throws Exception {
	    report.addProperty("testStability26", "1126.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability26", // name
	                                              // of
	                                              // property
	        "tesdtStability26"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd26", "1263.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability28() throws Exception {
	    report.addProperty("newProperty28", "12283.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability28", // name
	                                              // of
	                                              // property
	        "testdStability28"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd28", "2813.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability29() throws Exception {
	    report.addProperty("newProperty29", "113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability29", // name
	                                              // of
	                                              // property
	        "testdStability29"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd29", "2913.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void newProperty30() throws Exception {
	    report.addProperty("testStability30", "303.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability30", // name
	                                              // of
	                                              // property
	        "testSdtability30"); // link's title
	    report.report("This is the second step", "step 30", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd30", "1130.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability31() throws Exception {
	    report.addProperty("newProperty31", "1131.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability31", // name
	                                              // of
	                                              // property
	        "testSdtability31"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd31", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability32() throws Exception {
	    report.addProperty("newProperty31", "13113.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability31", // name
	                                              // of
	                                              // property
	        "testdStability31"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd31", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability33() throws Exception {
	    report.addProperty("newProperty32", "132.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSdtability33", // name
	                                              // of
	                                              // property
	        "testSdtabilityv33"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd33", "11333.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability38() throws Exception {
	    report.addProperty("newProperty38", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStability34", // name
	                                              // of
	                                              // property
	        "tedstStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd38", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability39() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStability34", // name
	                                              // of
	                                              // property
	        "tdestStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd39", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability40() throws Exception {
	    report.addProperty("newProperty40", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStability34", // name
	                                              // of
	                                              // property
	        "tdestStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd40", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability41() throws Exception {
	    report.addProperty("newProperty41", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStability41", // name
	                                              // of
	                                              // property
	        "tdestStability41"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd41", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability42() throws Exception {
	    report.addProperty("newProperty42", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStability42", // name
	                                              // of
	                                              // property
	        "tdestStability42"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd42", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability43() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability43", // name
	                                              // of
	                                              // property
	        "tedstStability43"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEnd43", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability44() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStabilit44", // name
	                                              // of
	                                              // property
	        "tdestStability44"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd44", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability45() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability45", // name
	                                              // of
	                                              // property
	        "tdestStability45"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd45", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability46() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability46", // name
	                                              // of
	                                              // property
	        "tedstStability46"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd46", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability47() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability47", // name
	                                              // of
	                                              // property
	        "tedstStability347"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd47", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability48() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedtStability48", // name
	                                              // of
	                                              // property
	        "tedstStability48"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd48", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability49() throws Exception {
	    report.addProperty("testStabilit49", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability49", // name
	                                              // of
	                                              // property
	        "testdStability49"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEnd49", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability50() throws Exception {
	    report.addProperty("newProperty50", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability50", // name
	                                              // of
	                                              // property
	        "tesdtStability50"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd50", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability51() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability34", // name
	                                              // of
	                                              // property
	        "tesdtStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd34", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability52() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability34", // name
	                                              // of
	                                              // property
	        "testdStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd34", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability53() throws Exception {
	    report.addProperty("newProperty534", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability53", // name
	                                              // of
	                                              // property
	        "tesdtStability53"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd53", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability54() throws Exception {
	    report.addProperty("newProperty54", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability54", // name
	                                              // of
	                                              // property
	        "tesdtStability54"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd54", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability55() throws Exception {
	    report.addProperty("newProperty55", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability55", // name
	                                              // of
	                                              // property
	        "tesdtStability55"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd55", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability56() throws Exception {
	    report.addProperty("newProperty56", "13563.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability56", // name
	                                              // of
	                                              // property
	        "testdStability56"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd56", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void newProperty57() throws Exception {
	    report.addProperty("testStability57", "1573.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability58", // name
	                                              // of
	                                              // property
	        "testdStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEnd58", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability59() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability59", // name
	                                              // of
	                                              // property
	        "tesdtStability59"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd59", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void newProperty60() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability60", // name
	                                              // of
	                                              // property
	        "tesdtStability60"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEnd60", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability61() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability61", // name
	                                              // of
	                                              // property
	        "tesdtStability361"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd61", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability62() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability62", // name
	                                              // of
	                                              // property
	        "tesdtStability62"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd62", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability63() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability63", // name
	                                              // of
	                                              // property
	        "tesdtStability63"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd63", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStabilit64() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability64", // name
	                                              // of
	                                              // property
	        "tedstStability64"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd64", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability65() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tedstStability65", // name
	                                              // of
	                                              // property
	        "tedstStability65"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd65", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability66() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability66", // name
	                                              // of
	                                              // property
	        "tesdtStability66"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd66", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability67() throws Exception {
	    report.addProperty("newProperty67", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability67", // name
	                                              // of
	                                              // property
	        "tesdtStability67"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd67", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability68() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability68", // name
	                                              // of
	                                              // property
	        "tesdtStability68"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd68", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability69() throws Exception {
	    report.addProperty("newProperty69", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability69", // name
	                                              // of
	                                              // property
	        "tesdtStability69"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd69", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability70() throws Exception {
	    report.addProperty("newProperty70", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability70", // name
	                                              // of
	                                              // property
	        "testdStability70"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd70", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability80() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability80", // name
	                                              // of
	                                              // property
	        "testdStability80"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("newPropertyEn80", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability81() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability81", // name
	                                              // of
	                                              // property
	        "testdStability81"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd81", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability821() throws Exception {
	    report.addProperty("newProperty82", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability82", // name
	                                              // of
	                                              // property
	        "testdStability82"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd82", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void newProperty831() throws Exception {
	    report.addProperty("testStability34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability83", // name
	                                              // of
	                                              // property
	        "tesdStability83"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd83", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability101() throws Exception {
	    report.addProperty("newProperty101", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3101", // name
	                                                // of
	                                                // property
	        "tesdtStability34101"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd3101", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability102() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3102", // name
	                                                // of
	                                                // property
	        "tesdtStability34102"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd3102", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability3102() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability34102", // name
	                                                // of
	                                                // property
	        "tesdtStability34102"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd34102", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability104() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3104", // name
	                                                // of
	                                                // property
	        "tesdtStability3104"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd3104", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability105() throws Exception {
	    report.addProperty("newProperty105", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability104", // name
	                                              // of
	                                              // property
	        "tedstStability8105"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd105", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability106() throws Exception {
	    report.addProperty("newProperty83", "133");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tdestStabilit106", // name
	                                              // of
	                                              // property
	        "tedstStability106"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd106", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability107() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability3107", // name
	                                                // of
	                                                // property
	        "tesdtStability34107"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd107", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability108() throws Exception {
	    report.addProperty("newProperty108", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesdtStability108", // name
	                                              // of
	                                              // property
	        "tesdtStability108"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd108", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability86() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testdStability86", // name
	                                              // of
	                                              // property
	        "testdStability86"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd86", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability87() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStability87", // name
	                                              // of
	                                              // property
	        "testSjtability87"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd87", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability89() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testfStability89", // name
	                                              // of
	                                              // property
	        "testfStability89"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd89", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability90() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testfStability90", // name
	                                              // of
	                                              // property
	        "testfStability90"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd90", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability110() throws Exception {
	    report.addProperty("newProperty110", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSftability110", // name
	                                              // of
	                                              // property
	        "testSftability110"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd110", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability111() throws Exception {
	    report.addProperty("newProperty111", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSftability111", // name
	                                              // of
	                                              // property
	        "tesftStability111"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd111", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability112() throws Exception {
	    report.addProperty("newProperty112", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "tesftStability112", // name
	                                              // of
	                                              // property
	        "tesftStability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd112", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability113() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testfStability113", // name
	                                              // of
	                                              // property
	        "testSftability113"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd91", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability114() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSftability114", // name
	                                              // of
	                                              // property
	        "testSftability114"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd114", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability115() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSftabilit115", // name
	                                              // of
	                                              // property
	        "testSftability115"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd115", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability116() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testSftability34", // name
	                                              // of
	                                              // property
	        "testSftability34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd115", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability117() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStafbility117", // name
	                                              // of
	                                              // property
	        "testStabfility3117"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd3117", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability35() throws Exception {
	    report.addProperty("newProperty354", "13453.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStabfility34", // name
	                                              // of
	                                              // property
	        "testStabfility34"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd35", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	  }

	  public void testStability36() throws Exception {
	    report.addProperty("newProperty34", "1343.2");
	    report.step("This is an example of test report");
	    report.report("This is the first step", "step 1", true);
	    // demonstrates a general link property addition
	    ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "testStabfility36", // name
	                                              // of
	                                              // property
	        "testStabfgility336"); // link's title
	    report.report("This is the second step", "step 2", true);
	    // assertEquals(true,false);
	    // throw new Exception("xxx");
	    report.addProperty("testStabilityEnd36", "113.2");
	    report.addProperty("ThirdProperty1", "3123");
	    report.addProperty("FourhProperty", "4567");
	    report.addProperty("FourhProperty", "4567");
	  }

}
