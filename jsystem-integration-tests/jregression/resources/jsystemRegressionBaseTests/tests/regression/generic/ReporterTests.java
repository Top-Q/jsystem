package regression.generic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import jsystem.framework.TestProperties;
import jsystem.framework.graph.Graph;
import jsystem.framework.graph.GraphMonitorManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.ReporterHelper;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

import com.aqua.excel.ExcelFile;

/**
 * This class contains test that execute report functions
 * @author aqua
 *
 */
public class ReporterTests extends SystemTestCase {
	private String title;
	private String pressOn;
	private int expected;
	private Logger log = Logger.getLogger(ReporterTests.class.getName());
	public int getExpected() {
		return expected;
	}


	public void setExpected(int expected) {
		this.expected = expected;
	}


	public String getPressOn() {
		return pressOn;
	}


	public void setPressOn(String pressOn) {
		this.pressOn = pressOn;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * This test do nothing
	 */
	public void testBasicParameters(){
		report.report("nothing");
	}
	
	
	public void testReportWithMessage(){
		report.report("message report" , "this is report message" ,0);
	}
	
	public void testReportWithoutMessage(){
		report.report("report without message",0);
	}
	
	public void testWithStep() {
		report.step("check step");
	}
	
	public void testWith2Steps() {
		report.step("This is the first step");
		report.step("This is the second step");
	}

	public void testWithError() throws Exception {
		throw new Exception("This is the exception error");
	}

	public void testWithFailer() {
		assertTrue("should be true", false);
	}

	public void testWithFailNoException() {
		report.report("Fail report", false);
	}
	
	public void testRepFailToPass() {
		report.setFailToPass(true);
		report.report("this report should pass", false);
		report.setFailToPass(false);
	}
	
	public void testFailToPass() {
		report.setFailToPass(true);
		report.report("this report should pass", false);
		report.report("warning", null, Reporter.WARNING, false);
		report.setFailToPass(false);
	}

	public void testWithWarning() {
		report.report("warning title", null, Reporter.WARNING, false);
	}

	public void testFailToWarning() {
		report.setFailToWarning(true);
		report.report("this report should warn", false);
		report.setFailToWarning(false);
	}

	public void testSetSilent() {
		report.setSilent(true);
		report.report("report1");
		report.report("report2", false);
		report.setSilent(false);
	}

	public void testGetCurrentTestFolder() {
		report.step(report.getCurrentTestFolder());
	}

	public void testSaveFile() {
		report.saveFile("myFile.bin", new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		File savedFile = new File(report.getCurrentTestFolder(), "myFile.bin");
		assertTrue("File couldn't be found: " + savedFile.getAbsolutePath(), savedFile.exists());
	}
	
	public void testReportSaveFile() {
		report.saveFile("myFile.txt", "check report save file".getBytes());
		report.addLink("myFile.txt", "myFile.txt");
		File savedFile = new File(report.getCurrentTestFolder(), "myFile.txt");
		assertTrue("File couldn't be found: " + savedFile.getAbsolutePath(), savedFile.exists());
	}

	public void testStartEndReport() {
		for (int i = 0; i < 2; i++) {
			report.startReport("testInternalTestExample", null);
			report.step("Step for test: " + (i + 1));
			report.endReport();
		}
	}
	
	/**
	 * Demonstrates report with graph
	 */
	public void testReporterWithLeveling() throws Exception{
		report.startLevel("first level", Reporter.MainFrame);
		report.report("message in level 1");
		report.startLevel("second level", Reporter.CurrentPlace);
		report.report("message in level 2");
		report.report("another message in level 2");
		report.stopLevel();
		report.report("another message in level 1");
		report.stopLevel();
		report.report("message in main report page");

	}

	public void testInternalTest() {
		for (int i = 0; i < 10; i++) {
			report.startReport("testInternalTestExample", null);
			report.step("Step for test: " + (i + 1));
			report.endReport();
		}
	}
	/**
	 * @params.include title,pressOn,expected
	 */
	public void testConfirmTest() {
		if (("yes".equals(pressOn)) || ("no".equals(pressOn))) {
			log.info("inside if where pressOn equals either \"yes\" or \"no\" in testConfirmTest");
			int res = report.showConfirmDialog(title, pressOn, JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			report.step("user reply = " + res);
			assertTrue(res == (int) expected);
		} else if (expected == 0) {
			log.info("value of pressOn is = "+pressOn);
			log.info("inside expected == 0 testConfirmTest" );
			int res = report.showConfirmDialog(title, pressOn, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
			report.step("user reply = " + res);
			assertTrue(res == (int) expected);

		} else {
			pressOn = "no";
			int res = report.showConfirmDialog(title, pressOn, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
			report.step("user reply = " + res);
			assertTrue(res == (int) expected);
		}
	}

	public void testExcle() throws Exception {
		ExcelFile excel = ExcelFile.getInstance("MyExcel.xls", true);
		excel.addHeader(new String[] { "Packet size", "Rate" });
		excel.addRow(new String[] { "3", "4" });
		excel.addRow(new String[] { "3", "4" });
		report.addLink("my excel", "MyExcel.xls");
	}

	public void testAddFileUsingReporterHelper() throws Exception {
		FileUtils.write("MyFile.txt", "Shalom Olam");
		File f = new File("MyFile.txt");
		ReporterHelper.copyFileToReporterAndAddLink(report, f,"myFile");
	}

	public void testAddLinkProperty() throws Exception {
		FileUtils.write("MyFile.txt", "Shalom Olam");
		File f = new File("MyFile.txt");
		ReporterHelper.copyFileToReporterAndAddLinkProperty(report, f, "linkToFile", null);
		ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "linkToOne",null);
	}

	@TestProperties(name = "Simple test for adding different image files to report as link")
	public void testAddLinkToImage() throws Exception {
		/**
		 * Copy Image as Resource to the log folder
		 */
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("regression/generic/images/splash.jpg");
        report.report(report.getCurrentTestFolder()+ System.getProperty("file.separator") + "splash.jpg"); 
		IOUtils.copy(in, new FileOutputStream(new File(report.getCurrentTestFolder()
				+ System.getProperty("file.separator") + "splash.jpg")));

		in = this.getClass().getClassLoader().getResourceAsStream("regression/generic/images/blue.png");
		IOUtils.copy(in, new FileOutputStream(new File(report.getCurrentTestFolder()
				+ System.getProperty("file.separator") + "blue.png")));

		in = this.getClass().getClassLoader().getResourceAsStream("regression/generic/images/classDir.gif");

		IOUtils.copy(in, new FileOutputStream(new File(report.getCurrentTestFolder()
				+ System.getProperty("file.separator") + "classDir.gif")));

		report.addLink("Link to JPG", "splash.jpg");

		report.addLink("Link to PNG", "blue.png");

		report.addLink("Link to GIF", "classDir.gif");
	}

	@TestProperties(name = "Simple test for adding graph to report as link")
	public void testSaveGraph() throws Exception {
		GraphMonitorManager gmm = GraphMonitorManager.getInstance();

		Graph g = gmm.allocateGraph("1", "2");
		Random r = new Random();
		int sec = 0;
		while (sec < 10) {
			g.add("p", sec, r.nextInt(10));
			sec++;
		}

		report.saveFile("my.jpg", g.getImageAsByteArray());

		report.addLink("graph", "my.jpg");
	}

	public void testReportWithException(){
		Exception e1 = new Exception("Cause Exception");
		Exception e2 = new Exception("Main exception",e1);
		report.report("Message with Exception",e2);
	}
}
