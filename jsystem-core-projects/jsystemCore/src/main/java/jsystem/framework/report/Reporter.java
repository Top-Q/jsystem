/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.IOException;
import java.util.List;

import jsystem.framework.system.SystemObjectImpl;
import junit.framework.SystemTestCase;

/**
 * The Reporter is use to get the reports from the tests or APIs and send them
 * to the TestReporter(s).
 * <p>
 * An instance of the <code>Reporter</code> implementation can be found in 
 * <code>SystemTestCase</code> to be used by the test, or in <code>SystemObjectImpl</code>
 * to be used by the system object. 
 * 
 * @see SystemTestCase
 * @see SystemObjectImpl
 * @author Guy Arieli
 */
public interface Reporter extends InteractiveReporter{
	
	public enum EnumBadge {
		RESULT("badge-dark", "#result"),
		DEBUG("badge-secondary", "#debug"),
		JIRA("badge-primary", ""), // text will be converted to link
		INFO("badge-info", "#info"),
		BUG("badge-danger", "#bug"),
		WORKAROUND("badge-warning", "#workaround"),;
		// LIGHT("badge-light", ""),
		// SUCCESS("badge-success", ""),
	    ;

		private String keyword;
		public String text;

		EnumBadge(String keyword, String text) {
			this.keyword = keyword;
			this.text = text;
		}

		/**
		 * In the case of a Jira Badge, the text will be converted to the link
		 * @param text
		 * @return
		 */
		public EnumBadge setText(String text) {
			this.text = text;
			return this;
		}

		public String get() {
			return keyword + "<text>" + text + "<text>";
		}

	}

	
	public enum EnumReportLevel{
		CurrentPlace(0),
		MainFrame(1);
		
		private int value;
		
		EnumReportLevel(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
	}
	
	public static enum ReportAttribute{
		BOLD, LINK, HTML, STEP;
	}
	
	/**
	 * Pass report status
	 */
	public static final int PASS = 0;

	/**
	 * Warning report status
	 */
	public static final int WARNING = 2;
	/**
	 * Fail report status
	 */
	public static final int FAIL = 1;



	/**
	 * use for LevelHtmlReporter
	 */
	public static final int CurrentPlace = 0;

	public static final int MainFrame = 1;

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param message
	 *            Report message.
	 * @param status
	 *            report status.
	 * @param bold
	 *            bold report.
	 */
	void report(String title, String message, boolean status, boolean bold);

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param message
	 *            Report message.
	 * @param status
	 *            report status.
	 */
	void report(String title, String message, boolean status);

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param status
	 *            report status.
	 */
	void report(String title, boolean status);

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 */
	void report(String title);

	/**
	 * Post a BOLD step report<br>
	 * <B>Note: The step report is a special report that is used later on 
	 *          for publishing to DB and Managerial analysis.<br>
	 *          use it only for Actual test steps.<br>
	 *          for regular bold messages use: <I>report.report("Your title",ReportAttribute.BOLD);</I></B>
	 * 
	 * @param stepDescription
	 *            Step description.
	 */
	void step(String stepDescription);

	/**
	 * Post a test failure report.
	 * 
	 * @param title
	 *            Test title.
	 * @param t
	 *            The exception that cause the report.
	 */
	void report(String title, Throwable t);

	/**
	 * Set the reports to be silent. The silent status is false by default and
	 * set to false every new test.
	 * 
	 * @param status
	 *            if set to true no reports will be seen.
	 */
	void setSilent(boolean status);
	
	/**
	 * returns if the reports is silent. The silent status is false by default and
	 * set to false every new test.
	 * 
	 * @return status
	 *            if true no reports will be seen.
	 */
	boolean isSilent();
	
	/**
	 * Set the reports to print\not print TimeStamps.
	 * by default, TimeStamp will be added to each report.
	 * set to true every new test.
	 * 
	 * @param enable
	 *            if set to True no reports will be seen.
	 */
	void setTimeStamp(boolean enable);

	/**
	 * Report a HTML content. The message of the report as asumed to be in HTML
	 * format.
	 * 
	 * @param title
	 *            report title.
	 * @param html
	 *            report message in HTML format.
	 * @param status
	 *            report status.
	 */
	public void reportHtml(String title, String html, boolean status);

	/**
	 * Add link
	 * 
	 * @param title
	 *            the name (title) of the link
	 * @param link
	 *            the link url
	 */
	public void addLink(String title, String link);

	/**
	 * Save file into the reporter (every reporter implementation will handle it
	 * differently).
	 * 
	 * @param fileName
	 *            the file name.
	 * @param content
	 *            the content of the file.
	 */
	public void saveFile(String fileName, byte[] content);

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param message
	 *            Report message.
	 * @param status
	 *            report status can be PASS, FAIL or WARNING.
	 * @param bold
	 *            bold report.
	 */
	public void report(String title, String message, int status, boolean bold);

	/**
	 * Set an XML test export data (will be used by the reporting application).
	 * 
	 * @param data
	 *            the XML data.
	 */
	public void setData(String data);


	/**
	 * start new level
	 * 
	 * @param level
	 *            level name
	 * @param place
	 *            may be Reporter.MainFrame or Reporter.CurrentPlace
	 * @throws IOException
	 */
	public void startLevel(String level, int place) throws IOException;
	
	
	/**
	 * start new level in Reporter.currentPlace
	 * 
	 * @param level
	 *            level name
	 * @throws IOException
	 */
	public void startLevel(String level) throws IOException;
	
	/**
	 * start new level
	 * 
	 * @param level
	 *            level name
	 * @param place
	 *            may be Reporter.MainFrame or Reporter.CurrentPlace
	 * @throws IOException
	 */
	public void startLevel(String level, EnumReportLevel place) throws IOException;
	
	
	

	/**
	 * Close current level and go back to previous level
	 * 
	 * @throws IOException
	 */
	public void stopLevel() throws IOException;
	
	/**
	 * Close all opened levels and return to Main Level
	 * 
	 * @throws IOException
	 */
	public void closeAllLevels() throws IOException;

	/**
	 * Start an internal test. An internal test is a test that is defined by the
	 * main test. It will be viewed by the reporting system as an indipendent
	 * test.
	 * 
	 * @param methodName
	 *            The internal test method name to be used
	 * @param parameters
	 *            The parameters of the internal test (spaced delimiter list) or
	 *            null if no parameters avilable.
	 * 
	 */
	public void startReport(String methodName, String parameters);

	/**
	 * Start an internal test. An internal test is a test that is defined by the
	 * main test. It will be viewed by the reporting system as an indipendent
	 * test.
	 * 
	 * @param methodName
	 *            The internal test method name to be used
	 * @param parameters
	 *            The parameters of the internal test (spaced delimiter list) or
	 *            null if no parameters available.
	 * @param classDoc
	 *            The class documentation of the internal test as should be
	 *            viewed in the reports.
	 * @param testDoc
	 *            The test documentation of the internal test as should be
	 *            viewed in the reports.
	 */
	public void startReport(String methodName, String parameters,
			String classDoc, String testDoc);

	/**
	 * End the internal test.
	 * 
	 */
	public void endReport();

	/**
	 * End the internal test
	 * 
	 * @param steps
	 *            the steps of the test as should be seen in the reporting
	 *            system.
	 * @param failCause
	 *            the fail cause of the internal test as should be seen in the
	 *            reporting system.
	 */
	public void endReport(String steps, String failCause);

	/**
	 * 
	 * @return true if the fail to pass flag is turn on.
	 */
	public boolean isFailToPass();

	/**
	 * Set the fail to pass status flag. If set to true all the reports that
	 * marked as fail or warning will be change to pass.
	 * 
	 * @param failToPass
	 *            the fail to pass status
	 */
	public void setFailToPass(boolean failToPass);

	/**
	 * 
	 * @return true if the fail to warning flag is turn on.
	 */
	public boolean isFailToWarning();

	/**
	 * Set the fail to warning status flag. If set to true all the reports that
	 * marked as fail will be change to warning.
	 * 
	 * @param failToWarning
	 *            the fail to warning status
	 */
	public void setFailToWarning(boolean failToWarning);

	/**
	 * Add a report to all the register reporters
	 * 
	 * @param title
	 *            the report title.
	 * @param message
	 *            the report message
	 * @param status
	 *            the report status. {@link #PASS}, {@link #FAIL},
	 *            {@link #WARNING}
	 * @param bold
	 *            if set to true the report will be seen bold.
	 * @param html
	 *            if set to true the report message will considered html formated.
	 * @param step
	 *            if set to true the report will be considered as step.
	 * @param link
	 *            if set to true the report will be considered as link.
	 */
	public void report(String title, String message, int status, boolean bold,
			boolean html, boolean step, boolean link);

	/**
	 * Add a report to all the register reporters
	 * 
	 * @param title
	 *            the report title.
	 * @param message
	 *            the report message
	 * @param status
	 *            the report status. {@link #PASS}, {@link #FAIL},
	 *            {@link #WARNING}
	 * @param bold
	 *            if set to true the report will be seen bold.
	 * @param html
	 *            if set to true the report message will considered html formated.
	 * @param step
	 *            if set to true the report will be considered as step.
	 * @param link
	 *            if set to true the report will be considered as link.
	 * @param time
	 *            the time of the report.
	 */
	public void report(String title, String message, int status, boolean bold,
			boolean html, boolean step, boolean link, long time);

	/**
	 * Get the current test folder. For example for the first test it will be
	 * /log/current/test1 It should be used for file attachment to the current
	 * test log.
	 * 
	 * @return the current directory from the user.dir
	 */
	public String getCurrentTestFolder();
	
	/**
	 * Get the last report file created
     * for example: report17.html
	 * 
	 * @return the latest report file name
	 */
	public String getLastReportFile();

	/**
	 * Start to buffer all the reports. The reporters will not recieve any
	 * reporting events. The reports are saved in ReportElement object. In any
	 * case new test will start were buffering is off.
	 */
	public void startBufferingReports();
	/**
	 * Start to buffer all the reports. The reporters will not recieve any
	 * reporting events. The reports are saved in ReportElement object. In any
	 * case new test will start were buffering is off.
	 * @param printBufferdReportsInRunTime
	 */
	public void startBufferingReports(boolean printBufferdReportsInRunTime);

	/**
	 * Stop buffering reports.
	 * 
	 */
	public void stopBufferingReports();

	/**
	 * Get all the reports that were buffered.
	 * 
	 * @return list with all the ReportElement's
	 */
	public List<ReportElement> getReportsBuffer();

	/**
	 * Clear the reports buffer.
	 * 
	 */
	public void clearReportsBuffer();

	/**
	 * report the information in the report element
	 * 
	 * @param report
	 *            a report element
	 */
	public void report(ReportElement report);

	/**
	 * add a property. The properties can be seen in the reporting system
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 */
	public void addProperty(String key, String value);

	/**
	 * Associates a property - value with a container in the scenario hierarchy tree.
	 * Property later can be viewed in the HTML reporter later.
	 * 
	 * @param ancestorLevel - the level of test's ancestor in to which property will be associated.
	 *                        if the value is 0, the property will be associated with test's parent (either a scenario or a flow control)
	 *                        if the value is 1 , the property will be associated with test grandparent etc'.
	 *                        If the value supplied is greater then the number of levels in the scenario tree, property will
	 *                        be associated with root scenario. 
	 *                   
	 * @param key  - property key
	 * @param value - property value
	 */
	public void setContainerProperties(int ancestorLevel,String key,String value);

	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param status
	 *            report status can be PASS, FAIL or WARNING.
	 */
	public void report(String title, int status);

	
	/**
	 * Post a test report
	 * 
	 * @param title
	 *            Report title.
	 * @param message
	 *            Report message.
	 * @param status
	 *            report status can be PASS, FAIL or WARNING.
	 */
	public void report(String title, String message, int status);
	

	/**
	 * 
	 * @param title - Report title
	 * @param attribute - Can hold one of the following values: BOLD, LINK, HTML, STEP
	 */
	public void report(String title, ReportAttribute attribute);
	
	
	/**
	 * 
	 * @param title - Report title
	 * @param message - Report message
	 * @param attribute - Can hold one of the following values: BOLD, LINK, HTML, STEP
	 */
	public void report(String title, String message, ReportAttribute attribute);
    
	/**
	 * 
	 * @param title - Report title
	 * @param message - Report message
	 * @param status - Pass, Fail, Warning
	 * @param attribute - Can hold one of the following values: BOLD, LINK, HTML, STEP
	 */
	public void report(String title, String message, int status, ReportAttribute attribute);
	
	
	//***** Badge Methods *****//
	public void jira(String title, String jiraId);

	public void info(String title, String info);

	public void info(String title);

	public void workaround(String title);

	public void debug(String title);

	public void bug(String title);

	public void result(String title, int status);

	public void result(String title);

	public void result(String title, boolean status);

	public void result(String title, ReportAttribute reportAttribute);
	
}
