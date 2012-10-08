package com.aqua.sanity;

import jsystem.extensions.report.html.Report;
import jsystem.framework.TestProperties;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.jsystemobjects.TestType;

public class ScenarioResetToDefault  extends JSysTestCase4UseExistingServer{
	
	private static String PRAM_NAME = "str";
	private static String TAB_NAME = "General";
	private static final String SCENARIO_NAME = "inerScenario";
	private static final String SCENARIO_NAME_2 = "outScenario";
	private static final String TEST_NAME = "test1";
	private static final String BEFORE_STRING = "Before";
	private static final String AFTER_STRING = "After";
	
	@Before
	public void setUp()throws Exception{
		super.setUp();
		report.step("Creating "+SCENARIO_NAME);
		scenarioClient.createScenario(SCENARIO_NAME);
		scenarioClient.addTest(TEST_NAME, "SimpleTests", 2);
		applicationClient.saveScenario();
	
		report.step("Creating "+SCENARIO_NAME_2);
		scenarioClient.createScenario(SCENARIO_NAME_2);
		scenarioClient.addTest(TEST_NAME, "SimpleTests", 1);
		scenarioClient.addTest(SCENARIO_NAME,TestType.SCENARIO.getType());
		applicationClient.saveScenario();
	}
	
	@Test
	@TestProperties(paramsInclude = { "" })
	public void resetToDefultTestInsideSingalScenario () throws Exception {
		
		scenarioClient.openScenario(SCENARIO_NAME);
		
		report.step("Change tests parmters");
		scenarioClient.setTestParameter(1, TAB_NAME, PRAM_NAME,BEFORE_STRING,false);
		scenarioClient.setTestParameter(2, TAB_NAME, PRAM_NAME,BEFORE_STRING,false);
		String  testOneParameter =scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		String testTwoParameter =scenarioClient.getTestParameter(2, TAB_NAME, PRAM_NAME);
		report.report("test one parameter befor \"Reset To Defult\" is: "+testOneParameter);
		report.report("test Two parameter \"Reset To Defult\" is: "+testTwoParameter);
		
		report.step("Reset to defult test-1");
		scenarioClient.resetToDefault(1);
		 String  testOneParameterAfter =scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		 String testTwoParameterAfter =scenarioClient.getTestParameter(2, TAB_NAME, PRAM_NAME);
		if(0 == testOneParameterAfter.compareTo(testOneParameter)){
			report.report("The parmter of test1 didn't change despite the \"Reset To Defult\" ",false);
		}else{
			report.report("The parmter of test1 did change ");
		}
		if(0 != testTwoParameterAfter.compareTo(testTwoParameter)){
			report.report("The parmter of test1 has been changed! the new value is: "+testTwoParameterAfter+" the old value is: "+testTwoParameter,false);
		}else{
			report.report("The parmter of test2 didn't change ");
		}
		
	}
	
	@Test
	@TestProperties(paramsInclude = { "" })
	public void resetToDefultNestedScenario () throws Exception {
		
		report.step("Change tests parmters in "+SCENARIO_NAME);
		scenarioClient.openScenario(SCENARIO_NAME);
		scenarioClient.setTestParameter(1, TAB_NAME, PRAM_NAME,BEFORE_STRING,false);
		scenarioClient.setTestParameter(2, TAB_NAME, PRAM_NAME,BEFORE_STRING,false);
		String inerScenarioFirstTestParameter = scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		String inserScenarioSecondParamter = scenarioClient.getTestParameter(2, TAB_NAME, PRAM_NAME);
		applicationClient.saveScenario();

		
		
		report.step("Change tests parmters in "+SCENARIO_NAME_2);
		scenarioClient.openScenario(SCENARIO_NAME_2);
		scenarioClient.setTestParameter(1, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		scenarioClient.setTestParameter(3, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		scenarioClient.setTestParameter(4, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		String  testOneParameter = scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		
		
		report.step("Reset to defult "+SCENARIO_NAME);
		
		scenarioClient.resetToDefault(2);
		 String  testOneParameterAfter = scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		 String inerScenarioFirstTestParameterAfter = scenarioClient.getTestParameter(3, TAB_NAME, PRAM_NAME);
		 String inserScenarioSecondParamterAfter = scenarioClient.getTestParameter(4, TAB_NAME, PRAM_NAME);
		 if(0 != inerScenarioFirstTestParameterAfter.compareTo(inerScenarioFirstTestParameter) && 0 == inserScenarioSecondParamterAfter.compareTo(inserScenarioSecondParamter) ){
				report.report("The parmter of "
						+SCENARIO_NAME 
						+"didn't change despite the \"Reset To Defult\"! The new value of test1.1 is "
						+inerScenarioFirstTestParameterAfter
						+" wen it should be "+inerScenarioFirstTestParameter
						+"! The new value of test1.2 is "
						+inserScenarioSecondParamterAfter+" wen it should be "+inserScenarioSecondParamter,
						false);
			}else{
				report.report("The parmter of "+SCENARIO_NAME +" did change ");
			}
			if(0 != testOneParameterAfter.compareTo(testOneParameter)){
				report.report("The parmter of test1 has been changed! the new value is: "+testOneParameterAfter+" the old value is: "+testOneParameter,false);
			}else{
				report.report("The parmter of test1 didn't change ");
			}
		
		
	}
	
	@Test
	@TestProperties(paramsInclude = { "" })
	public void resetToDefultNestedScenarioOnAllScenario () throws Exception {
	
		report.step("Change "+SCENARIO_NAME_2 +" parmter");
		scenarioClient.setTestParameter(1, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		scenarioClient.setTestParameter(3, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		scenarioClient.setTestParameter(4, TAB_NAME, PRAM_NAME,AFTER_STRING,false);
		 String  testOneParameter = scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		 String testTwoParameter = scenarioClient.getTestParameter(3, TAB_NAME, PRAM_NAME);
		 String testThredParamter = scenarioClient.getTestParameter(4, TAB_NAME, PRAM_NAME);
		
		report.step("Reset to defult "+SCENARIO_NAME_2);
		scenarioClient.resetToDefault(0);
		 String  testOneParameterAfter = scenarioClient.getTestParameter(1, TAB_NAME, PRAM_NAME);
		 String testTwoParameterAfter = scenarioClient.getTestParameter(3, TAB_NAME, PRAM_NAME);
		 String testThredParamterAfter = scenarioClient.getTestParameter(4, TAB_NAME, PRAM_NAME);
		 
		 if( 0 != testOneParameter.compareTo(testOneParameterAfter) || 0 !=  testTwoParameter.compareTo(testTwoParameterAfter) || 0 != testThredParamter.compareTo(testThredParamterAfter)){
			 report.report("The parmter of "
						+SCENARIO_NAME_2 
						+"didn't change despite the \"Reset To Defult\"! The new value of test1 is "
						+testOneParameterAfter
						+" wen it should be "+testOneParameterAfter
						+"! The new value of "+SCENARIO_NAME+".test1.1 is "
						+testTwoParameterAfter+" wen it should be "+testTwoParameter
						+"! The new value of "+SCENARIO_NAME+".test1.2 is "
						+testThredParamterAfter+" wen it should be "+testThredParamter,
						false); 
		 }
		
	}
}
