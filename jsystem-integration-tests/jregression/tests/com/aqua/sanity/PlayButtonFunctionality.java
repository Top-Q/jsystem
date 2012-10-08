package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.framework.fixture.RootFixture;
import analyzers.BooleanAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * a class to test the play button enabled disabled state, 
 * and functional behavior. 
 * @author Dan
 *
 */
public class PlayButtonFunctionality extends JSysTestCaseOld {
	public PlayButtonFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * call JSystemTestCase setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * 0. clean current scenario
	 * 1. add 5 test to scenario tree and check that 5 test are checked
	 * 2. check that play button is enabled
	 * 3. delete the tests 
	 * 4. check that play button is disabled
	 */
	public void testCheckPlayButtonDisableWithAddDeleteTests()throws Exception{
		boolean isEnabled;
		applicationClient.launch();
		ScenarioUtils.createAndCleanScenario(scenarioClient, "default");
		scenarioClient.addTest("testShouldPass", "GenericBasic",5);
	
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		report.report("play is enabled");
		report.report("checking that after adding test to scenario tree " +
				"the play button is enabled");
		//expected to be true that play is enabled
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new BooleanAnalyzer(isEnabled));
		scenarioClient.deleteAllTestsFromScenarioTree();
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		report.report("checking that after deleting all test from scenario tree"+
				"the play button is disabled");
		//expected to be false that play is enabled
		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new BooleanAnalyzer(isEnabled));
	}
	
	/**
	 * 0. clean current scenario
	 * 1. add 5 test to scenario tree and check that 5 test are checked
	 * 2. check that play button is enabled
	 * 3. uncheck all 
	 * 4. check that play button is disabled
	 */
	public void testCheckPlayButtonDisableWithAddUncheckAll()throws Exception{
		boolean isEnabled;
		applicationClient.launch();
		ScenarioUtils.createAndCleanScenario(scenarioClient, scenarioClient.getCurrentScenario());
		scenarioClient.addTest("testShouldPass", "GenericBasic",5);
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		report.report("checking that after adding test to scenario tree" +
				"the paly button is enabled");
		//expected to be true that paly is enabled
		analyzer.setTestAgainstObject(true);
		analyzer.analyze(new BooleanAnalyzer(isEnabled));
		scenarioClient.unmapAll();
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		report.report("checking that after deleting all test from scenario tree"+
				"the play button is disabled");
		//expected to be false that play is enabled
		analyzer.setTestAgainstObject(false);
		analyzer.analyze(new BooleanAnalyzer(isEnabled));
	}
	/**
	 * 1. launch
	 * 2. check play button status after launch
	 */
	public void testCheckPlayStatusWhenRunnerGoesUp()throws Exception{
		boolean isEnabled;
		applicationClient.launch();
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		if(scenarioClient.getNumOfMappedTestsInScenario() > 0){
			//expected that when runner goes up the button is disabled
			report.report("check that button is disabled after lauch");
			analyzer.setTestAgainstObject(true);
			analyzer.analyze(new BooleanAnalyzer(isEnabled));
		}else if(scenarioClient.getNumOfMappedTestsInScenario() == 0){
			//expected that when runner goes up the button is disabled
			report.report("check that button is disabled after lauch");
			analyzer.setTestAgainstObject(false);
			analyzer.analyze(new BooleanAnalyzer(isEnabled));
		}
		else{
			throw new Exception("number of checkboxes checked is negative");
		}
	}
}
