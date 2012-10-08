package com.aqua.sanity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aqua.analyzers.BooleanAnalyzer;
import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.enumerators.Buttons;
import com.aqua.jsystemobjects.TestType;

public class NavigateToSubScenarioTest extends JSysTestCase4UseExistingServer {

	private final String scenario1 = "Scenario1";
	private final String scenario2 = "Scenario2";
	private final String scenario3 = "Scenario3";
	private final String scenario4 = "Scenario4";
	private final String leftDirection = "left";
	private final String rightDirection = "right";
	public NavigateToSubScenarioTest() {
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		super.setUp();
		scenarioClient.createScenario(scenario1);
		scenarioClient.createScenario(scenario2);
		scenarioClient.createScenario(scenario3);
		scenarioClient.createScenario(scenario4);
		applicationClient.saveScenario();
	}
	
	/**
	 * 1. select the root scenario and check that it is scenario4 and then make sure the 
	 *    navigate left button is enable, and that navigate right button is disabled.
	 * 2. move to scenario2 make sure it's scenario2 and and check that both right and left
	 *    navigate buttons are enabled.
	 * 3. push the right navigate button once and check that scenario is scenario3 and both buttons are enabled.
	 *    push the right navigate button once and check that scenario is scenario4 and right button is disabled.
	 *    push the left navigate button twice and check that scenario is scenario2 and both buttons are enabled.
	 * @throws Exception
	 */
	@Test
	public void moveBetweenCreatedScenarios()throws Exception{
		report.step("select the root scenario and check that it is scenario4 and then make sure the navigate left button is enable, and that navigate right button is disabled.");
		scenarioClient.selectTestRow(0);
		scenarioClient.checkCurrentScenarioIsMatched(scenario4);
		boolean result = applicationClient.checkNavigateButtonIsEnabled(leftDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"left button is enabled as expected","left button is not enabled"));
		result = applicationClient.checkNavigateButtonIsEnabled(rightDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(false,"right button is not enabled as expected","right button is enabled"));
		report.step("move to scenario2 make sure it's scenario2 and and check that both right and left navigate buttons are enabled.");
		applicationClient.pushButton(Buttons.PREVIOUS_SCENARIO.getName(),2);
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		report.step("push the right navigate button once and check that scenario is scenario3 and both buttons are enabled.\npush the right navigate button once and check that scenario is scenario4 and right button is disabled.\npush the left navigate button twice and check that scenario is scenario2 and both buttons are enabled.");
		applicationClient.pushButton(Buttons.NEXT_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched(scenario3);
		result = applicationClient.checkNavigateButtonIsEnabled(leftDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"button navigate previous is enabled as expected","button navigate forward is disabled"));
		result = applicationClient.checkNavigateButtonIsEnabled(rightDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"button navigate previous is enabled as expected","button navigate forward is disabled"));
		applicationClient.pushButton(Buttons.NEXT_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched(scenario4);
		result = applicationClient.checkNavigateButtonIsEnabled(leftDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"button navigate previous is enabled as expected","button navigate forward is disabled"));
		result = applicationClient.checkNavigateButtonIsEnabled(rightDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(false,"button navigate previous is enabled as expected","button navigate forward is disabled"));
		applicationClient.pushButton(Buttons.PREVIOUS_SCENARIO.getName(),2);
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		result = applicationClient.checkNavigateButtonIsEnabled(leftDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"button navigate previous is enabled as expected","button navigate forward is disabled"));
		result = applicationClient.checkNavigateButtonIsEnabled(rightDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"button navigate previous is enabled as expected","button navigate forward is disabled"));
	}
	
	
	/**
	 * 1. to two steps back to scenario 2 and check it's really 2 as expected.
	 * 2. create a scenario midScenario, and check that scenario3 and scenario4 can't be navigated to anymore
	 * @throws Exception
	 */
	@Test
	public void goToScenario2AndCreateNewScenarioAndCheckThat3and4NoLongerExist()throws Exception{
		report.step("select the current scenario and check it's scenario4");
		scenarioClient.selectTestRow(0);
		scenarioClient.checkCurrentScenarioIsMatched(scenario4);
		report.step("navigate twice backwords and check the scenario is scenario2");
		applicationClient.pushButton(Buttons.PREVIOUS_SCENARIO.getName(), 2);
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		report.step("create a scenario midScenario, and check that scenario3 and scenario4 can't be navigated to anymore");
		scenarioClient.createScenario("midScenario");
		boolean result = applicationClient.checkNavigateButtonIsEnabled(rightDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(false,"the navigate to next scenario is disabled as expected","the navigate to next scenario is enabled"));
		result = applicationClient.checkNavigateButtonIsEnabled(leftDirection);
		analyzer.setTestAgainstObject(result);
		analyzer.analyze(new BooleanAnalyzer(true,"the navigate to previous scenario is enabled as expected","the navigate to previous scenario is disabled"));
		applicationClient.pushButton(Buttons.PREVIOUS_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		applicationClient.pushButton(Buttons.NEXT_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched("midScenario");
	}
	
	/**
	 * 1. open scenario1 and add to it a sub scenario scenaroi2
	 * 2. select scenario2 and choose, Navigate to sub scenario
	 * 3. check that current scenario opened is scenario2 as expected.
	 * 4. navigate left once and check that current scenario is scenario1
	 * 5. navigate right once and check that current scenario is scenario2
	 * @throws Exception
	 */
	@Test
	public void createScenarioInSceanrioSelectChildScenarioAndNavigateToSubScenario()throws Exception{
		scenarioClient.openScenario(scenario1);
		scenarioClient.selectTestRow(0);
		scenarioClient.addTest(scenario2,TestType.SCENARIO.getType());
		applicationClient.saveScenario();
		scenarioClient.selectTestRow(1);
		scenarioClient.navigateToSubScenario(1);
		applicationClient.saveScenario();
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		applicationClient.pushButton(Buttons.PREVIOUS_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched(scenario1);
		applicationClient.pushButton(Buttons.NEXT_SCENARIO.getName());
		scenarioClient.checkCurrentScenarioIsMatched(scenario2);
		System.out.println("");
	}
	@After
	public void tearDown()throws Exception{
		super.tearDown();
		scenarioClient.openScenario(scenario1);
		scenarioClient.deleteCurrentScenario();
		scenarioClient.openScenario(scenario2);
		scenarioClient.deleteCurrentScenario();
		scenarioClient.openScenario(scenario3);
		scenarioClient.deleteCurrentScenario();
		scenarioClient.openScenario(scenario4);
		scenarioClient.deleteCurrentScenario();
	}
}
