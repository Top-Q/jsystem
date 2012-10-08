package com.aqua.sanity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.ScenarioUtils;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.fixture.RootFixture;
import jsystem.utils.FileUtils;
import analyzers.HtmlTableValuesAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class HtmlReporterOutputAccuracy extends JSysTestCaseOld {
	public HtmlReporterOutputAccuracy(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	public void setUp() throws Exception{
		super.setUp();
	}
	
	/**
	 * create two scenarios each with two tests, one that pass one that fails
	 * then extract the text from the file to String and parse it to see 
	 * the data is correct.
	 */
	public void testHtmlInfoReturnedFromPressingLogIsCorrect()throws Exception{
		final String runnerDir = "/usr/local/aqua/runner/";
		applicationClient.launch();
		ScenarioUtils.createAndCleanScenario(scenarioClient, "reporterDataChecking");
		scenarioClient.addTest("testShouldPass", "GenericBasic",2);
		scenarioClient.addTest("testFailWithError", "GenericBasic",2);
		applicationClient.play();
		applicationClient.waitForRunEnd();
		reporterClient.pressLogButton();
		File htmlfile = new File(runnerDir+"runnerout/runner/log/current/summary.html");
		String str = FileUtils.read(htmlfile);
		
		//analyze the table columns values
		Map<String, String> columnsNvals = new HashMap<String, String>();
		columnsNvals.put("Number of tests", "4");
		columnsNvals.put("Number of fails", "2");
		columnsNvals.put("Number of warnings", "0");
		analyzer.setTestAgainstObject(columnsNvals);
		analyzer.analyze(new HtmlTableValuesAnalyzer(str));
		
		//analyze the links table to the reports, number of rows found against
		//the expected.
		Pattern pattern = Pattern.compile("a\\s[hH][rR][eE][fF]=\"report[0-9]+.html\"");
		Matcher matcher = pattern.matcher(str);
		int count = 0;
		while(matcher.find()){
			matcher.group(0);
			count++;
		}
		analyzer.setTestAgainstObject(4);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 4, 0));
	}
}



