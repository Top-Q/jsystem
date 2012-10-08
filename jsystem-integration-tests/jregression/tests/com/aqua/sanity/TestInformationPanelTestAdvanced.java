package com.aqua.sanity;

import javax.swing.JOptionPane;

import utils.ScenarioUtils;
import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.TestProperties;
import jsystem.framework.common.CommonResources;
import jsystem.runner.agent.tests.PublishTest;
import jsystem.runner.agent.tests.PublishTest.ActionType;
import jsystem.treeui.params.ParametersPanel;

import analyzers.BooleanAnalyzer;

import com.aqua.general.JSysTestCaseOld;

/**
 * Tests TestInfo tab area
 * @author golan.derazon@aquasw.com
 *
 */
public class TestInformationPanelTestAdvanced extends JSysTestCaseOld {
	public TestInformationPanelTestAdvanced(){
		super();
		setFixture(InfoPanelFixture.class);
	}
	
	/**
	 */
	@TestProperties(name = "Test file chooser parameter")
	public void testFileChooser() throws Exception {
//		PublishTest test = new PublishTest();
//		test.setActionType(ActionType.email);
//		test.setMailSubject("Dan's Subject");
//		test.setMessageHeader("Dan's Header");
//		test.setSendTo("dan@ignissoft.com");
//		test.setInitReporter(false);
//		test.publish();
		String jsystemFile = CommonResources.JSYSTEM_PROPERTIES_FILE_NAME;
//		report.showConfirmDialog("Waiting for Dan to Release the test", "Please verify that the input inserted into the file parameter is "+jsystemFile, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
		jsystem.setFileChooserParameter(1, "File",0, jsystemFile);
		report.report("File Parameter value after set is :"+jsystem.getParameterValue("Files", 1, "File"));
		sleep(10000);
		jsystem.play();
		jsystem.waitForRunEnd();
		sleep(2000);
		String fileProp = getRunProperties().getProperty("FileParameterTest_file");
		report.step("Verify that in the Run Properties there is a FileParameterTest_file=..."+jsystemFile);
		analyzer.setTestAgainstObject(fileProp.endsWith(jsystemFile));
		analyzer.analyze(new BooleanAnalyzer(true,"found Matching value for property FileParameterTest_file",fileProp + " Doesn't end with "+jsystemFile));
	}

	/**
	 */
	@TestProperties(name = "Test parameters table size for each test and scenario")
	public void testParameterDisableEnable() throws Exception {
		jsystem.deleteTest(2);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTestParameter(2, "Demo Type", "DemoType", "enabledisable",true);
		jsystem.setTestParameter(2, "General", "CompareBy", "content",true);
		jsystem.verifyParameterIndexAndEditability("Binary", 2, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("CompareBySize", 2, "General", 2, false);
		jsystem.setTestParameter(2, "General", "CompareBy", "attributes",true);
		jsystem.verifyParameterIndexAndEditability("Binary", 2, "General", 0, false);
		jsystem.verifyParameterIndexAndEditability("CompareBySize", 2, "General", 1,true);
	}

	/**
	 */
	@TestProperties(name = "Test that verifies that parameters are hidden")
	public void testParameterHide() throws Exception {
		jsystem.deleteTest(2);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTestParameter(2, "Demo Type", "DemoType", "hide",true);
		jsystem.setTestParameter(2, "General", "CompareBy", "content",true);
		jsystem.verifyParameterIndexAndEditability("Binary", 2, "General", 1, true);
		String res =jsystem.verifyParameterseExist("CompareBySize", 2, "General");
		analyzer.setTestAgainstObject(Boolean.valueOf(res));
		analyzer.analyze(new BooleanAnalyzer(false));
		jsystem.setTestParameter(2, "Demo Type", "DemoType", "enabledisable",true);
		jsystem.verifyParameterIndexAndEditability("Binary", 2, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("CompareBySize", 2, "General", 2,false);

	}

	/**
	 */
	@TestProperties(name = "Test that verifies that tabs can be changed")
	public void testParameterChangeTab() throws Exception {
		jsystem.deleteTest(2);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTestParameter(2, "General", "CompareBy", "attributes",true);
		jsystem.verifyParameterIndexAndEditability("CompareBy", 2, "Compare Type", 0, true);
		try {
			jsystem.verifyParameterIndexAndEditability("CompareBy", 2, "General", 0, true);
			assertTrue(false);
		}catch (Exception e){}
		
		jsystem.setTestParameter(2, "Compare Type", "CompareBy", "content",true);
		jsystem.verifyParameterIndexAndEditability("CompareBy", 2, "General", 0, true);
		
		try {
			jsystem.verifyParameterIndexAndEditability("CompareBy", 2, "Compare Type", 0, true);	
			assertTrue(false);
		}catch (Exception e){}

	}

	/**
	 */
	@TestProperties(name = "Test that verifies parameter description change")
	public void testParameterChangeDescription() throws Exception {
		jsystem.deleteTest(2);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTestParameter(2, "General", "CompareBy", "content",true);
		String description = jsystem.getParameterDescription("CompareBy", 2, "General");
		analyzer.setTestAgainstObject(description);
		analyzer.analyze(new FindText("their content"));
		jsystem.setTestParameter(2, "General", "CompareBy", "attributes",true);
		description = jsystem.getParameterDescription("CompareBy", 2, "Compare Type");
		analyzer.setTestAgainstObject(description);
		analyzer.analyze(new FindText("file attributes"));
	}

	/**
	 */
	@TestProperties(name = "Test that verifies tab sorting")
	public void testTabSorting() throws Exception {
		jsystem.deleteTest(2);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTabSorting(2, ParametersPanel.SORT_BY_SECTION_AB);
		int index = jsystem.getTabIndex(2, "General");
		report.step("Check that General tab number 2 (0 is first)");
		analyzer.setTestAgainstObject(index);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 2 , 0));
		jsystem.setTabSorting(2, ParametersPanel.SORT_BY_SECTION_STRING);
		report.step("Check that General tab number 1 (0 is first)");
		index = jsystem.getTabIndex(2, "General");
		analyzer.setTestAgainstObject(index);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 1 , 0));
	}

	/**
	 */
	@TestProperties(name = "Test that verifies tab selection is saved when moving between tests")
	public void testValidateTabSelectionIsSavedBetweenTests() throws Exception {
		ScenarioUtils.createAndCleanScenario(jsystem,"infopanel");
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.addTest("testCompareFolders", "AdvancedTestParametersTest", false);
		jsystem.setTestParameter(2, "Files", "File", "xxx",false);
		String tab = jsystem.getActiveTabTab(1);
		analyzer.setTestAgainstObject(tab);
		analyzer.analyze(new CompareValues("Files"));
		jsystem.setTestParameter(2, "General", "Binary", "true",true);
		tab = jsystem.getActiveTabTab(2);
		analyzer.setTestAgainstObject(tab);
		analyzer.analyze(new CompareValues("General"));
	}

}
