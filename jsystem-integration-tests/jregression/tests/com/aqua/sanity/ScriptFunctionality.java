package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystem;

public class ScriptFunctionality extends JSysTestCaseOld {
	public ScriptFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		backupJSystemProperties();
		report.step("Set the ANT execution engine");
		jsystem.setJSystemProperty(FrameworkOptions.SCRIPT_ENGINES, "jsystem.framework.scripts.ant.AntScriptEngine");
		jsystem.launch();
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		jsystem.exit();
		restoreJSystemProperties();
	}

	public void testIdentifyAndExecuteAnt() throws Exception {
		jsystem.addTest( "build.basic", JSystem.SCRIPT, true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(1);

	}
	public void testSetAntParameter() throws Exception {
		jsystem.addTest( "build.basic", JSystem.SCRIPT, true);
		jsystem.setTestParameter(1, "General", "out.dir", "test", false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(1);
		jsystem.checkXmlTestAttribute(1, "steps", "Hello World test");

	}
	//basicReportFail
	public void testReportFail() throws Exception {
		jsystem.addTest( "build.basicReportFail", JSystem.SCRIPT, true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(0);
	}

	public void testExecutionFail() throws Exception {
		jsystem.addTest( "build.basicExecutionFail", JSystem.SCRIPT, true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestsPass(0);
	}
}
