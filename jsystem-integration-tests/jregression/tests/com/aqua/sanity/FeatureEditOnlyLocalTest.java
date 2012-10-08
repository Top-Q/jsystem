package com.aqua.sanity;

import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystem;

public class FeatureEditOnlyLocalTest extends JSysTestCaseOld{
	
	public FeatureEditOnlyLocalTest() {
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * 1. Create a Scenario 2. Add tests to it 3. Mark as edit only local
	 * 4. Clean the scenario. 5.Save it 6.Make another scenario
	 * 7. Add the first scenario to this scenario 8. check if its uneditable
	 */
	@TestProperties(name = "Test local only edit feature basic operation.")
	public void testEditOnlyLocalFeatureBasicOperation() throws Exception {

		final String subEditOnlyLocalScenarioName = "Local only editable scenario";
		final String rootScenarioName = "Root Scenario";
		
		final String ClassNameOfTestToAdd = "ParameterTest";
		final String testNameToAdd = "testAllParameters";
		
		applicationClient.launch();
		
		report.report("Testing edit only locally feature basic operation.");
		
		report.report("Creating a scenario named: " + subEditOnlyLocalScenarioName);
		ScenarioUtils.createAndCleanScenario(scenarioClient, subEditOnlyLocalScenarioName);
		
		report.step("Adding two tests to it.");
		for (int i = 0; i < 2; i++){
			scenarioClient.addTest(testNameToAdd, ClassNameOfTestToAdd);
		}
		
		report.step("Marking scenario as edit only locally.");
		scenarioClient.markScenarioAsEditLocalOnly();
		
		report.step("Cheking if scenario is marked as edit only locally.");
		Boolean isEditOnlyLocally = scenarioClient.isScenarioMarkedAsEditOnlyLocally();
		if(!isEditOnlyLocally){
			report.report("The scenario is not marked as edit only locally unlike we have expected.", false);
		}
		
		report.step("Save the scenario");
		applicationClient.saveScenario();
		
		report.step("Create Root Scenario for holding the local only editable scenario.");
		ScenarioUtils.createAndCleanScenario(scenarioClient,rootScenarioName);
		report.step("Save the scenario");
		applicationClient.saveScenario();
		
		scenarioClient.selectSenario(rootScenarioName);
		scenarioClient.addTest(subEditOnlyLocalScenarioName, JSystem.SCENARIO);
		Boolean isEditable = scenarioClient.isSubScenarioFieldsAreEditable(1);
		if(isEditable) {
			report.report("The test shouldnt be editable, it should have been edit only locally.", false);
		}
		else{
			report.report("The test is not editable as it should be, it was set to be edit only locally.");
		}
		Thread.sleep(1000);
	
	}
	
	/**
	 * 1. Create scenario : Edit only locally scenario 2. adding tests with parameters to its 3.mark it as edit only locally
	 * 4. create scenario : Public Editable Scenario 5. adding tests with parameters to it 6. add Edit local only scenario as its sub scenario
	 * 7. create Father scenario that holds Public Editable Scenario as sub scenario
	 * 8. check if the user can set parameters only in the Public edits Scenario and not in the Edit local only scenario
	 */
	@TestProperties(name = "Test local only edit feature in hirarchy, where the edit local only scenario is in the bottom.")
	public void testEditOnlyLocalLastInHiratrchyEditLocalOnlyBottom() throws Exception {

		final String topHirarchyScenario = "Father scenario";
		final String middleHirarchyScenario = "Public editable scenario";
		final String BottomHirarchyScenario = "Edit only locally scenario";
		
		final String ClassNameOfTestToAdd = "ParameterTest";
		final String testNameToAdd = "testAllParameters";
		
		applicationClient.launch();
		
		report.report("Testing edit only locally feature within scenario hirarchy.", 
				"Testing the edit only locally feature in hirarchy, we have a father scenario holding a public editable scenario " +
				"holding the edit only locally scenario. in this state the user can only edit the public editable scenario but not the edit only locally scenario.", true);
		
		report.step("Creating a scenario named: " + BottomHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, BottomHirarchyScenario);
		
		report.step("Adding two tests to it.");
		for (int i = 0; i < 2; i++){
			scenarioClient.addTest(testNameToAdd, ClassNameOfTestToAdd);
		}
		
		report.step("Marking scenario as edit only locally.");
		scenarioClient.markScenarioAsEditLocalOnly();
		
		report.step("Creating a scenario named: " + middleHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, middleHirarchyScenario);
		
		report.step("Adding two tests to it.");
		for (int i = 0; i < 2; i++){
			scenarioClient.addTest(testNameToAdd, ClassNameOfTestToAdd);
		}
		
		scenarioClient.addTest(BottomHirarchyScenario, JSystem.SCENARIO);
		
		report.step("Creating a scenario named: " + topHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, topHirarchyScenario);
		
		scenarioClient.addTest(middleHirarchyScenario, JSystem.SCENARIO);
		

		Boolean isMiddleEditable = scenarioClient.isSubScenarioFieldsAreEditable(1);

		Boolean isBottomEditable = scenarioClient.isSubSubScenarioFieldsAreEditable(0, 2);

		if(isMiddleEditable == true && isBottomEditable == false) {
			report.report("The public editable is editable and the only locally editable is not editable.", true);
		}
		else if (isMiddleEditable == true && isBottomEditable == true) {
			report.report("The public editable is editable but the only locally editable is also editable.", false);
		}
		else if (isMiddleEditable == false && isBottomEditable == true) {
			report.report("the public editable is not editable and the only locally editable id editable.", false);
		}
		else if (isMiddleEditable == false && isBottomEditable == false) {
			report.report("the only locally editable is not editable but the public editable is also not editable.", false);
		}
		Thread.sleep(1000);
	}
	
	/**
	 * 1. Create scenario : Public Editable Scenario 2. adding tests with parameters to its 
	 * 4. create scenario :  5. Edit only locally scenario 6. adding tests with parameters to it 7. mark it as edit only locally
	 * 6. add Public Editable Scenario as its sub scenario 
	 * 8. create Father scenario that holds dit only locally scenario as sub scenario
	 * 9. check if is unable to set parameters in the Edit only locally scenario and also in the Public editable scenario.
	 */
	@TestProperties(name = "Test local only edit feature in hirarchy, where the edit local only scenario is in the middle.")
	public void testEditOnlyLocalLastInHiratrchyEditLocalOnlyMiddl() throws Exception {

		final String topHirarchyScenario = "Father scenario";
		final String middleHirarchyScenario = "Edit only locally scenario";
		final String BottomHirarchyScenario = "Public editable scenario";
		
		final String ClassNameOfTestToAdd = "ParameterTest";
		final String testNameToAdd = "testAllParameters";
		
		applicationClient.launch();
		
		report.report("Testing edit only locally feature within scenario hirarchy.", 
				"Testing the edit only locally feature in hirarchy, we have a father scenario holding a public editable scenario " +
				"holding the edit only locally scenario. in this state the user can only edit the public editable scenario but not the edit only locally scenario.", true);
		
		report.step("Creating a scenario named: " + BottomHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, BottomHirarchyScenario);
		
		report.step("Adding two tests to it.");
		for (int i = 0; i < 2; i++){
			scenarioClient.addTest(testNameToAdd, ClassNameOfTestToAdd);
		}
		
		report.step("Creating a scenario named: " + middleHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, middleHirarchyScenario);
		
		report.step("Adding two tests to it.");
		for (int i = 0; i < 2; i++){
			scenarioClient.addTest(testNameToAdd, ClassNameOfTestToAdd);
		}
		
		report.step("Marking scenario as edit only locally.");
		scenarioClient.markScenarioAsEditLocalOnly();
		
		scenarioClient.addTest(BottomHirarchyScenario, JSystem.SCENARIO);
		
		report.step("Creating a scenario named: " + topHirarchyScenario);
		ScenarioUtils.createAndCleanScenario(scenarioClient, topHirarchyScenario);
		
		scenarioClient.addTest(middleHirarchyScenario, JSystem.SCENARIO);
		

		Boolean isMiddleEditable = scenarioClient.isSubScenarioFieldsAreEditable(1);

		Boolean isBottomEditable = scenarioClient.isSubSubScenarioFieldsAreEditable(0, 2);

		if(isMiddleEditable == true && isBottomEditable == false) {
			report.report("The public editable is not editable but the only locally editable is editable.", false);
		}
		else if (isMiddleEditable == true && isBottomEditable == true) {
			report.report("The public editable is editable and the only locally editable is also editable.", false);
		}
		else if (isMiddleEditable == false && isBottomEditable == true) {
			report.report("the only locally editable is not editable but the public editable is editable.", false);
		}
		else if (isMiddleEditable == false && isBottomEditable == false) {
			report.report("the only locally editable is not editable and the public editable is also not editable as should be.", true);
		}
		Thread.sleep(1000);
	}
	
}