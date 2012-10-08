package com.aqua.sanity;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class ScenarioNavigationFunctionality extends JSysTestCase4UseExistingServer{
	public ScenarioNavigationFunctionality(){
		super();
	}
	
	@Test
	public void checkScenariosNavigation() throws Throwable{
		scenarioClient.createScenario("navigation_1");
		scenarioClient.addTest("testThatPass", "SimpleTests",1);
		scenarioClient.createScenario("navigation_2");
		scenarioClient.addTest("testThatPass", "SimpleTests",2);
		scenarioClient.createScenario("navigation_3");
		scenarioClient.addTest("testThatPass", "SimpleTests",3);
		scenarioClient.scenarioNavigateBackward();
		scenarioClient.scenarioNavigateBackward();
		
		reporterClient.initReporters();
		sleep(1000);
		applicationClient.play();
		applicationClient.waitForExecutionEnd();
		sleep(2000);
		remoteInformationClient.checkNumberOfTestExecuted(1);

		scenarioClient.scenarioNavigateForward();
		reporterClient.initReporters();
		sleep(1000);
		applicationClient.play();
		applicationClient.waitForExecutionEnd();
		sleep(2000);
		remoteInformationClient.checkNumberOfTestExecuted(2);

		scenarioClient.scenarioNavigateForward();
		reporterClient.initReporters();
		sleep(1000);
		applicationClient.play();
		applicationClient.waitForExecutionEnd();
		sleep(3000);
		remoteInformationClient.checkNumberOfTestExecuted(3);
		sleep(2000);
	}

}
