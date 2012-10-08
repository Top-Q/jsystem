
package com.aqua.sanity;
import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.FrameworkOptions;

import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

/**
 * Testing Scenario As Test feature
 * 
 * @author Nizan Freedman
 *
 */
public class ScenarioAsTest extends JSysTestCase4UseExistingServer {
	
	private static String sonScenario = "ScenarioAsTest_SON";
	private static String parentScenario = "ScenarioAsTest_PARENT";
	
	public ScenarioAsTest(){
		super();
	}
	
	@Before
	public void createBasicScenarios() throws Exception{
		super.setUp();
		scenarioClient.cleanScenario(sonScenario);
		scenarioClient.addTest("testThatPass", "SimpleTests",3);
		
		scenarioClient.cleanScenario(parentScenario);
		scenarioClient.addTest(sonScenario, "scenario");
	}
	
	/**
	 * Check that adding a failed test to a son scenario and then marking it as negative works good
	 * 
	 * @throws Throwable
	 */
	@Test
	public void checkNegativeScenarioAsTest() throws Throwable{
		if("false".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString())) ||"null".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString()))){
			applicationClient.setJSystemProperty(FrameworkOptions.SCENARIO_AS_TEST, "true");
		}
		
		scenarioClient.openScenario(sonScenario);
		scenarioClient.addTest("testThatFail", "SimpleTests");
		applicationClient.saveScenario();
		
		scenarioClient.openScenario(parentScenario);
		report.step("Mark Son scanerio as Test");
		scenarioClient.markScenarioAsTest(1,true);
		report.step("Mark Son scanerio as Negative");
		scenarioClient.markAsNegative(1, true);
		applicationClient.play();
		applicationClient.waitForExecutionEnd();
		sleep(3000);
		remoteInformationClient.checkNumberOfTestsPass(1);
		reporterClient.initReporters();
	}
	
	/**
	 * Check that adding a test to a marked sub scenario only adds it to it when it is not a TestScenario
	 * 
	 * @throws Throwable
	 */
	@Test
	public void checkAddingTestToScenarioTest() throws Throwable{
		if("false".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString())) ||"null".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString()))){
			applicationClient.setJSystemProperty(FrameworkOptions.SCENARIO_AS_TEST, "true");
		}
		addTestToSonAndAnalyze(false);
		
		report.step("Mark Son scanerio as Test");
		scenarioClient.markScenarioAsTest(1,true);
		applicationClient.saveScenario();
		addTestToSonAndAnalyze(true);
	}
	
	private void addTestToSonAndAnalyze(boolean addToParent) throws Exception{
		report.step("Open Parent scenario, select Son, add a test");
		scenarioClient.openScenario(parentScenario);
		int initialParent = remoteInformationClient.getNumOfRootTestsForScenario(parentScenario);
		int initialSon = remoteInformationClient.getNumOfRootTestsForScenario(sonScenario);
		scenarioClient.selectTestRow(1);
		scenarioClient.addTest("testThatPass", "SimpleTests");
		applicationClient.saveScenario();
		int newParent = remoteInformationClient.getNumOfRootTestsForScenario(parentScenario);
		int newSon = remoteInformationClient.getNumOfRootTestsForScenario(sonScenario);
		analyzer.setTestAgainstObject(newParent);
		if (addToParent){
			report.step("Check that test was added To Parent");
			analyzer.analyze(new NumberCompare(compareOption.EQUAL, initialParent + 1, 0));
		}else{
			report.step("Check that test was Not added To Parent");
			analyzer.analyze(new NumberCompare(compareOption.EQUAL, initialParent, 0));
		}
		
		analyzer.setTestAgainstObject(newSon);
		if (addToParent){
			report.step("Check that test was Not added To Son");
			analyzer.analyze(new NumberCompare(compareOption.EQUAL, initialSon, 0));
		}else{
			report.step("Check that test was added To Son");
			analyzer.analyze(new NumberCompare(compareOption.EQUAL, initialSon + 1, 0));
		}
	}
	
	/**
	 * Test the test map and unmap and that a ScenarioTest is counted as one test
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkMappingAndTestCount() throws Exception{
		if("false".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString())) ||"null".equalsIgnoreCase(remoteInformationClient.getJsystemPropertyValueForKey(FrameworkOptions.SCENARIO_AS_TEST.toString()))){
			applicationClient.setJSystemProperty(FrameworkOptions.SCENARIO_AS_TEST, "true");
		}
		scenarioClient.openScenario(sonScenario);
		int numOfSonTests = remoteInformationClient.getNumOfRootTestsForScenario(sonScenario);
		scenarioClient.unmapTest(1);
		applicationClient.saveScenario();
		numOfSonTests--;
		report.step("Check that all Son Tests except one test were executed");
		playAnalyzeAndInit(numOfSonTests);
		
		scenarioClient.openScenario(parentScenario);
		scenarioClient.addTest("testThatPass", "SimpleTests");
		applicationClient.saveScenario();
		int numOfParentTests = remoteInformationClient.getNumOfRootTestsForScenario(parentScenario);
		report.step("Check that all Parent Tests including Son tests were executed");
		//The '-1' is for the sub scenario that will be included in the root tests count.
		playAnalyzeAndInit(numOfParentTests -1 + numOfSonTests);
		
		scenarioClient.markScenarioAsTest(1, true);
		scenarioClient.unmapTest(1);
		applicationClient.saveScenario();
		report.step("Check that only Parent Tests ran");
		playAnalyzeAndInit(numOfParentTests - 1);
		
		scenarioClient.mapTest(1);
		applicationClient.saveScenario();
		report.step("Check that Parent Tests ran and Son Scenario counted as 1");
		playAnalyzeAndInit(numOfParentTests);
		
		scenarioClient.markScenarioAsTest(1, false);
		
		scenarioClient.unmapTest(1);
		applicationClient.saveScenario();
		report.step("Check that only Parent Tests ran");
		playAnalyzeAndInit(numOfParentTests - 1);
		
		scenarioClient.mapTest(1);
		applicationClient.saveScenario();
		playAnalyzeAndInit(numOfParentTests + numOfSonTests);
	}
	
	private void playAnalyzeAndInit(int expected) throws Exception{
		applicationClient.play();
		applicationClient.waitForExecutionEnd();
		sleep(6000);
		remoteInformationClient.checkNumberOfTestExecuted(expected);
		reporterClient.initReporters();
	}
	
	@Test
	public void simpleInitReportersTest()throws Exception{
		reporterClient.initReporters();
	}
}
