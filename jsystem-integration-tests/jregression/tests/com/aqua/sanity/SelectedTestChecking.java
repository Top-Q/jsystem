package com.aqua.sanity;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.treeui.error.ErrorPanel;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

/**
 * 
 * @author Dan
 *A Class for testing checkboxes clean issues
 *all tests checking the functionality of the test/scenario tree checkboxes
 *goes in here
 */

public class SelectedTestChecking extends JSysTestCaseOld {
	
	public SelectedTestChecking(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	/**
	 * this function tests the number of tests checked in tree, 
	 * against the static variable indicating this amount
	 * @return number of checked check boxes
	 * @throws Exception
	 */
	@TestProperties(name="test checkbox cleanup in tests tree")
	public long testNumOfChkboxChecked()throws Exception{
		
		jsystem.launch();
		report.step("");//add content
		jsystem.cleanCurrentScenario();
		jsystem.checkTestInTestsTree("testReportFor10Sec", "GenericBasic",true);
		jsystem.checkTestInTestsTree("testShouldPass", "GenericBasic", true);
		jsystem.checkTestInTestsTree("testDateParameter","AdvancedTestParametersTest", true);
		jsystem.checkTestInTestsTree("testFileParameter","AdvancedTestParametersTest", true);
		jsystem.checkTestInTestsTree("testCompareFolders","AdvancedTestParametersTest", false);
		jsystem.checkTestInTestsTree("testAllParameters","ParameterTest", true);
		
		
		long numOfchkBoxChecked = jsystem.getNumOfchkBoxChekced();
		jsystem.analyze(new NumberCompare(compareOption.EQUAL, 6, 0));
		return numOfchkBoxChecked;
	}
	/**
	 * checks if pushing the add button on illegal scenarios will not
	 * clear the checked boxes, and will clear if selection is legal.
	 * checks test adding, scenario adding, fixture adding.
	 * @throws Exception
	 */
	public void testCheckRemoveFuctionality() throws Exception{
		jsystem.launch();
		report.step("");//add content
		jsystem.cleanCurrentScenario();
		
		jsystem.checkTestInTestsTree("testReportFor10Sec", "GenericBasic", true);
		jsystem.checkTestInTestsTree("testShouldPass", "GenericBasic", true);
	
//		jsystem.checkTestInTestsTree("regression.generic.BasicFixture", "generic");
//		jsystem.checkTestInTestsTree("regression.generic.AdvanceFixture", "generic");
		
//		jsystem.checkTestInTestsTree("default",null);
		
		int numChecked = jsystem.moveCheckedToScenarioTree();
		Thread.sleep(2000);
		if(numChecked > 0){
			jsystem.analyze(new NumberCompare(compareOption.EQUAL, numChecked, 0));
		}
	}
}
