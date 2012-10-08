package com.aqua.fixtures;

import jsystem.framework.fixture.Fixture;

import com.aqua.jsystemobjects.ClientHandlerType;
import com.aqua.jsystemobjects.JSystemEnvController;
import com.aqua.jsystemobjects.TestType;
import com.aqua.jsystemobjects.clients.JScenarioClient;

public class CreateStabilityScenarioFixture extends Fixture {
	JSystemEnvController envController = null;
	JScenarioClient scenarioClient = null;
	private int numOfTestsToRunForStabilityCheck = 1000;
	private int numOfTestsLastLoop = 0;
	public CreateStabilityScenarioFixture(){
		setParentFixture(NewActivateRunnerFixture.class);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		JSystemEnvController envController = (JSystemEnvController)system.getSystemObject("envController");
		JScenarioClient scenarioClient = (JScenarioClient)(envController.getSystemClient(ClientHandlerType.SCENARIO));
		numOfTestsLastLoop = numOfTestsToRunForStabilityCheck/500;
		/*create the scenarios*/
		report.step("create 4 scenarios");
		scenarioClient.createScenario("stabilityMainScenario");		
		scenarioClient.createScenario("stabilityChildScenario_1");
		scenarioClient.createScenario("stabilityChildScenario_2");
		scenarioClient.createScenario("stabilityChildScenario_3");
		scenarioClient.openScenario("stabilityChildScenario_3");
		scenarioClient.selectTestRow(0);
		/*add 5 tests to the lowest scenario*/
		report.step("add  5 tests to scenario 3");
		scenarioClient.addTest( "heavyRportingTest", "ReporterStabilityTest",1);
		scenarioClient.addTest( "heavyReportingWarningTest", "ReporterStabilityTest",1);
		scenarioClient.addTest( "reportParameters", "ReporterStabilityTest",1);
		scenarioClient.addTest( "heavyReportingErroredTest", "ReporterStabilityTest",1);
		scenarioClient.addTest( "heavyReportingExceptionTest", "ReporterStabilityTest",1);
		
		/* choose next level scenario and add to it the previous scenario x10 times
		 * 5 x 10 = 50 tests*/
		report.step("add scenario3 10 times to scenario 2, total of 50 tests");
		scenarioClient.openScenario("stabilityChildScenario_2");
		scenarioClient.addTest("stabilityChildScenario_3", TestType.SCENARIO.getType(),10);
		
		/* choose the next scenario and add to it the previous scenario x10 times
		 * 5x10x10 = 500 tests*/
		report.step("add scenario2 10 times to scenario 1, total of 500 tests");
		scenarioClient.openScenario("stabilityChildScenario_1");
		scenarioClient.addTest("stabilityChildScenario_2", TestType.SCENARIO.getType(),10);
		
		/* add the large scenario x10 times to the main scenario
		 * 5x10x10x10 = 5000 tests*/
		report.step("add scenario 1 10 times to main scenario, total of 5000 tests");
		scenarioClient.openScenario("stabilityMainScenario");
		scenarioClient.addTest("stabilityChildScenario_1", TestType.SCENARIO.getType(),numOfTestsLastLoop);
	}

	public int getNumOfTestsToRunForStabilityCheck() {
		return numOfTestsToRunForStabilityCheck;
	}

	public void setNumOfTestsToRunForStabilityCheck(
			int numOfTestsToRunForStabilityCheck) {
		this.numOfTestsToRunForStabilityCheck = numOfTestsToRunForStabilityCheck;
	}
}
