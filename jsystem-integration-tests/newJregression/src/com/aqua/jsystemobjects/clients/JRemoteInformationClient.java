package com.aqua.jsystemobjects.clients;

import java.io.File;
import java.io.FileNotFoundException;

import jsystem.utils.FileUtils;

import org.jsystem.objects.xmlrpc.XmlRpcHelper;

import com.aqua.analyzers.XPathNumberOfElements;
import com.aqua.jsystemobjects.handlers.JRemoteInformationHandler;

public class JRemoteInformationClient extends BaseClient {

	public JRemoteInformationClient(XmlRpcHelper connectionHandler) {
		super(connectionHandler);
	}

	@Override
	protected String getHandlerName() {
		return JRemoteInformationHandler.class.getSimpleName();
	}
	
	public String getJsystemPropertyValueForKey(String key) throws Exception{
		return (String)handleCommand("return the value associated with this key in jsystem.properties", "getJsystemPropertyValueForKey", key);
	}
	
	/**
	 * get the number of ROOT test for given Scenario. Notice that this includes also sub scenarios.
	 * 
	 * @param scenarioName	the scenario to check 
	 * @return	the amount of root tests in the scenario
	 * @throws Exception
	 */
	public int getNumOfRootTestsForScenario(String scenarioName) throws Exception{
		return (Integer)handleCommand("Get number of ROOT tests for scenario "+scenarioName, "getNumOfRootTestsForScenario", scenarioName);
	}

	/**
	 * get path for report dir
	 */
	public String getReportXmlFile() throws Exception {
		String dir = (String) handleCommand("get report.0.xml path", "getReportDir");
		return dir;
	}
	
	public void checkNumberOfTestsPass(int numberOfTests) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkTestPath file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test[@status=\"true\"]", numberOfTests),false,false);
	}
	
	public void checkNumberOfTestExecuted(int numberOfTests) throws Exception {
		File reportXml = new File(getReportXmlFile());
		if (!reportXml.exists()) {
			report.report("checkNumberOfTestExecuted file, reporter xml file: " + reportXml.getAbsolutePath() + ", wasn't found", false);
			throw new FileNotFoundException(reportXml.getAbsolutePath());
		}
		setTestAgainstObject(FileUtils.readDocumentFromFile(reportXml));
		analyze(new XPathNumberOfElements("/reports/test", numberOfTests),false, false);
	}
}
