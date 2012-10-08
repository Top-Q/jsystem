package com.aqua.sanity;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.aqua.analyzers.StringCompareAnalyzer;
import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.jsystemobjects.TestType;

public class TestChangeParamsInSubScenarioInstance extends
		JSysTestCase4UseExistingServer {
	
	public TestChangeParamsInSubScenarioInstance() {
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		super.setUp();
	}
	
	/**
	 * 1. launch remote runner
	 * 2. add a test with a string parameter
	 * 3. set the test parameter in first subscenario to string "StringA"
	 * 4. change the parameter value in second subscenario to "StringB"
	 * 5. check that the parameter value in first subscenario is still "StringA".
	 * 6. remove the test from subscenario
	 * @throws Exception
	 */
	@Test
	public void testChangeParamsInSubScenarioEffectOnSecondInstance()throws Exception{
		applicationClient.launch();
		scenarioClient.cleanScenario("parametersChangeInSubScenarios");
		scenarioClient.cleanScenario("parametersChangeScenario");
		scenarioClient.addTest("parametersChangeInSubScenarios", TestType.SCENARIO.getType(),2);
		//stand on first subscenario row and add test to it.
		scenarioClient.selectTestRow(1);
		scenarioClient.addTest("SimpleTest", "TestSubScenariosParametersChange");
		//stand on the test itself in the first sub scneario
		scenarioClient.setTestParameter(2, "General", "testString", "StringA", false);
		scenarioClient.setTestParameter(4, "General", "testString", "StringB", false);
		Thread.sleep(1000);
		String testParameter1 = scenarioClient.getTestParameter(2, "General", "testString");
		String testParameter2 = scenarioClient.getTestParameter(4, "General", "testString");
		analyzer.setTestAgainstObject(testParameter1);
		analyzer.analyze(new StringCompareAnalyzer(testParameter2,StringCompareAnalyzer.TestOption.UnEqual,false));
		
	}
	
	/**
	 * 1. create a root scenario and a subscenario, under subscenario add a test
	 *    that writes to run.properties the value of the parameter.
	 * 2. set the parameter to string XXX and play
	 * 3. check that parameter in runtime is indeed XXX
	 * 4. add another test of the same type
	 * 5. set the second test parameter to YYY
	 * 6. play and test that the parameter in runtime is indeed YYY
	 * @throws Exception
	 */
	@Test
	public void testChangeTestsOrderInScenarioWithParamsValuesOrder()throws Exception{
		applicationClient.launch();
		scenarioClient.cleanScenario("parametersChangeInSubScenarios");
		scenarioClient.cleanScenario("parametersChangeScenario");
		scenarioClient.addTest("parametersChangeInSubScenarios", TestType.SCENARIO.getType());
		scenarioClient.selectTestRow(1);
		scenarioClient.addTest("writeParameterToRunProperties", "TestSubScenariosParametersChange",2);
		scenarioClient.setTestParameter(2, "General", "testString", "XXX", false);
		scenarioClient.setTestParameter(3, "General", "testString", "YYY", false);
		applicationClient.play(true);

		report.step("test that the first parameter from the first test is XXX and the second is YYY");
		Properties p = getRunProperties();
		String testString1 = p.getProperty("testString1");
		String testString2 = p.getProperty("testString2");
		analyzer.setTestAgainstObject(testString1);
		analyzer.analyze(new StringCompareAnalyzer("XXX"));
		analyzer.setTestAgainstObject(testString2);
		analyzer.analyze(new StringCompareAnalyzer("YYY"));
		
		report.step("open sub scenario and change move first test down");
		scenarioClient.openScenario("parametersChangeInSubScenarios");
		scenarioClient.moveTestDown(1);
		
		report.step("reopen root scenario");
		scenarioClient.openScenario("parametersChangeScenario");
		applicationClient.play(true);

		report.step("test that the first parameter from the first test is YYY and the second is XXX");
		analyzer.setTestAgainstObject(testString1);
		analyzer.analyze(new StringCompareAnalyzer("YYY"));
		analyzer.setTestAgainstObject(testString2);
		analyzer.analyze(new StringCompareAnalyzer("XXX"));
	}
	
	@Test
	public void checkParamValueChangeInDeepScenTestIsDiffInMultiInstancesOfScenInScenTree()throws Exception{
		applicationClient.launch();
		report.step("create a clean scenario parent with a subscenario that has a subscenario itself");
		scenarioClient.cleanScenario("parametersChangeInSubScenarios");
		scenarioClient.cleanScenario("parametersChangeInSubSubScenarios");
		scenarioClient.cleanScenario("parametersChangeScenario");
		report.report("add the first Subscenario to the main scenario");
		scenarioClient.addTest("parametersChangeInSubScenarios", TestType.SCENARIO.getType());
		scenarioClient.selectTestRow(1);
		report.report("add the subSubScenario to the sub scenario");
		scenarioClient.addTest("parametersChangeInSubSubScenarios", TestType.SCENARIO.getType());
		scenarioClient.selectTestRow(2);
		scenarioClient.addTest("writeParameterToRunProperties", "TestSubScenariosParametersChange");
		scenarioClient.cleanScenario("grandParentScenario");
		scenarioClient.addTest("parametersChangeScenario", TestType.SCENARIO.getType(),2);
		report.step("setting first test parameter value to StringA and second test parameter value to StringB");
		scenarioClient.setTestParameter(4, "General", "testString", "StringA", false);
		scenarioClient.setTestParameter(8, "General", "testString", "StringB", false);
		applicationClient.play();
		report.step("check that values axpected are values in run time");
		Properties p = getRunProperties();
		String stringA = p.getProperty("testString1");
		String stringB = p.getProperty("testString2");
		analyzer.setTestAgainstObject(stringA);
		analyzer.analyze(new StringCompareAnalyzer("StringA"),true);
		analyzer.setTestAgainstObject(stringB);
		analyzer.analyze(new StringCompareAnalyzer("StringB"),true);
		System.out.println("lll");
	}
}
