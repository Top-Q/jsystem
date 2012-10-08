package com.aqua.sanity;

import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class TestListenerFunctionality extends JSysTestCaseOld {

	public TestListenerFunctionality() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
/**
 * This test is currentlly not mapped in the sanity doc
 * @throws Exception
 */
	public void testWorkingWithTestListener() throws Exception{
		jsystem.launch();
		jsystem.selectSenario("testlistener/testListener");
		Thread.sleep(5000);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkTestPass(4); // it is now 4 since changeSut isn't counted
	}
}
