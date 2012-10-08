package com.aqua.sanity.analyzers;

import utils.ScenarioUtils;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * This class test the analysis abilities of the jsystem.
 * it load from the jsystemRegressionBaseTests a class with the name of SimpleAnalyzerTest 
 * from this test it loads 4 analysis tests :
 * 
 * 1.testPassAnalyze
 * 2.testFailedAnalyze
 * 3.testCheckThrowTrue
 * 4.testCheckThrowFalse
 * 
 * in the end of the 4 test it checks the number of tests that expected to pass
 * @author Guy levi
 */
public class AnalysisTest extends JSysTestCaseOld {
	
	public AnalysisTest(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * 1. Create a scenario
	 * 2. add few tests to the scenario
	 * 3. run the scenario
	 * 4. check if the expect test are running
	 * @throws Exception
	 */
	@TestProperties(name = "test analysis functionlity")
	public void testAnalysis() throws Exception{
		jsystem.launch();
		jsystem.changeSut("analysisSut.xml");
		report.step("create a scenario with few steps");
		ScenarioUtils.createAndCleanScenario(jsystem, "AnalysisScenario");
		report.report("add test 1");
		jsystem.addTest("testPassAnalyze", "SimpleAnalyzerTest", true);
		report.report("add test 2");
		jsystem.addTest("testFailedAnalyze", "SimpleAnalyzerTest", true);
		report.report("add test 3");
		jsystem.addTest("testCheckThrowTrue", "SimpleAnalyzerTest", true);
		report.report("add test 4");
		jsystem.addTest("testCheckThrowFalse", "SimpleAnalyzerTest", true);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		

		report.step("check that the restored scenario was executed");
		//check that 3 from 4 test are pass
		jsystem.checkNumberOfTestsPass(3);
		//check if the 4 test report the problem of it failure. 
		jsystem.checkXmlTestAttribute(4, "failCause", "The texts are not equals");

	}
}
