/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.scenario.flow_control.AntIfElseIf;
import jsystem.framework.scenario.flow_control.AntSwitch;
import jsystem.framework.scenario.flow_control.AntSwitchCase;

/**
    Utility class for operations on runner's scenario model.
    In several places in the class a code like this is written:
    
	if (test.getParent().getParent()!= null){
		test.getParent().getParent().update();
	}else {
		test.getParent().update();
	}
	
	The reason for this kind of update is when a parameter of a test is updated,
    as part of the scenario parameterization feature, it's value is saved in the 
    in the parent of the test's scenario, thus the parent of test's scenario should be
    saved. please also read <code>Scenario</code> documentation.
 */
public class MultipleScenarioOps {

	public static void updateDocumentation(JTest test, String doc) throws Exception {
		test.setDocumentation(doc);
		ScenarioHelpers.setDirtyFlag();
	}
	
	/**
	 * remove test in all scenario instances
	 * 
	 * @param root
	 *            the point of start looking scenarioParentName
	 * @param scenarioParentName
	 *            where the specific test located
	 * @param indexOfTestToMove
	 *            specific index of test to move
	 * @throws Exception
	 */
	public static void removeTests(Scenario rootScenario, Scenario scenarioToRemoveFrom, 
			ArrayList<Integer> containersPath, int indexToRemove) throws Exception {
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToRemoveFrom.getName())) {
				
				JTestContainer pointer = scenario;
				if (!containersPath.isEmpty()) {
					int index = containersPath.get(0);
					
					Iterator<Integer> iter = containersPath.iterator();
					while (iter.hasNext()) {
						index = iter.next();
						pointer = (JTestContainer)pointer.rootTests.get(index);
					}
				}
				JTest testToRemove = pointer.getTestFromRoot(indexToRemove);
				pointer.removeTest(testToRemove);			
			}
		}
	}
	public static void moveTestUp(Scenario rootScenario, Scenario scenarioToMoveIn, 
			ArrayList<Integer> containersPath, int indexToRemove) throws Exception {
		
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToMoveIn.getName())) {
				
				JTestContainer pointer = scenario;
				if (!containersPath.isEmpty()) {
					int index = containersPath.get(0);
					
					Iterator<Integer> iter = containersPath.iterator();
					while (iter.hasNext()) {
						index = iter.next();
						pointer = (JTestContainer)pointer.rootTests.get(index);
					}
				}
				JTest testToMove = pointer.getTestFromRoot(indexToRemove);
				pointer.moveUp(testToMove);
			}
		}
	}	

	public static void moveTestDown(Scenario rootScenario, Scenario scenarioToMoveIn, 
			ArrayList<Integer> containersPath, int indexToRemove) throws Exception {
		
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToMoveIn.getName())) {
				
				JTestContainer pointer = scenario;
				if (!containersPath.isEmpty()) {
					int index = containersPath.get(0);
					
					Iterator<Integer> iter = containersPath.iterator();
					while (iter.hasNext()) {
						index = iter.next();
						pointer = (JTestContainer)pointer.rootTests.get(index);
					}
				}
				JTest testToMove = pointer.getTestFromRoot(indexToRemove);
				pointer.moveDown(testToMove);
			}
		}
	}
	
	public static void moveTestToBottom(Scenario rootScenario, Scenario scenarioToMoveIn, 
			ArrayList<Integer> containersPath, int indexToRemove) throws Exception {
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToMoveIn.getName())) {
				
				JTestContainer pointer = scenario;
				if (!containersPath.isEmpty()) {
					int index = containersPath.get(0);
					
					Iterator<Integer> iter = containersPath.iterator();
					while (iter.hasNext()) {
						index = iter.next();
						pointer = (JTestContainer)pointer.rootTests.get(index);
					}
				}
				JTest testToMove = pointer.getTestFromRoot(indexToRemove);
				pointer.moveToBottom(testToMove);
			}
		}
	}
	
	public static void moveTestToTop(Scenario rootScenario, Scenario scenarioToMoveIn, 
			ArrayList<Integer> containersPath, int indexToRemove) throws Exception {
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToMoveIn.getName())) {
				
				JTestContainer pointer = scenario;
				if (!containersPath.isEmpty()) {
					int index = containersPath.get(0);
					
					Iterator<Integer> iter = containersPath.iterator();
					while (iter.hasNext()) {
						index = iter.next();
						pointer = (JTestContainer)pointer.rootTests.get(index);
					}
				}
				JTest testToMove = pointer.getTestFromRoot(indexToRemove);
				pointer.moveToTop(testToMove);
			}
		}
	}
	
	/**
	 * checking / unchecking all tests from scenario as root
	 * 
	 * @param scneario
	 *            root scneario
	 * @param check
	 *            true means check
	 * @throws Exception
	 */
	public static void checkAllNodes(List<JTest>listOfTestElements, boolean check) throws Exception {
		HashSet<JTestContainer> scenariosToSave = new HashSet<JTestContainer>();
		for (JTest test:listOfTestElements){
			test.setDisable(!check);
			if (test instanceof JTestContainer){
				scenariosToSave.add(((JTestContainer)test));
				if (test.getParent() != null){
					scenariosToSave.add((JTestContainer)test.getParent());
				}
				List<Scenario> list = ScenarioHelpers.getScenarios((JTestContainer)test);
				for (Scenario s1:list){
					scenariosToSave.add(s1);
				}
			}else {
				JTestContainer s = (JTestContainer)test.getParent();
				if (s.getParent() != null){
					scenariosToSave.add((JTestContainer)s.getParent());
				}else {
					scenariosToSave.add(s);
				}
			}
		}
		ScenarioHelpers.setDirtyFlag();
	}



	/**
	 * change comment to scenario
	 * 
	 * @param scenario
	 *            scenario to comment
	 * @param comment
	 *            comment text
	 */
	public static void editComment(JTest test,String comment) throws Exception{
		test.setTestComment(comment);
		ScenarioHelpers.setDirtyFlag();
	}

	/**
	 * check specific test in all scneario`s instances
	 * 
	 * @param rootScenario
	 * @param scenarioName
	 * @param index
	 * @param isCheck
	 * @throws Exception
	 */
	public static void check(JTestContainer rootScenario, String scenarioName, int index, boolean isCheck) throws Exception {
		for (int i = 0; i < rootScenario.getRootTests().size(); i++) {
			JTest jTest = (JTest) rootScenario.getRootTests().get(i);
			if (jTest instanceof JTestContainer) {
				if (((JTestContainer) jTest).getName().equals(scenarioName)) {
					JTest t = (JTest) ((JTestContainer) jTest).getTestFromRoot(index);
					t.setDisable(isCheck);					
				}
				check((JTestContainer) jTest, scenarioName, index, isCheck);
			}
		}
	}
	
	/**
	 * add test to all of scenario`s instances
	 * TODO: new doc
	 * 
	 * @param rootScenario
	 * @param scenarioParentName
	 * @param indexToInsert
	 *            where to locate new test
	 * @param testToAdd
	 *            the test to add
	 * @param currentTestIndex
	 */
	public static ArrayList<JTest> addTestToScenario(Scenario rootScenario, Scenario scenarioToAddTo, 
			ArrayList<Integer> containersPath, JTest testToAdd, int currentTestIndex) throws Exception {
		
		ArrayList<JTest> testsToReturn = new ArrayList<JTest>();
		JTest cloneTest;
		
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		
		for (Scenario scenario: allScenarios) {
			if (scenario.getName().equals(scenarioToAddTo.getName())) {
				cloneTest = testToAdd.cloneTest();
				cloneTest.setUUID(testToAdd.getUUID());
				testsToReturn.add(cloneTest);
				JTestContainer pointer = scenario;
				int finalIndexToInsert = containersPath.get(0);
				
				Iterator<Integer> iter = containersPath.iterator();
				while (iter.hasNext()) {
					finalIndexToInsert = iter.next();
					if (iter.hasNext()) {
						pointer = (JTestContainer)pointer.rootTests.get(finalIndexToInsert);
					}
				}
				pointer.addTest(cloneTest, finalIndexToInsert);
			}
		}
		rootScenario.updateAllTests();		
		return testsToReturn;
	}
	
	/**
	 * Add tests which were copied from the scenario
	 * The new test\container will be copied with all it sub tests\containers and updated parameters
	 * @param rootScenario
	 * @param scenarioParentName
	 * @param indexToInsert
	 *            where to locate new test
	 * @param testToAdd
	 *            the test to add
	 * @param currentTestIndex
	 */
	public static ArrayList<JTest> addTestWithParamsToScenario(Scenario rootScenario, Scenario scenarioToAddTo, 
			ArrayList<Integer> containersPath, JTest testToAdd, int currentTestIndex) throws Exception {
		
		//Fixture copying is not supported
		if(testToAdd instanceof Fixture){
			return null;
		}
		ArrayList<JTest> testsToReturn = new ArrayList<JTest>();
		JTest cloneTest;
		List<Scenario> allScenarios = ScenarioHelpers.getAllScenarios(rootScenario);
		allScenarios.add(rootScenario);
		String uuid = JTestContainer.getRandomUUID();
		//Go over all the Scenarios
		for (Scenario scenario: allScenarios){
			//Find the relevant scenario to add to
			if (scenario.getName().equals(scenarioToAddTo.getName())){
				//Get all parameters from original test/container
				Parameter[] p = testToAdd.getParameters();
				
				//Build the new test to paste and set its parameters
				cloneTest = testToAdd.cloneTest();
				cloneTest.setUUID(uuid);
				
				//Following will be set for Test or Scenario only
				cloneWithInternalFlags(testToAdd, cloneTest);
				
				if(testToAdd instanceof JTestContainer){//Clone all contained tests/containers also
					cloneContainersTestRecursivly((JTestContainer) cloneTest, (JTestContainer) testToAdd, (testToAdd instanceof Scenario));
				}
				testsToReturn.add(cloneTest);
				JTestContainer pointer = scenario;
				JTestContainer lastPointer = scenario;
				int finalIndexToInsert = containersPath.get(0);
				
				//Find the right place to add the new test in the tree
				Iterator<Integer> iter = containersPath.iterator();
				while (iter.hasNext()) {
					int next = iter.next();
					/*
					 * Special cases when a Paste should add the row ASSIDES the selected row and not UNDER it since its illegal to Paste AFTER
					 * Change Paste to Past under
					 * 1) SwitchCase paste in SwitchCase
					 * 2) IfElseIf paste in IfElseIf
					 * 3) Switch paste in Switch
					 *  
					 */
					if((((cloneTest instanceof AntSwitchCase) && (pointer instanceof AntSwitchCase))
							||((cloneTest instanceof AntIfElseIf) && (pointer instanceof AntIfElseIf))
								||((cloneTest instanceof AntSwitch) && (pointer instanceof AntSwitch)))
									&& (!(iter.hasNext()))){
						pointer = lastPointer;
						break;
					}else{
						lastPointer = pointer;
					}
					finalIndexToInsert = next;
					if (iter.hasNext()) {
						pointer = (JTestContainer)pointer.rootTests.get(finalIndexToInsert);
					}
				}
				//Add test to current pointer
				pointer.addTest(cloneTest, finalIndexToInsert, !(cloneTest instanceof Scenario));
				setParametersValues(rootScenario.getName(),cloneTest, p, testToAdd.getFullUUID());
			}
		}
		rootScenario.updateAllTests();		
		return testsToReturn;
	}
	
	/**
	 * Copy a container with recursive copying of all internal tests
	 * 
	 * @param dest
	 * @param src
	 * @param containedInSubScenario
	 * @throws Exception
	 */
	private static void cloneContainersTestRecursivly(JTestContainer dest, JTestContainer src, boolean containedInSubScenario)throws Exception{
		dest.rootTests = new Vector<JTest>();
		String rootScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		//Go over all root sub tests/containers
		for(int i = 0 ; i < src.getRootTests().size() ; i++){
			
			JTest rootTest = src.getRootTests().get(i);

			JTest cloneTest = rootTest.cloneTest();
			if(containedInSubScenario){ //in case that the cloned test/container is contained in a sub scenario, we shouldn't change the UUID
				cloneTest.setUUID(rootTest.getUUID());
			}
			else{
				cloneTest.setUUID(JTestContainer.getRandomUUID());
			}
			
			//Get all parameters from original test/container
			Parameter[] p = rootTest.getParameters();
			
			//Following will be set for Test or Scenario only
			cloneWithInternalFlags(rootTest, cloneTest);
			
			if(rootTest instanceof JTestContainer){//Clone the contained tests/containers of the current node also
				cloneContainersTestRecursivly((JTestContainer)cloneTest, (JTestContainer)rootTest, ((rootTest instanceof Scenario) || (containedInSubScenario)));
			}
			
			//Add test to current pointer
			dest.addTest(cloneTest, -2, !(cloneTest instanceof Scenario));
			
			setParametersValues(rootScenario,cloneTest, p, rootTest.getFullUUID());
		}
	}
	
	private static void setParametersValues(String rootScenario,JTest clonedTest, Parameter[] srcParameters, String srcFullUuid) throws Exception{
		Parameter [] clonedParameters = ParameterUtils.clone(srcParameters);
		
		if(!(clonedTest instanceof Scenario)){
			clonedTest.load();
			clonedTest.loadParametersAndValues();
			
			for (Parameter param : clonedParameters){
				param.setDirty();
			}
			
			clonedTest.setParameters(clonedParameters);
		
			Parameter [] clonedTestParameters = clonedTest.getParameters();
			for (Parameter param : clonedTestParameters){
				param.signalToSave(); // for file saving
			}
		}
	}
	
	/**
	 * Set all internal flags not copied by the JTest.clone
	 * 
	 * @param src The source test to copy from
	 * @param dest	The cloned test to set parameter for
	 */
	private static void cloneWithInternalFlags(JTest src, JTest dest){
		//Following will be set for Test or Scenario only
		if((!(src instanceof JTestContainer)) || (src instanceof Scenario)){
			if(dest instanceof Scenario){
				((Scenario)dest).setScenarioAsTest(((Scenario)src).isScenarioAsTest());
			}
		}	
	}
	
	public static void updateTest(JTest test, Parameter[] params, boolean updateRecursively) throws Exception {
		if (test == null) {
			return;
		}
		test.setParameters(params, updateRecursively);
		ScenarioHelpers.setDirtyFlag();
	}
}
