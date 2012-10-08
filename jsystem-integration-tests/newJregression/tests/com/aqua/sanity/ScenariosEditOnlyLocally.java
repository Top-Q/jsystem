package com.aqua.sanity;

import java.io.File;
import jsystem.framework.TestProperties;
import org.junit.Before;
import org.junit.Test;
import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.jsystemobjects.TestType;

/**
 * This class should test the "Edit Only Locally" feature Tests supported are:
 * 1. editOnlyLocallyUserProvederType 2.
 * 
 * Test editOnlyLocallyUserProvederType flow: 1. Create scenario
 * Scenario_Mark_Edit_Only_Locally 2. Add to the scenario the tests
 * userProviderTest and arrayTest 3. Mark the scenario as "edit only locally" 4.
 * Create scenario Scenario_Include_Scenario 5. Add to the scenario
 * Scenario_Include_Scenario the first scenario
 * (Scenario_Mark_Edit_Only_Locally) 6. Verify that it is allow to change all
 * values of the properties inside Scenario_Mark_Edit_Only_Locally 7.Verify that
 * is is not allow to change all values of the properties inside
 * Scenario_Include_Scenario
 * 
 */
public class ScenariosEditOnlyLocally extends JSysTestCase4UseExistingServer {

	private static String PRAM_NAME = "stringBean";

	private static String ARRAY_NAME = "stringBeanArray";

	private static String TAB_NAME = "General";

	private static final String SCENARIO_MARK_EDIT_ONLY_LOCALLY = "Scenario_Mark_Edit_Only_Locally";

	private static final String SCENARIO_NOT_MARK_EDIT_ONLY_LOCALLY = "Scenario_Include_Scenario";

	private static final String USER_PROVIDER_TEST_NAME = "userProviderTest";

	private static final String ARRAY_TEST_NAME = "arrayTest";

	private static final String AFTER_STRING = "After";

	private static final String TEST_CLASS = "SimpleTests";

	/**
	 * Create scenario named Scenario_Mark_Edit_Only_Locally and add to it the
	 * test userProviderTest and arrayTest From the class SimpleTests.
	 * 
	 * Mark Scenario_Mark_Edit_Only_Locally as "Edit Only Locally".
	 * 
	 * Create scenario named :Scenario_Include_Scenario add to it the scenario
	 * Scenario_Mark_Edit_Only_Locally
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		report.step("Creating " + SCENARIO_MARK_EDIT_ONLY_LOCALLY);
		scenarioClient.createScenario(SCENARIO_MARK_EDIT_ONLY_LOCALLY);
		scenarioClient.addTest(USER_PROVIDER_TEST_NAME, TEST_CLASS, 1);
		scenarioClient.addTest(ARRAY_TEST_NAME, TEST_CLASS, 1);
		scenarioClient.editOnlyLocally(0);
		applicationClient.saveScenario();

		report.step("Creating " + SCENARIO_NOT_MARK_EDIT_ONLY_LOCALLY);
		scenarioClient.createScenario(SCENARIO_NOT_MARK_EDIT_ONLY_LOCALLY);
		scenarioClient.addTest(SCENARIO_MARK_EDIT_ONLY_LOCALLY, TestType.SCENARIO.getType());
		applicationClient.saveScenario();
	}

	/**
	 * 1)Change the values of stringBean (a UserProviderParmter in the test
	 * userProviderTest) and stringBeanArray (a UserProviderParmterArray in the
	 * test userProviderTest)from scenario : Scenario_Mark_Edit_Only_Locally.
	 * 
	 * 2)Try to change the values of stringBean (a UserProviderParmter in the
	 * test userProviderTest) and stringBeanArray (a UserProviderParmterArray in
	 * the test userProviderTest)from scenario : Scenario_Include_Scenario.
	 * 
	 * 3)Check if the properties files has been modified after steep 2.
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(paramsInclude = { "" })
	public void editOnlyLocallyUserProviderType() throws Exception {

		report.report("Trying to change  " + USER_PROVIDER_TEST_NAME + " Parameters from "
				+ SCENARIO_MARK_EDIT_ONLY_LOCALLY);
		scenarioClient.openScenario(SCENARIO_MARK_EDIT_ONLY_LOCALLY);
		String[] args = { AFTER_STRING };
		scenarioClient.setTestUserProviderTestParam(1, TAB_NAME, PRAM_NAME, args);
		String[] argsString = { "Test1", "Test2", "Test3" };
		scenarioClient.setTestArrayParam(2, TAB_NAME, ARRAY_NAME, argsString);

		// collect the last modified time for the scenarios properties files in
		// order to verify at the end of the test that they didn't change
		File editOnlyLocalyScenarioPropertis = applicationClient.getScenarioFiles(SCENARIO_MARK_EDIT_ONLY_LOCALLY).get(
				1);
		File editNotOnlyLocalyScenarioPropertis = applicationClient.getScenarioFiles(
				SCENARIO_NOT_MARK_EDIT_ONLY_LOCALLY).get(1);
		long editOnlyLocalyFileModifyBefore = editOnlyLocalyScenarioPropertis.lastModified();
		long editNotOnlyLocalyFileModifyBefore = editNotOnlyLocalyScenarioPropertis.lastModified();

		report.report("Trying to change  " + USER_PROVIDER_TEST_NAME + " Parameters from ");
		scenarioClient.openScenario(SCENARIO_NOT_MARK_EDIT_ONLY_LOCALLY);
		// Can't change the stringBean and stringBeanArray from
		// Scenario_Include_Scenario because Scenario_Mark_Edit_Only_Locally is
		// marked as "Edit Only Locally"
		report.setFailToPass(true);
		scenarioClient.setTestUserProviderTestParam(2, TAB_NAME, PRAM_NAME, args);
		scenarioClient.setTestArrayParam(3, TAB_NAME, ARRAY_NAME, argsString);
		report.setFailToPass(false);

		// See if files have been modified
		if (editOnlyLocalyScenarioPropertis.lastModified() != editOnlyLocalyFileModifyBefore
				|| editNotOnlyLocalyScenarioPropertis.lastModified() != editNotOnlyLocalyFileModifyBefore) {
			report.report("The Fiels:" + editOnlyLocalyScenarioPropertis.getAbsolutePath() + " and "
					+ editNotOnlyLocalyScenarioPropertis.getAbsolutePath() + " has been modifed", false);
		}

	}

}
