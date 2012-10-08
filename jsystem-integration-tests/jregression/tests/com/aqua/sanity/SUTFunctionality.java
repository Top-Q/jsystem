package com.aqua.sanity;

import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.ActivateRunnerFixture;

/**
 */
public class SUTFunctionality extends JSysTestCaseOld {
	
	public SUTFunctionality(){
		super();
		setFixture(ActivateRunnerFixture.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 */
	public void testCorruptedSUT() throws Exception{
		jsystem.cleanCurrentScenario();	
		jsystem.addTest("testSetSut", "TestSut", true);
		report.step("select corrupted SUT");
		jsystem.selectSut("corruptedSUT1.xml");
		report.step("Make sure that error dialog is opened");
		jsystem.openSutEditor(true,true);
		report.step("Run runner and make sure test is working with corrupted sut");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(1);
		jsystem.checkNumberOfTestsPass(1);		
		assertEquals("corruptedSUT1.xml",getRunProperties().getProperty("SUT_NAME"));
		report.step("Select good SUT");
		jsystem.selectSut("tabbedSut.xml");
		report.step("make sure sut editor is opened");
		jsystem.openSutEditor(false,true);
		report.step("Make sure runner is working with good sut");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkNumberOfTestExecuted(2);
		jsystem.checkNumberOfTestsPass(2);		
		assertEquals("tabbedSut.xml",getRunProperties().getProperty("SUT_NAME"));
	}
	
	public void testTestThatPass()throws Exception{
		jsystem.addTest("testThatPass", "FixtureTest", true);
		jsystem.play();
		jsystem.waitForRunEnd();
		report.report("run Ended");
	}
	
	/**
	 */
	public void testSUTEditorIsWorkingWellAfterRefresh() throws Exception{
		jsystem.cleanCurrentScenario();
		jsystem.addTest("testSetSut", "TestSut", true);
		report.step("select corrupted SUT");
		jsystem.selectSut("corruptedSUT1.xml");
		report.step("Make sure that error dialog is opened");
		jsystem.openSutEditor(true,true);
		jsystem.refresh();
		jsystem.openSutEditor(true,true);
		report.step("Select good SUT");
		jsystem.selectSut("tabbedSut.xml");
		report.step("make sure sut editor is opened");
		jsystem.openSutEditor(false,true);
		report.step("Make sure runner is working with good sut");
		jsystem.refresh();
		jsystem.openSutEditor(false,true);
	}

	/**
	 */
	public void testTestParameterWhichUseSUT() throws Exception{
		report.step("clean scenario and add test");
		jsystem.cleanCurrentScenario();
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.addTest("testSetSut", "TestSut", true);
		report.step("select SUT");
		jsystem.selectSut("listSut.xml");
		report.step("validate list");
		jsystem.setTestParameter(1, "General", "Host", "127.0.0.1", true);
		report.step("replace sut");
		jsystem.selectSut("secondListSut.xml");
		try {
			jsystem.setTestParameter(2, "General", "Host", "127.0.0.1", true);
			assertTrue(false);
		}catch (Exception e){
			//should fail
		}
		jsystem.setTestParameter(2, "General", "Host", "xxx", true);
		jsystem.setTestParameter(1, "General", "Host", "yyy", true);
	}
}
