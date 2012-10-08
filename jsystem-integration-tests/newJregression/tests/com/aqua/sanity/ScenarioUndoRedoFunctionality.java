package com.aqua.sanity;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class ScenarioUndoRedoFunctionality extends JSysTestCase4UseExistingServer{
	public ScenarioUndoRedoFunctionality(){
		super();
	}
	
//	@Test
//	public void checkBasicUndoRedo() throws Throwable{
//		scenarioClient.createScenario("undoScenario");
//		scenarioClient.addTest("testThatPass", "SimpleTests",4);
//		scenarioClient.createScenario("undoScenario2");
//		scenarioClient.addTest("testThatPass", "SimpleTests",3);
//		scenarioClient.scenarioNavigateBackward();
//		
//		scenarioClient.scenarioUndo();
//		scenarioClient.scenarioUndo();
//		
//		reporterClient.initReporters();
//		sleep(1000);
//		applicationClient.play();
//		applicationClient.waitForExecutionEnd();
//		sleep(3000);
//		remoteInformationClient.checkNumberOfTestExecuted(2);
//
//		scenarioClient.scenarioRedo();
//		scenarioClient.scenarioRedo();
//		reporterClient.initReporters();
//		sleep(1000);
//		applicationClient.play();
//		applicationClient.waitForExecutionEnd();
//		sleep(4000);
//		remoteInformationClient.checkNumberOfTestExecuted(4);
//
//	}
	
	/**
	 * 1. create a new scenario on the remote jsystem
	 * 2. add a test with parameters to the scenario
	 * 3. set the string paramter from a LOV to 1 and make sure it's 1
	 * 4. change the string paramter from a LOV to 3 and make sure it's 3
	 * 5. perform undo operation and check value is 1 again.
	 * 6. perform a redo operation and check it's 3 again.
	 * 7. change value back to 1 and finish test.
	 * @throws Exception
	 */
	@Test
	public void undoRedoStringChange()throws Exception{
		final String scenarioName = "undoRedoScenario";
		report.step("create a new remote scenario");
		scenarioClient.createScenario(scenarioName);
		scenarioClient.cleanScenario(scenarioName);
		applicationClient.saveScenario();
		report.step("add a test with paramters to the scenario");
		scenarioClient.addTest("testUndoStringChange", "UndoRedoTests");
		report.step("set the string parameter from LOV to 1 and make sure it's 1");
		scenarioClient.setTestParameter(1, "General", "stringParameter", "1", true);
		String parameterValue = scenarioClient.getTestParameter(1, "General", "stringParameter");
		int testAgainst = Integer.parseInt(parameterValue);
		analyzer.setTestAgainstObject(testAgainst);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,1,0));
		report.step("change the string paramter from a LOV to 3 and make sure it's 3");
		scenarioClient.setTestParameter(1, "General", "stringParameter", "3", true);
		parameterValue = scenarioClient.getTestParameter(1, "General", "stringParameter");
		testAgainst = Integer.parseInt(parameterValue);
		analyzer.setTestAgainstObject(testAgainst);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,3,0));
		applicationClient.undo();
		Thread.sleep(500);
		parameterValue = scenarioClient.getTestParameter(1, "General", "stringParameter");
		testAgainst = Integer.parseInt(parameterValue);
		analyzer.setTestAgainstObject(testAgainst);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,1,0));
		applicationClient.redo();
		Thread.sleep(500);
		parameterValue = scenarioClient.getTestParameter(1, "General", "stringParameter");
		testAgainst = Integer.parseInt(parameterValue);
		analyzer.setTestAgainstObject(testAgainst);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL,3,0));
		Thread.sleep(2000);
		System.out.println("break point");
	}

}
