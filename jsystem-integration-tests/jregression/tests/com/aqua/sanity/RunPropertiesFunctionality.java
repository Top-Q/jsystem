package com.aqua.sanity;

import jsystem.framework.TestProperties;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.general.ScenarioUtils;
import com.aqua.jsystemobject.ActivateRunnerFixture;

/**
 */
public class RunPropertiesFunctionality extends JSysTestCaseOld {
	public RunPropertiesFunctionality(){
		super();
		setFixture(ActivateRunnerFixture.class);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		jsystem.initReporters();
	}
	
	/**
	 */
	@TestProperties(name = "Testing that run properties are created and deleted as expected")
	public void testRunProperties() throws Exception{
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String val = ""+System.currentTimeMillis();
		jsystem.addTest("testSetRunProperty", "TestRunProperties", true);
		jsystem.setTestParameter(1, "General", "PropName", "testRunProperties", false);
		jsystem.setTestParameter(1, "General", "PropValue", val, false);

		jsystem.addTest("testCheckRunProperty", "TestRunProperties", true);
		jsystem.setTestParameter(2, "General", "PropName", "testRunProperties", false);
		jsystem.setTestParameter(2, "General", "PropValue", val, false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);

		jsystem.initReporters();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testCheckRunProperty", "TestRunProperties", true);
		jsystem.setTestParameter(1, "General", "PropName", "testRunProperties", false);
		jsystem.setTestParameter(1, "General", "PropValue", val, false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.checkNumberOfTestsPass(0);		
	}
	
	/**
	 */
	@TestProperties(name = "Testing that summary properties appear in xml report")
	public void testSummaryProperties() throws Exception{
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		String version = ""+System.currentTimeMillis();

		jsystem.addTest("testSetPermanentSummaryProperty", "TestRunProperties", true);
		jsystem.setTestParameter(1, "General", "PropName", "myProp1", false);
		jsystem.setTestParameter(1, "General", "PropValue", "myPropValue1", false);
		
		jsystem.addTest("testSetTemporarySummaryProperty", "TestRunProperties", true);
		jsystem.setTestParameter(2, "General", "PropName", "myProp2", false);
		jsystem.setTestParameter(2, "General", "PropValue", "myPropValue2", false);

		jsystem.addTest("testSetVersionSummaryProperty", "TestRunProperties", true);
		jsystem.setTestParameter(3, "General", "Version", version, false);

		
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(3);
		jsystem.checkNumberOfTestsPass(3);
		sleep(2000);
		jsystem.checkReporterSummaryProperty("Version",version,true);
		jsystem.checkReporterSummaryProperty("myProp1","myPropValue1",true);
		jsystem.checkReporterSummaryProperty("myProp2","myPropValue2",true);
		
		jsystem.initReporters();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testThatDoesntDoAnyThing", "TestRunProperties", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkReporterSummaryProperty("Version","ver3",false);
		jsystem.checkReporterSummaryProperty("myProp1","myPropValue1",true);
		jsystem.checkReporterSummaryProperty("myProp2","myPropValue2",false);

	}
}
