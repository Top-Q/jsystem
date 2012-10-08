package com.aqua.sanity.junit4;

import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;

import org.junit.Test;

import utils.ScenarioUtils;

import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class JUnit4Functionality extends JSysTestCase4 {
	public JUnit4Functionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	@Test
	@TestProperties(name = "5.2.4.23 JUnit 4 tests")
	public void testJUnit4() throws Exception{
		jsystem.launch();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("compare1", "JUnit4Tests", true);
		jsystem.addTest("compare2", "JUnit4Tests", true);
		jsystem.setTestParameter(1, "General", "Value", "1", false);
		jsystem.setTestParameter(2, "General", "Value", "3", false);
		jsystem.play();
		jsystem.waitForRunEnd();
        jsystem.checkNumberOfTestExecuted(2);
        jsystem.checkNumberOfTestsPass(1);
	}
}