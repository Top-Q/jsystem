package com.aqua.sanity.newRgressionTests;

import org.junit.Test;

import analyzers.BooleanAnalyzer;

import com.aqua.general.JSysTestCase4UseExistingServer;

public class ReporterFunctionality extends JSysTestCase4UseExistingServer {
	private String notAppPort = "7654";
	public ReporterFunctionality() {
		super();
	}
	
	/**
	 * after runner is up, rewrite properties file db.properties by changing the 
	 * port number to notAppPort so that it can't connect to reports application 
	 * for database connection error.
	 * 
	 * the tests assumes that db.properties hold correct values to publish
	 * to begin with.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkReportsButtonDiabledEnabledAccordingToDbPropertiesFile() throws Exception{
//		Thread.sleep(40000);
		String workingPort = applicationClient.getDBProperty("browser.port");
		applicationClient.changeDbProperty("browser.port", notAppPort);
		applicationClient.refresh();
		analyzer.setTestAgainstObject(reporterClient.isReportsButtonEnabled());
		analyzer.analyze(new BooleanAnalyzer(false));
		reporterClient.changeDBpropertyWithGui("browser.port", workingPort);
		applicationClient.refresh();
		analyzer.setTestAgainstObject(reporterClient.isReportsButtonEnabled());
		analyzer.analyze(new BooleanAnalyzer(true));
		System.out.println("debug stop");
	}
}
