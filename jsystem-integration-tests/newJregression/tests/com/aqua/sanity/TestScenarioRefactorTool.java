package com.aqua.sanity;

import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class TestScenarioRefactorTool extends JSysTestCase4UseExistingServer {

	private final String rootScenario = "firstScenario";

	public TestScenarioRefactorTool() {
		super();
	}

	/*
	 * Scenario s exists Scenario s has sub scenarios (not yet) Scenario s
	 * includes test t. Test t has parameter p with value v. When test t is
	 * executed it asserts that the parameter p has the value v. Test t’ exists
	 * Test t’ has parameter p’ When test t’ executed it asserts the parameter
	 * p’ has the value v.
	 */

	@Before
	public void setUp() throws Exception {
		super.setUp();
		scenarioClient.cleanScenario(rootScenario);
		scenarioClient.addTest("testCurrent", "CurrentNewTest", 1);
	    applicationClient.saveScenario();
	}

	/*
	 * The "scenario refactor tool" changes scenario S to S' The
	 * "scenario refactor tool" changes test "testCurrent" in S’ to tests
	 * "testNew" The "scenario refactor tool" renames parameter currentParameter
	 * to p’ in test t’
	 */

	@Test
	public void testScenarioRefactorTool() throws Exception {
		report.report("starting testScenarioRefactorTool");
		
		applicationClient.play(true);
		// To run the tool.
		
		Command command = new Command();
		command.setCmd(new String[] {
				"cmd.exe",
				"/c",
				"java -jar /Users/itai_a/ScenarioRefactorTool.jar -scenario :C:\\jsystem\\runner\\runnerout\\testsProject scenarios\firstScenario scenarios\newScenario" });
		try {
			Execute.execute(command, true);
			report.report("Refactor Tool Output", command.getStdout().toString(), true);
		} catch (Exception e) {
			report.report(e.getMessage());
		}
	    applicationClient.saveScenario();

		command.setCmd(new String[] {
				"cmd.exe",
				"/c",
				"java -jar /Users/itai_a/ScenarioRefactorTool.jar -test :C:\\jsystem\\runner\\runnerout\\testsProject sanity.CurrentNewTest.testCurrent sanity.CurrentNewTest.testNew" });
		try {
			Execute.execute(command, true);
			report.report("Refactor Tool Output", command.getStdout().toString(), true);
		} catch (Exception e) {
			report.report(e.getMessage());
		}

	    applicationClient.saveScenario();
		applicationClient.play(true);

		report.report("ending testScenarioRefactorTool");

	}

}
