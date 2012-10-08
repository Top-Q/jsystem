package com.aqua.sanity;

import java.util.Arrays;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.sanity.scenarioparam.CreateScenarioParameterizationFixture;

/**
 * Tests TestInfo tab area
 * 
 * @author golan.derazon@aquasw.com
 * 
 */
public class TestInformationPanelTestBasic extends JSysTestCaseOld {
	public TestInformationPanelTestBasic() {
		super();
		setFixture(CreateScenarioParameterizationFixture.class);
		setTearDownFixture(RootFixture.class);
	}

	/**
	 * 
	 */
	@TestProperties(name = "Check that test java doc is shown in html format")
	public void testCheckThatTestJavaDocIsInHTMLFormat() throws Exception {
		jsystem.selectSenario("default");
		report.step("clean scenario and add test which the system can't load");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testShouldPass", "GenericBasic", true);
		String javadoc = jsystem.getTestJavaDoc(1);
		assertTrue(javadoc.indexOf("should pass") > -1);
		assertTrue(javadoc.indexOf("<html>") > -1);
	}

	/**
	 */
	@TestProperties(name = "Test parameters table size for each test and scenario")
	public void testParametersTableSizes() throws Exception {
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);
		double[] test1Sizes = new double[] { 0.1, 0.1, 0.1, 0.1 };
		double[] test3Sizes = new double[] { 0.2, 0.2, 0.2, 0.2 };
		double[] test11Sizes = new double[] { 0.1, 0.4, 0.2, 0.2 };
		double[] test13Sizes = new double[] { 0.5, 0.1, 0.2, 0.3 };
		jsystem.setParameterTableSize(1, "General", test1Sizes);
		// data has to be saved in order for the settings to be saved
		jsystem.setTestParameter(1, "General", "Param2", "testParametersUpdate-0", false);
		jsystem.setParameterTableSize(3, "General", test3Sizes);
		jsystem.setTestParameter(3, "General", "Param2", "testParametersUpdate-0", false);
		jsystem.setParameterTableSize(11, "General", test11Sizes);
		jsystem.setTestParameter(11, "General", "Param2", "testParametersUpdate-0", false);
		jsystem.setParameterTableSize(13, "General", test13Sizes);
		jsystem.setTestParameter(13, "General", "Param2", "testParametersUpdate-0", false);
		double[] sizes = jsystem.getParameterTableSize(1, "General");
		report.report("comparing test1Sizes = " + stringTableSize(test1Sizes) + " with sizes = "
				+ stringTableSize(sizes));
		compareSizes(test1Sizes, sizes);
		sizes = jsystem.getParameterTableSize(3, "General");
		compareSizes(test3Sizes, sizes);
		sizes = jsystem.getParameterTableSize(11, "General");
		compareSizes(test11Sizes, sizes);
		sizes = jsystem.getParameterTableSize(13, "General");
		compareSizes(test13Sizes, sizes);

		jsystem.selectSenario("default");
		jsystem.selectSenario(CreateScenarioParameterizationFixture.PARENT_SCENARIO_NAME);

		sizes = jsystem.getParameterTableSize(1, "General");
		compareSizes(test1Sizes, sizes);
		sizes = jsystem.getParameterTableSize(3, "General");
		compareSizes(test3Sizes, sizes);
		sizes = jsystem.getParameterTableSize(11, "General");
		compareSizes(test11Sizes, sizes);
		sizes = jsystem.getParameterTableSize(13, "General");
		compareSizes(test13Sizes, sizes);
	}

	private String stringTableSize(double[] sizes) {
		StringBuilder sb = new StringBuilder();
		for (Double size : sizes) {
			sb.append(size.toString());
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Verifies that sorting of parameters by param name in parameters table
	 * works as expected
	 */
	@TestProperties(name = "Test parameters table sorting by name")
	public void testParametersSortingByName() throws Exception {
		ScenarioUtils.createAndCleanScenario(jsystem, "sorting");
		jsystem.addTest("testAllParametersWithOrder", "ParameterTest", true);

		// by default the sorting is by @params.include
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 0, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 2, true);

		// first sort changes it to a-b-c
		jsystem.sortParametersTable(1, "General", 0);
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 0, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 2, true);

		// second sort changes it to c-b-a
		jsystem.sortParametersTable(1, "General", 0);
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 2, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 0, true);

		// data has to be saved in order for the settings to be saved
		jsystem.setTestParameter(1, "General", "CliCommand", "command", false);

		// validating that sort is saved after scenario is reselcted.
		jsystem.selectSenario("default");
		sleep(2000);
		jsystem.selectSenario("sorting");
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 2, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 0, true);

	}

	/**
	 * Verifies that sorting of parameters by param type in parameters table
	 * works as expected
	 */
	@TestProperties(name = "Test parameters table sorting by param type")
	public void testParametersSortingByType() throws Exception {
		ScenarioUtils.createAndCleanScenario(jsystem, "sorting");
		jsystem.addTest("testAllParametersWithOrder", "ParameterTest", true);

		// by default the soring is by @params.include
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 0, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 2, true);

		// first sort changes it to a-b-c
		jsystem.sortParametersTable(1, "General", 2);
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 0, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 2, true);

		// second sort changes it to c-b-a
		jsystem.sortParametersTable(1, "General", 2);
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 2, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 0, true);

		// data has to be saved in order for the settings to be saved
		jsystem.setTestParameter(1, "General", "CliCommand", "command", false);

		// validating that sort is saved after scenario is reselcted.
		jsystem.selectSenario("default");
		sleep(2000);
		jsystem.selectSenario("sorting");
		jsystem.verifyParameterIndexAndEditability("EnumValue", 1, "General", 2, true);
		jsystem.verifyParameterIndexAndEditability("CliCommand", 1, "General", 1, true);
		jsystem.verifyParameterIndexAndEditability("SelectedOption", 1, "General", 0, true);

	}

	public void testDefaultValueIsAddedToCombo() throws Exception {
		jsystem.selectSenario("default");
		report.step("clean scenario and add test");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testSetSut", "TestSut", true);
		report.step("select SUT");
		jsystem.selectSut("listSut.xml");
		report.step("validate list");
		jsystem.setTestParameter(1, "General", "Host", "rrrtttuuu", true);
		jsystem.setTestParameter(1, "General", "Host", "127.0.0.1", true);
	}

	public void testCurrentValueIsAddedToCombo() throws Exception {
		jsystem.selectSenario("default");
		report.step("clean scenario and add test");
		ScenarioUtils.createAndCleanScenario(jsystem, jsystem.getCurrentScenario());
		jsystem.addTest("testSetSut", "TestSut", true);
		jsystem.addTest("testSetSut", "TestSut", true);
		report.step("select SUT");
		jsystem.selectSut("listSut.xml");
		report.step("validate list");
		jsystem.setTestParameter(1, "General", "Host", "127.0.0.1", true);
		report.step("replace sut");
		jsystem.selectSut("secondListSut.xml");
		jsystem.setTestParameter(2, "General", "Host", "xxx", true);
		jsystem.setTestParameter(1, "General", "Host", "127.0.0.1", true);
		jsystem.setTestParameter(1, "General", "Host", "yyy", true);

	}

	private void compareSizes(final double[] expected, final double[] actual) {
		report.report("Comparing sizes");
		if (expected.length != actual.length) {
			assertTrue("Actual and expected number of sizes values are not equal", false);
			return;
		}
		for (int i = 0; i < expected.length; i++) {
			analyzer.setTestAgainstObject(actual[i]);
			// Double values are not very accurate.
			analyzer.analyze(new NumberCompare(compareOption.EQUAL, expected[i], 0.01), true);
		}
	}
}
