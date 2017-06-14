/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jsystem.extensions.report.html.HtmlTestReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ExtendTestReporter;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.sut.SutListener;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This reporter is used to create xml structured report. This report can be
 * read and publish by the publisher.
 * 
 * @author guy.arieli
 * 
 */
public class XmlReporter implements ExtendTestReporter, ExtendTestListener, SutListener, ScenarioListener {
	
	static Logger log = Logger.getLogger(XmlReporter.class.getName());
	
	public static final String DESCRIPTION = "Description";

	public static final String VERSION = "Version";

	public static final String BUILD = "Build";

	public static final String INIT_REPORT = "init_report";
	
	public static final String ACTION_TYPE = "action_type";
	
	public static final String SCENARIO_NAME = "Scenario";
	
	public static final String STATION = "Station";
	
	public static final String SETUP = "Setup";
	
	public static final String UPLOAD_FILES="UploadFiles";

	public static final String USER = "User";

	public static final String START_TIME = "startTime";
	
	public static final String EXECUTION_PROPERTIES = "properties";

	Document doc;

	DocumentBuilder db;

	// HashMap packages;

	File reportFile;

	Reader reader;

	int fileMaxSize = 50000000;

	int fileCount = -1;

	int lastTestStatus = Reporter.PASS;

	private TestInfo lastTestInfo;

	public XmlReporter() {
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.log(Level.WARNING, "problem creating document builder");
		}
		start();
	}

	public void initReporterManager() throws IOException {
	}

	public boolean asUI() {
		return false;
	}

	public synchronized void report(String title, String message, boolean isPass, boolean bold) {
	}

	public String getName() {
		return "XML reporter";
	}

	public void addError(Test test, Throwable t) {
		lastTestStatus = Reporter.FAIL;
	}

	public void addFailure(Test test, AssertionFailedError t) {
		lastTestStatus = Reporter.FAIL;
	}

	public synchronized void endTest(Test test) {
		if (lastTestInfo.isHiddenInHTML && lastTestStatus == Reporter.PASS) {
			reader.removeLastTest();
			return;
		}
		reader.endTest(test, lastTestStatus);
		lastTestStatus = Reporter.PASS;
		if (reportFile.length() > fileMaxSize) {
			start();
		}
	}

	public void startTest(Test test) {

	}

	public void startTest(TestInfo testInfo) {
		lastTestStatus = Reporter.PASS;
		lastTestInfo = testInfo;
		reader.startTest(testInfo);
		sutChanged(SutFactory.getInstance().getSutInstance().getSetupName());
	}

	protected Element getReportElement(String title, String message, String isPass, boolean bold, boolean html,
			boolean link) {
		Element report = doc.createElement("step");
		report.setAttribute("name", title);
		if (message != null) {
			report.setAttribute("message", String.valueOf(message));
		}
		report.setAttribute("status", isPass);
		report.setAttribute("bold", (new Boolean(bold)).toString());
		report.setAttribute("html", (new Boolean(html)).toString());
		report.setAttribute("link", (new Boolean(link)).toString());
		report.setAttribute("time", Long.toString(System.currentTimeMillis()));
		return report;
	}

	private void start() {
		reportFile = getFileName();
		doc = db.newDocument();
		try {
			reader = new Reader(reportFile, doc);
		} catch (Exception e) {
			log.log(Level.SEVERE, "creating a Reader failed");
			return;
		}
	}

	public void init() {
		// XML reporter takes some of it's data from the
		// run properties. Deleting run properties when reporter is initialized.
		//If this will be removed the scenario name and other parameters will not be published
		RunProperties.getInstance().resetRunProperties();
		fileCount = -1;
		start();
	}

	protected File getFileName() {
		String reportDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER);
		if (reportDir == null) {
			reportDir = "log";
		}
		File current = new File(reportDir, "current");
		fileCount++;
		return new File(current.getAbsolutePath(), "reports." + fileCount + ".xml");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jsystem.framework.report.ExtendTestReporter#saveFile(java.lang.String,
	 * byte[])
	 */
	public void saveFile(String fileName, byte[] content) {
		Element report = doc.createElement("save");
		report.setAttribute("fileName", fileName);
		report.appendChild(doc.createTextNode(StringUtils.bytesToString(content)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#report(java.lang.String,
	 * java.lang.String, int, boolean)
	 */
	public synchronized void report(String title, String message, int status, boolean bold, boolean html, boolean link) {
		switch (status) {
		case Reporter.FAIL:
			lastTestStatus = Reporter.FAIL;
			break;
		case Reporter.PASS:
			break;
		default:
			if (lastTestStatus == Reporter.PASS) {
				lastTestStatus = Reporter.WARNING;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#startSection()
	 */
	public void startSection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#endSection()
	 */
	public void endSection() {
	}

	public static void generate(File tempDir, File[] xmls) throws Exception {
		if (tempDir.exists()) {
			FileUtils.deltree(tempDir);
		} else {
			tempDir.mkdirs();
		}
		HtmlTestReporter reporter = new HtmlTestReporter(tempDir.getPath(), true);
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		for (int i = 0; i < xmls.length; i++) {
			System.out.println("Load file: " + xmls[i].getName());
			XMLReader reader = saxParser.getXMLReader();
			reader.setContentHandler(new ReportCreator(reporter, tempDir));
			reader.parse(new InputSource(xmls[i].getAbsolutePath()));
		}
		reporter = null;

	}

	public void setData(String data) {

	}

	public void addWarning(Test test) {
	}

	public void sutChanged(String sutName) {
		if (reader != null) {
			reader.setSut(sutName);
		}
	}

	public void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		if (reader != null && changeType.equals(ScenarioChangeType.CURRENT)) {
			reader.setScenario(current.getName());
		}
	}

	public void scenarioDirectoryChanged(File directory) {

	}

	public void endRun() {
	}

	final public static void main(final String[] args) throws Exception {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(new ReportCreator(new HtmlTestReporter("c:\\xxx", true), new File("c:\\xxx")));
		reader.parse(new InputSource("c:\\reports.0.xml"));
	}

	public void addProperty(String key, String value) {
		if ((value != null) && !value.isEmpty()) {
			reader.addProperty(key, value);
		}
	}

	public void readElements() {
		reader.readElements();
	}

	public void report(String title, String message, int status, boolean bold) {
		report(title, message, status == Reporter.PASS, bold);

	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startContainer(JTestContainer container) {
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues, Parameter[] newValues) {
		// TODO Auto-generated method stub

	}

	public void flush() throws Exception {
		reader.readElements();
	}

	@Override
	public void setContainerProperties(int property, String key, String value) {
		// Ignore property
		reader.addExecutionProperty(key, value);
	}
}

/** an example sink for content events. It simply prints what it sees. */
class ReportCreator extends DefaultHandler implements ContentHandler {
	HtmlTestReporter reporter;

	String currentPackage = null;

	File tmpDir;

	FileOutputStream writer = null;

	long startTime = 0;

	long endTime = 0;

	String[] name = null;

	char lastChar;

	boolean isCharLeft = false;

	public ReportCreator(HtmlTestReporter reporter, File tmpDir) {
		this.reporter = reporter;
		this.tmpDir = tmpDir;
	}

	public void startElement(final String namespace, final String localname, final String type,
			final Attributes attributes) throws SAXException {

		XmlReporter.log.log(Level.INFO, StringUtils.getStackTrace(Thread.currentThread()));

		if (type.equals("package")) {
			currentPackage = attributes.getValue("name");
		} else if (type.equals("test")) {
			String params = attributes.getValue("params");
			int count = 1;
			String scount = attributes.getValue("count");
			String tname = attributes.getValue("name");
			if (scount != null) {
				try {
					count = Integer.parseInt(scount);
				} catch (Throwable t) {
					XmlReporter.log.log(Level.WARNING, "Fail to get test count for test: " + tname);
				}
			}
			name = tname.split("\\.");
			TestInfo ti = new TestInfo();
			ti.className = currentPackage + "." + name[0];
			ti.methodName = name[1];
			ti.meaningfulName = null;
			ti.comment = null;
			ti.parameters = params;
			ti.count = count;
			ti.fullUuid = "";
			reporter.startTest(ti);

			if (attributes.getValue("status").equals("false")) {
				reporter.addError(null, null);
			}

			String testMessage = attributes.getValue("message");
			if (testMessage != null && testMessage.equals("")) {
				testMessage = null;
			}
			if (testMessage != null) {
				reporter.report(testMessage, null, true, true, false);
			}

			startTime = Long.parseLong(attributes.getValue("startTime"));

			// /// temporary patch for fixing runner halt problem //////
			endTime = 1111111111111L;

			if (attributes.getValue("endTime") != "") {
				try {
					endTime = Long.parseLong(attributes.getValue("endTime"));
				} catch (Throwable ignore) {
				}
			}

			// //////////////////////////

		} else if (type.equals("step")) {
			String title = attributes.getValue("name");
			String message = attributes.getValue("message");
			if (message != null && message.equals("")) {
				message = null;
			}
			String sStatus = attributes.getValue("status");
			boolean bold = attributes.getValue("bold").toLowerCase().equals("true");
			boolean html = attributes.getValue("html").toLowerCase().equals("true");
			boolean link = attributes.getValue("link").toLowerCase().equals("true");
			if (link) {
				reporter.report(title, message, 0, false, false, true);
			} else if (html) {
				int stat = Reporter.FAIL;
				if (sStatus.toLowerCase().equals("true")) {
					stat = Reporter.PASS;
				}
				reporter.report(title, message, stat, false, true, false);
			} else {
				int status;
				if (sStatus.toLowerCase().equals("true")) {
					status = Reporter.PASS;
				} else if (sStatus.toLowerCase().equals("false")) {
					status = Reporter.FAIL;
				} else {
					status = Reporter.WARNING;
				}
				reporter.report(title, message, status, bold, false, false);
			}

		} else if (type.equals("section")) {
			if (attributes.getValue("start").equals("true")) {
				reporter.startSection();
			} else {
				reporter.endSection();
			}

		} else if (type.equals("save")) {
			String fileName = attributes.getValue("fileName");
			File f = new File(tmpDir, "current");
			File file = new File(f, fileName);
			try {

				writer = new FileOutputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void endElement(final String namespace, final String localname, final String type) throws SAXException {
		if (type.equals("save")) {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writer = null;
		} else if (type.equals("test")) {
			long runningTime = endTime - startTime;
			if (runningTime < 0) {
				runningTime = 0;
			}
			reporter.endTest(currentPackage, name[0], name[1], runningTime);

		}
	}

	public void characters(final char[] ch, int start, int len) {
		if (ch != null && ch.length > 0) {
			if (writer != null) {
				int i = 0;
				try {
					if (isCharLeft) {
						writer.write((char) (getCharVal(lastChar) * 16 + getCharVal(ch[start])));
						isCharLeft = false;
						start++;
						len--;
					}
					if (len % 2 != 0) {
						lastChar = ch[start + len - 1];
						len--;
						isCharLeft = true;
					}
					for (i = 0; i < len; i = i + 2) {
						writer.write((char) (getCharVal(ch[start + i]) * 16 + getCharVal(ch[start + i + 1])));
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private int getCharVal(char c1) {
		switch (c1) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':
			return 10;
		case 'b':
			return 11;
		case 'c':
			return 12;
		case 'd':
			return 13;
		case 'e':
			return 14;
		case 'f':
			return 15;
		default:
			return -1;

		}
	}
}
