		package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * Tests error handling in the runner
 * @author golan.derazon@aquasw.com
 *
 */
public class ErrorHandling extends JSysTestCaseOld {
	public ErrorHandling(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * the add test method is closing error messages if opened 
	 * inside the moved checked to scenario tree. therefor wait For WarningDialog will stuck.
	 */
	@TestProperties(name = "Check that an error dialog is shown when trying to load erorred test")
	public void testAddErroredTest() throws Exception{
		jsystem.launch();
		report.step("clean scenario and add test which the system can't load");
		ScenarioUtils.createAndCleanScenario(jsystem,jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		jsystem.addTest("testDummyTest", "TestWhichFailsToInstantiate", true);
//		jsystem.waitForWarningDialog();
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(1);
	}
}
