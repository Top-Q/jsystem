package com.aqua.jsystemobjects.clients;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.utils.FileUtils;

import org.jsystem.objects.xmlrpc.XmlRpcHelper;
import org.junit.Assert;

import com.aqua.analyzers.HtmlTableValuesAnalyzer;
import com.aqua.analyzers.StringCompareAnalyzer;
import com.aqua.analyzers.XPathNumberOfElements;
import com.aqua.jsystemobjects.handlers.JReporterHandler;

public class JReporterClient extends BaseClient {

	
	public JReporterClient(XmlRpcHelper connectionHandler) {
		super(connectionHandler);
	}

	/**
	 * if reports button is enabled will return true, else will return false.
	 */
	public boolean isReportsButtonEnabled() throws Exception{
		return (Boolean)handleCommand("is the reports button enabled?", "isReportsButtonEnabled");
	}
	
	/**
	 * changes the db.properties setting at a specific key using the gui.
	 * @param properyKey
	 * @param PropertyValue
	 * @throws Exception
	 */
	public void changeDBpropertyWithGui(String properyKey, String PropertyValue) throws Exception{
		handleCommand("change db properties by gui", "changeDBpropertyWithGui", properyKey, PropertyValue);
	}

	public int initReporters() throws Exception{
		return (Integer)handleCommand("init reporters", "initReporters");
	}
	
	@Override
	protected String getHandlerName() {
		return JReporterHandler.class.getSimpleName();
	}
	
	public void checkNumberOfTestsPass(int numberOfTests) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test[@status=\"true\"]", numberOfTests));
	}
	
	public void checkNumberOfTestsWarning(int numberOfTests)throws Exception{
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test[@status=\"warning\"]", numberOfTests));
	}
	
	/**
	 * uses the checkNumberOfTestsPassFailOrWarnInSummary function by passing to it a map of values
	 * where the keys are the "General Statistics" table first column text, 
	 * and the values are the second column tests.
	 * @param numberOfTests
	 * @param numberOfFails
	 * @param numberOfWarnings
	 * @throws Exception
	 */
	public void checkNumberOfTestsTotalFailAndWarningInSummery(int numberOfTests,int numberOfFails, int numberOfWarnings)throws Exception{
		Map<String, String[]> columnsNvals = new HashMap<String, String[]>();
		columnsNvals.put("Number of tests", new String[]{new Integer(numberOfTests).toString()});
		columnsNvals.put("Number of fails", new String[]{new Integer(numberOfFails).toString()});
		columnsNvals.put("Number of warnings", new String[]{new Integer(numberOfWarnings).toString()});
		checkNumberOfTestsPassFailOrWarnInSummary(columnsNvals);
	}
	
	/**
	 * will table and argument made of a key value pair where key is a String and the key is and array of Strings.
	 * each element of a table might have 1 or more related columns of values to check.
	 * the test will read the html file to a string and use the HtmlTAbleValuesAnalyzer to match
	 * each table key with it's relevant expected values.
	 * @param tableColumnValuesAndExpectedResults
	 * @throws Exception
	 */
	public void checkNumberOfTestsPassFailOrWarnInSummary(Map<String, String[]> tableColumnValuesAndExpectedResults)throws Exception{
		String htmlFileContent = FileUtils.read(new File(getReportsDir()+"summary.html"));
		setTestAgainstObject(tableColumnValuesAndExpectedResults);
		analyze(new HtmlTableValuesAnalyzer(htmlFileContent));
	}
	
	/**
	 * this test will check that the given testName in the Sanity table of the Summary report
	 * will have the expected result, and that it will appear in that table with that result the correct number
	 * of times it was run under the scenario.
	 * @param testName
	 * @param expectedValue
	 * @param expectedNumberOfRecords
	 * @throws Exception
	 */
	public void checkSummerySanityTable(String testName, String[] expectedValue,int expectedNumberOfRecords)throws Exception{
		int counter = 0;
		String htmlFileContent = FileUtils.read(new File(getReportsDir()+"summary.html"));
		//analyze the links table to the reports, number of rows found against
		//the expected.
		Pattern pattern = Pattern.compile("<tr>[\\s\n]*<td>[\\s\n]*<\\s*a\\s+[hH][rR][eE][fF]=\"report[0-9]+.html\">\\s*[0-9]+\\s+"+testName+"</a>[\\s\n]*</td>[\\s\n]*<td[^>]*><?[^>]*>?([a-zA-Z]+)<?[^>]+>[\\s\n]*</td>[\\s\n]*</tr>");
		Matcher matcher = pattern.matcher(htmlFileContent);
		while(matcher.find()){
			//extract the value for the test testName from the table and match it with the expected value.
//			String matchValue = matcher.group(0).split("<[^>]+>")[4];
			String matchValue = matcher.group(1);
			setTestAgainstObject(expectedValue[counter]);
			analyze(new StringCompareAnalyzer(matchValue,StringCompareAnalyzer.TestOption.Equals, true));
			counter++;
		}
		//test that the number of times that test appears in the links table is correct.
		setTestAgainstObject(counter);
		analyze(new NumberCompare(compareOption.EQUAL, expectedNumberOfRecords,0));
		//if no match was found that test fails.
		if(counter == 0){
			Assert.assertFalse("match was not found between \""+testName+"\" and expected value \""+expectedValue+"\"",true);
		}
	}
	
	/**
	 * get path for report dir
	 */
	public String getReportXmlFile() throws Exception {
		return (String) handleCommand("get report.0.xml path", "getReportDir");
	}
	
	public String getReportsDir()throws Exception{
		return (String)handleCommand("get the remote reports dir", "getReportsDir");
	}
}
