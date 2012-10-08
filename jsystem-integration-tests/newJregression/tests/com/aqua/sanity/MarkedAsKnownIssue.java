package com.aqua.sanity;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class MarkedAsKnownIssue extends JSysTestCase4UseExistingServer {

	private final String rootScenario = "markedAsTestScenario";
	public MarkedAsKnownIssue() {
		super();
	}
	
	/**
	 * creates a clean scenario with 4 tests, 2 that pass and 2 that fails.
	 */
	@Before
	public void setUp()throws Exception{
		super.setUp();
		scenarioClient.cleanScenario(rootScenario);
		scenarioClient.addTest("testThatPass", "SimpleTests",2);
		scenarioClient.addTest("testThatWarns", "SimpleTests",2);
		scenarioClient.addTest("testThatFail", "SimpleTests",2);
		applicationClient.saveScenario();
	}
	
	@Test
	public void markOneFailAsKnownIssue()throws Exception{
		report.step("select a test that is a test that fails, and mark it as know issue");
		scenarioClient.selectTestRow(5);
		scenarioClient.markAsKnownIssue(5, true);
		applicationClient.play(true);
		report.report("check that the expected number of warning tests is 3 in the reports.0.xml file");
		reporterClient.checkNumberOfTestsWarning(3);
		report.report("check that in the General statistics table values represent 3 warnings, 1 fail, and 6 total");
		reporterClient.checkNumberOfTestsTotalFailAndWarningInSummery(6,1,3);
		Map<String, String[]> testsNamesAndResults = new HashMap<String, String[]>();
		testsNamesAndResults.put("SimpleTests.testThatFail", new String[]{"0","1","1"});
		testsNamesAndResults.put("SimpleTests.testThatPass", new String[]{"2","0","0"});
		testsNamesAndResults.put("SimpleTests.testThatWarns", new String[]{"0","0","2"});
		report.report("check that in the Test Statistics table testThatFail will have 1 with fail status and 1 with warning and 0 with pass");
		report.report("check that in the Test Statistics table testThatPass will have 0 with fail status and 0 with warning and 2 with pass");
		report.report("check that in the Test Statistics table testThatWarns will have 0 with fail status and 2 with warning and 0 with pass");
		reporterClient.checkNumberOfTestsPassFailOrWarnInSummary(testsNamesAndResults);
		report.report("check that in the Sanity table the links will show that:");
		report.report("testThatFail ended 1 time with fail status and 1 time with warning while");
		report.report("testThatPass ended with pass twice and testThatWarns ended with warning twice");
		//i expect to find the testThatFail twice in the table. once with the warning status and once with fail.
		reporterClient.checkSummerySanityTable("SimpleTests.testThatFail", new String[]{"warning","fail"},2);
		reporterClient.checkSummerySanityTable("SimpleTests.testThatPass", new String[]{"pass","pass"},2);
		reporterClient.checkSummerySanityTable("SimpleTests.testThatWarns", new String[]{"warning","warning"},2);
		
	}
}
