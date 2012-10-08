package com.aqua.stability;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.fixtures.CreateStabilityScenarioFixture;

/**
 * 
 * @author Dan Hirsch
 *
 */
public class ReporterStabilityTest extends JSysTestCase4UseExistingServer {
	private RunMode runMode;
	public ReporterStabilityTest() {
		super();
		setFixture(CreateStabilityScenarioFixture.class);
	}
	
	public void setUp() throws Exception{
		super.setUp();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void heavyRportingTest() throws Exception{
		/*set the run mode for the remote runner */
		applicationClient.setJSystemProperty(FrameworkOptions.RUN_MODE, runMode.toString());
		applicationClient.play(true);
		//check that the expected number of tests passed.
		report.step("check that 2000 tests actually pass as expected");
		reporterClient.checkNumberOfTestsPass(2000);
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}	
}
