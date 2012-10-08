package com.aqua.sanity;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 */
public class SUTFunctionalityAtStartup extends JSysTestCaseOld {

	public SUTFunctionalityAtStartup(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
		
	/**
	 */
	public void testJSystemPropertiesWithSUTThatDoesntExist() throws Exception{
		report.report("Set non existing sut");
		jsystem.setJSystemProperty(FrameworkOptions.USED_SUT_FILE, "kishkush.xml");
		jsystem.launch("secondListSut.xml");
		jsystem.cleanCurrentScenario();
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.selectSut("listSut.xml");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);		
	}

	/**
	 */
	public void testJSystemPropertiesWithCorruptedSUT() throws Exception{
		report.report("Set non existing sut");
		jsystem.setJSystemProperty(FrameworkOptions.USED_SUT_FILE, "corruptedSut.xml");
		jsystem.launch("secondListSut.xml");
		jsystem.cleanCurrentScenario();
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.selectSut("listSut.xml");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);		
	}

}
