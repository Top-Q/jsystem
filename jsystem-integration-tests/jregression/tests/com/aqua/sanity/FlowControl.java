package com.aqua.sanity;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.TestProperties;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.guiMapping.JsystemMapping;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.ScenarioUtils;

import com.aqua.general.JSysTestCase4;
import com.aqua.jsystemobject.JSystemEnvController;
import com.aqua.jsystemobject.NewActivateRunnerFixture;
import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.clients.JReporterClient;
import com.aqua.jsystemobject.clients.JScenarioClient;
import com.aqua.jsystemobject.handlers.JServerHandlers;

public class FlowControl extends JSysTestCase4 {
	private String errorMessage = "test failure";
	private String defaultErrorMessage = "Test fail";
	
	public FlowControl() {
		super();
		setFixture(NewActivateRunnerFixture.class);
	}
	
	/**
	 * this method is not calling the super.setUp and overrides it with code
	 * duplication since it's a mid phase test class written between old and new
	 * structure. environment is built by the NewActivateRunnerFixture and the super
	 * setUp is trying to recreate the env by calling the getJsystemEnv func.
	 * this way we ensure only one environment is created and applicationClient holds all
	 * relevant references for operation.
	 */
	@Before
	public void setUp() throws Exception {
		envController = (JSystemEnvController)system.getSystemObject("envController");
		applicationClient = (JApplicationClient)envController.getSystemClient(JServerHandlers.APPLICATION);
		scenarioClient = (JScenarioClient)envController.getSystemClient(JServerHandlers.SCENARIO);
		reporterClient = (JReporterClient)envController.getSystemClient(JServerHandlers.REPORTER);
		enableFlowControlToolbar();
		
		// Set the run mode
		applicationClient.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_RUN.toString());
	}
//	@After
//	public void tearDown(){
//		
//	}
	
	public void enableFlowControlToolbar() throws Exception {
		if (!(boolean)applicationClient.getFlowControlToolbarState()) {
			report.step("Enable of the toolbar");
			applicationClient.setToolbarView(JsystemMapping.getInstance().getFlowControlToolbar());	
		}
		
		// Recheck and throw an exception if not enabled
		if (!(boolean)applicationClient.getFlowControlToolbarState()) {
			throw new Exception("Failed to enable Flow Control Toolbar");
		}
	}

	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Test Flow Control toolbar enable/disable")
	@Test
	public void testFlowControlToolBar() throws Exception {
		
		report.step("Get the default state of the toolbar");
		Boolean startState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getFlowControlToolbar());
		Boolean startSeenState = (boolean)applicationClient.getFlowControlToolbarState();
		
		report.step("Change the state of the toolbar");
		applicationClient.setToolbarView(JsystemMapping.getInstance().getFlowControlToolbar());
		
		
		report.step("Get the new state of the toolbar");
		Boolean endState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getFlowControlToolbar());
		Boolean endSeenState = (boolean)applicationClient.getFlowControlToolbarState();
		
		report.step("Verify the toolbar change states");
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(startState);
		analyzer.analyze(new CompareValues(startSeenState));
		
		// Verify the state has changed
		analyzer.analyze(new CompareValues(!endState));
		
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(endState);
		analyzer.analyze(new CompareValues(endSeenState));
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Test Main toolbar enable/disable")
	@Test
	public void testMainToolBar() throws Exception {
		
		
		report.step("Get the default state of the toolbar");
		Boolean startState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getMainToolbar());
		Boolean startSeenState = (boolean)applicationClient.getMainToolbarState();

		
		report.step("Change the state of the toolbar");
		applicationClient.setToolbarView(JsystemMapping.getInstance().getMainToolbar());
	

		report.step("Get the new state of the toolbar");
		Boolean endState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getMainToolbar());
		Boolean endSeenState = (boolean)applicationClient.getMainToolbarState();
		
		report.step("Verify the toolbar change states");
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(startState);
		analyzer.analyze(new CompareValues(startSeenState));
		
		// Verify the state has changed
		analyzer.analyze(new CompareValues(!endState));
		
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(endState);
		analyzer.analyze(new CompareValues(endSeenState));
	}

	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Test Agent toolbar enable/disable")
	@Test
	public void testAgentToolBar() throws Exception {

		
		report.step("Get the default state of the toolbar");
		Boolean startState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getAgentToolbar());
		Boolean startSeenState = (boolean)applicationClient.getAgentToolbarState();

		
		report.step("Change the state of the toolbar");
		applicationClient.setToolbarView(JsystemMapping.getInstance().getAgentToolbar());


		report.step("Get the new state of the toolbar");
		Boolean endState = (boolean)applicationClient.getToolbarViewState(JsystemMapping.getInstance().getAgentToolbar());
		Boolean endSeenState = (boolean)applicationClient.getAgentToolbarState();
		
		report.step("Verify the toolbar change states");
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(startState);
		analyzer.analyze(new CompareValues(startSeenState));
		
		// Verify the state has changed
		analyzer.analyze(new CompareValues(!endState));
		
		// Verify the checkbox and the toolbar status are the same
		analyzer.setTestAgainstObject(endState);
		analyzer.analyze(new CompareValues(endSeenState));
	}

	/**
	 * @throws Exception
	 */
	public void createIfBasicScenario() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "newIfScenario");

		report.step("Add Test that return parameter");
		scenarioClient.addTest("testAddOneAndReturnParams", "FlowControlTests");
		report.step("Set parameter VarA to 122");
		scenarioClient.setTestParameter(1, "General", "VarA", "122", false);

		report.step("Add an If object, and expand it");
		scenarioClient.addIfObject();
		scenarioClient.collapseExpandScenario(2);

		report.step("Add an 'Else If' object");
		scenarioClient.selectTest(2);
		scenarioClient.addElseIfObject();

		report.step("Set if parameter VarA");
		scenarioClient.setTestParameter(2, "General", "FirstValue", "${VarA}", false);

		report.step("Set else if parameter VarA");
		scenarioClient.setTestParameter(3, "General", "FirstValue", "${VarA}", false);
		
		report.step("Add pass test");
		scenarioClient.selectTest(2);
		scenarioClient.addTest("testThatPass", "FlowControlTests");

		report.step("Add Pass test into 'else if'");
		scenarioClient.selectTest(4);
		scenarioClient.addTest("testThatPass", "FlowControlTests");

		report.step("Add fail test into 'else'");
		scenarioClient.selectTest(6);
		scenarioClient.addTest("testThatFail", "FlowControlTests");
	}
	
	/**
	 * @throws Exception
	 */
	public void createIfFailScenario() throws Exception {
		report.step("Create the basic If scenario");
		createIfBasicScenario();
		
		report.step("Set parameter SecondValue to wrong value");
		scenarioClient.setTestParameter(2, "General", "SecondValue", "42", false);

		report.step("Set else if parameter SecondValue to wrong value");
		scenarioClient.setTestParameter(4, "General", "SecondValue", "42", false);
	}
	
	/**
	 * @throws Exception
	 */
	public void createIfPassScenario() throws Exception {
		report.step("Create the basic If scenario");
		createIfBasicScenario();
		
		report.step("Set parameter SecondValue to correct value");
		scenarioClient.setTestParameter(2, "General", "SecondValue", "123", false);

		report.step("Set else if parameter SecondValue to wrong value");
		scenarioClient.setTestParameter(4, "General", "SecondValue", "42", false);
	}
	
	/**
	 * @throws Exception
	 */
	public void createIfIntoElseIfScenario() throws Exception {
		report.step("Create the basic If scenario");
		createIfBasicScenario();
		
		report.step("Set parameter SecondValue to wrong value");
		scenarioClient.setTestParameter(2, "General", "SecondValue", "42", false);

		report.step("Set else if parameter SecondValue to correct value");
		scenarioClient.setTestParameter(4, "General", "SecondValue", "123", false);
	}
	
	/**
	 * @throws Exception
	 */
	public void createSwitchScenario() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "newSwitchScenario");


		report.step("Add Test that return parameter");
		scenarioClient.addTest("testAddOneAndReturnParams", "FlowControlTests");
		
		report.step("Set parameter VarA to 122");
		scenarioClient.setTestParameter(1, "General", "VarA", "122", false);

		report.step("Add a Switch object, and expand it");
		scenarioClient.addSwitchObject();
		scenarioClient.collapseExpandScenario(2);
		
		report.step("Set Switch parameter Value");
		scenarioClient.setTestParameter(2, "General", "Value", "${VarA}", false);
		
		report.step("Add case object");
		scenarioClient.selectTest(2);
		scenarioClient.addCaseObject();
		
		report.step("Add pass test into case");
		scenarioClient.selectTest(3);
		scenarioClient.addTest("testThatPass", "FlowControlTests");

		report.step("Set case value to 123");
		scenarioClient.setTestParameter(3, "General", "Value", "123", false);

		report.step("Add fail test as default");
		scenarioClient.selectTest(5);
		scenarioClient.addTest("testThatFail", "FlowControlTests");
		
		// TODO: what should be verified ?
	}

	/**
	 * @throws Exception
	 */
	public void createForScenario() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "newForScenario");

		report.step("Add Test that return parameter");
		scenarioClient.addTest("testAddStringAndReturnParams", "FlowControlTests");
		
		report.step("Set parameter LoopString to 'a;b'");
		scenarioClient.setTestParameter(1, "General", "LoopString", "a;b", false);

		report.step("Add a Loop object, and expand it");
		scenarioClient.addLoopObject();
		
		report.step("Set Loop parameter Value");
		scenarioClient.setTestParameter(2, "General", "list", "${LoopString}", false);
		
		report.step("Add switch object");
		scenarioClient.selectTest(2);
		scenarioClient.addSwitchObject();
		scenarioClient.setTestParameter(3, "General", "Value", "${myVar}", false);
		
		report.step("Add 2 case objects");
		scenarioClient.selectTest(3);
		scenarioClient.addCaseObject();
		scenarioClient.selectTest(3);
		scenarioClient.addCaseObject();

		report.step("Set case value to a");
		scenarioClient.setTestParameter(4, "General", "Value", "a", false);
		report.step("Set case value to b");
		scenarioClient.setTestParameter(5, "General", "Value", "b", false);

		report.step("Add pass test into case");
		scenarioClient.selectTest(4);
		scenarioClient.addTest("testThatPass", "FlowControlTests");
		scenarioClient.selectTest(4);
		scenarioClient.addTest("testAssertValue", "FlowControlTests");
		scenarioClient.setTestParameter(5, "General", "Expected", "a", false);
		scenarioClient.setTestParameter(5, "General", "Given", "${myVar}", false);
		

		report.step("Add pass test into case");
		scenarioClient.selectTest(7);
		scenarioClient.addTest("testThatAssert", "FlowControlTests");
		scenarioClient.setTestParameter(8, "General", "ErrorMessage", errorMessage, false);
		scenarioClient.selectTest(7);
		scenarioClient.addTest("testAssertValue", "FlowControlTests");
		scenarioClient.setTestParameter(8, "General", "Expected", "b", false);
		scenarioClient.setTestParameter(8, "General", "Given", "${myVar}", false);

		
		report.step("Add fail test as default");
		scenarioClient.selectTest(10);
		scenarioClient.addTest("testThatFail", "FlowControlTests");
		scenarioClient.selectTest(10);
		scenarioClient.addTest("testAssertValue", "FlowControlTests");
		scenarioClient.setTestParameter(11, "General", "Expected", "c", false);
		scenarioClient.setTestParameter(11, "General", "Given", "${myVar}", false);

		
		// TODO: what should be verified ?
	}

	/**
	 * @throws Exception
	 */
	public void createForSubScenario() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "newForSubScenario");

		report.step("Add a Loop object, and expand it");
		scenarioClient.addLoopObject();
		
		scenarioClient.selectTest(1);
		scenarioClient.addTest("testThatPass", "FlowControlTests");
	}

	/**
	 * @throws Exception
	 */
	public void createForMainScenario() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "newForMainScenario");

		report.step("Add a Loop object, and expand it");
		scenarioClient.addLoopObject();
		
		report.step("Add the sub scenario to the loop");
		scenarioClient.selectTest(1);
		scenarioClient.addTest("newForSubScenario", "scenario");
	}

	/**
	 * @throws Exception
	 */
	public void executeForWithSubScenario() throws Exception {
		applicationClient.initReporters();
		applicationClient.play();
		applicationClient.waitForRunEnd();
		
		scenarioClient.checkNumberOfTestExecuted(16);
		scenarioClient.checkTestPass(1);
		scenarioClient.checkTestPass(2);
		scenarioClient.checkTestPass(3);
		scenarioClient.checkTestPass(4);
		scenarioClient.checkTestPass(5);
		scenarioClient.checkTestPass(6);
		scenarioClient.checkTestPass(7);
		scenarioClient.checkTestPass(8);
		scenarioClient.checkTestPass(9);
		scenarioClient.checkTestPass(10);
		scenarioClient.checkTestPass(11);
		scenarioClient.checkTestPass(12);
		scenarioClient.checkTestPass(13);
		scenarioClient.checkTestPass(14);
		scenarioClient.checkTestPass(15);
		scenarioClient.checkTestPass(16);
	}
	
	/**
	 * @throws Exception
	 */
	public void executeIfPassScenario() throws Exception {
		applicationClient.initReporters();
		applicationClient.play();
		applicationClient.waitForRunEnd();
		
		scenarioClient.checkNumberOfTestExecuted(2);
		scenarioClient.checkTestPass(1);
		scenarioClient.checkTestPass(2);
	}
	
	/**
	 * @throws Exception
	 */
	public void executeIfFailScenario() throws Exception {
		applicationClient.initReporters();
		applicationClient.play();
		applicationClient.waitForRunEnd();
		
		scenarioClient.checkNumberOfTestExecuted(2);
		scenarioClient.checkTestPass(1);
		scenarioClient.checkAssertionFailure(2, defaultErrorMessage);
	}
	
	/**
	 * @throws Exception
	 */
	public void executeIfIntoElseIfScenario() throws Exception {
		// Same execution & verification
		executeIfPassScenario();
	}
		
	/**
	 * @throws Exception
	 */
	public void executeSwitchScenario() throws Exception {
		applicationClient.initReporters();
		applicationClient.play();
		applicationClient.waitForRunEnd();
		
		scenarioClient.checkNumberOfTestExecuted(2);
		scenarioClient.checkTestPass(1);
		scenarioClient.checkTestPass(2);
	}
	
	/**
	 * @throws Exception
	 */
	public void executeForScenario() throws Exception {
		applicationClient.initReporters();
		applicationClient.play();
		applicationClient.waitForRunEnd();
		
		scenarioClient.checkNumberOfTestExecuted(7);
		scenarioClient.checkTestPass(1);
		scenarioClient.checkTestPass(2);
		scenarioClient.checkTestPass(3);
		scenarioClient.checkTestPass(4);
		scenarioClient.checkAssertionFailure(5, errorMessage);
		scenarioClient.checkTestPass(6);
		scenarioClient.checkAssertionFailure(7, defaultErrorMessage);
	}
	
	/**
	 * @throws Exception
	 */
	public void openSwitchScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/switchScenario");
	}
	
	/**
	 * @throws Exception
	 */
	public void openIfPassScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/ifPassScenario");
	}

	/**
	 * @throws Exception
	 */
	public void openIfFailScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/ifFailScenario");
	}
	/**
	 * @throws Exception
	 */
	public void openElseIfScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/elseIfScenario");
	}
		
	/**
	 * @throws Exception
	 */
	public void openForScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/forScenario");
	}
	/**use not default port for ftp command line
	 * @throws Exception
	 */
	public void openForWithSubScenario() throws Exception {
		scenarioClient.selectSenario(ScenarioUtils.getManualScenariosFolder() + "/forMainScenario");
	}
	
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'switch' scenario")
	@Test
	public void testCreateAndExecuteSwitchScenario() throws Exception {
		createSwitchScenario();
		
		executeSwitchScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Open and execute 'switch' scenario")
	@Test
	public void testOpenAndExecuteSwitchScenario() throws Exception {
		openSwitchScenario();
		
		executeSwitchScenario();
	}
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'if' scenario that gets true")
	@Test
	public void testCreateAndExecuteIfPassScenario() throws Exception {
		createIfPassScenario();
		
		executeIfPassScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Open and execute 'if' scenario that gets true")
	@Test
	public void testOpenAndExecuteIfPassScenario() throws Exception {
		openIfPassScenario();
		
		executeIfPassScenario();
	}

	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'if' scenario that gets false")
	@Test
	public void testCreateAndExecuteIfFailScenario() throws Exception {
		createIfFailScenario();
		
		executeIfFailScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Open and execute 'if' scenario that gets false")
	@Test
	public void testOpenAndExecuteIfFailScenario() throws Exception {
		openIfFailScenario();
		
		executeIfFailScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'if' scenario that gets true to 'Else if'")
	@Test
	public void testCreateAndExecuteElseIfScenario() throws Exception {
		createIfIntoElseIfScenario();
		
		executeIfIntoElseIfScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Open and execute 'if' scenario that gets true to 'Else if'")
	@Test
	public void testOpenAndExecuteElseIfScenario() throws Exception {
		openElseIfScenario();
		
		executeIfIntoElseIfScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'for' scenario")
	@Test
	public void testCreateAndExecuteForScenario() throws Exception {
		createForScenario();
		
		executeForScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Open and execute 'for' scenario with subscenario")
	@Test
	public void testOpenAndExecuteForScenario() throws Exception {
		openForWithSubScenario();
		
		executeForWithSubScenario();
	}
	
	/**
	 * @throws Exception
	 */
	@TestProperties(name = "Create and execute 'for' scenario with subscenario")
	@Test
	public void testCreateAndExecuteForWithSubScenario() throws Exception {
		createForSubScenario();
		createForMainScenario();
		
		executeForWithSubScenario();
	}
	
	@TestProperties(name = "Verify the behavior of the flow control buttons")
	@Test
	public void testVerifyButtonsBehavior() throws Exception {
		report.step("Clean the scenario");
		ScenarioUtils.createAndCleanScenario(scenarioClient, "checkButtons");
		
		// Verify default view
		verifyDefaultButtonsState();
		
		// Add a loop and select it
		scenarioClient.addLoopObject();
		scenarioClient.selectTest(1);
		
		// Verify the buttons state 
		verifyDefaultButtonsState();

		// Add switch
		scenarioClient.addSwitchObject();
		scenarioClient.selectTest(2);
		
		verifyButtonsStateSwitchSelected();
		
		// Add case
		scenarioClient.addCaseObject();
		scenarioClient.selectTest(3);
		verifyDefaultButtonsState();
		
		// Verify when default is selected (same as else)
		scenarioClient.selectTest(4);
		verifyButtonsStateElseSelected();	
		
		// Add if
		scenarioClient.addIfObject();
		scenarioClient.selectTest(5);
		verifyButtonsStateIfSelected();
		
		// Add Else If
		scenarioClient.addElseIfObject();
		scenarioClient.selectTest(6);
		verifyButtonsStateIfSelected();
		
		// Verify when else is selected
		scenarioClient.selectTest(7);
		verifyButtonsStateElseSelected();
		
		
	}
	
	public void verifyDefaultButtonsState() throws Exception {
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new CompareValues(applicationClient.isLoopButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isIfButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isSwitchButtonEnabled()));

		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new CompareValues(applicationClient.isElseIfButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isCaseButtonEnabled()));
	}
	
	public void verifyButtonsStateSwitchSelected() throws Exception {
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new CompareValues(applicationClient.isCaseButtonEnabled()));

		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new CompareValues(applicationClient.isLoopButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isIfButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isSwitchButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isElseIfButtonEnabled()));
	}
	public void verifyButtonsStateIfSelected() throws Exception {
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new CompareValues(applicationClient.isLoopButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isIfButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isSwitchButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isElseIfButtonEnabled()));

		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new CompareValues(applicationClient.isCaseButtonEnabled()));
	}
	public void verifyButtonsStateElseSelected() throws Exception {
		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new CompareValues(applicationClient.isMoveTestsDownButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isMoveTestsUpButtonEnabled()));
		analyzer.analyze(new CompareValues(applicationClient.isRemoveTestsButtonEnabled()));
		
		verifyDefaultButtonsState();
	}
}
