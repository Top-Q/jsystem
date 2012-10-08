package com.aqua.sanity;

import org.junit.Test;

import com.aqua.analyzers.BooleanAnalyzer;
import com.aqua.base.JSysTestCase4UseExistingServer;

public class PlayButtonFunctionality extends JSysTestCase4UseExistingServer {
	public PlayButtonFunctionality() {
		super();
	}
	
	
	
	/**
	 * 0. clean current scenario
	 * 1. add 5 test to scenario tree and check that 5 test are checked
	 * 2. check that play button is enabled
	 * 3. delete the tests 
	 * 4. check that play button is disabled
	 * @throws Exception 
	 */
	@Test
	public void chechPlayButtonEnabledDisabled() throws Exception{
		boolean isEnabled;
		applicationClient.launch();
		scenarioClient.cleanScenario("default");
		scenarioClient.addTest("testThatPass", "SimpleTests",5);
	
		isEnabled = applicationClient.checkIfPlayIsEnabled();
		report.report("play is enabled");
		report.report("checking that after adding test to scenario tree" +
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
}
