/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.ArrayList;
import java.util.TreeMap;

import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntIfElse;
import jsystem.framework.scenario.flow_control.AntSwitchDefault;

/**
 * This is a Test Container for handling multi-selected Tests with Ordering
 * 
 * @author Nizan Freedman
 * 
 */
public class TestsContainer {

	private TreeMap<String, JTest> map, tmpMap;

	private Object curIndex;

	private JTest test;

	private boolean allMapped;

	private boolean hasRoot;

	private boolean hasScen;
	
	private boolean hasFlowControl;
	
	private boolean hasElse; // not Else if!
	
	private boolean hasTestScenario;
	
	private boolean hasSwitchDefault; 

	private ArrayList<Scenario> allScenarios;
	
	public TestsContainer() {
		map = new TreeMap<String, JTest>();
		tmpMap = null;
		allMapped = true;
		hasRoot = false;
		hasScen = false;
		hasSwitchDefault = false;
		hasElse = false;
		allScenarios = new ArrayList<Scenario>();
		hasFlowControl = false;
	}

	/**
	 * return Array of all selected tests.
	 * 
	 * @return array of tests/scenarios
	 */
	public JTest[] getTests() {
		Object[] objects = map.values().toArray();
		JTest[] tests = new JTest[objects.length];

		for (int i = 0; i < objects.length; i++) {
			tests[i] = (JTest) objects[i];
		}
		return tests;
	}

	/**
	 * assume the tests is always belong to one scenario and return it.
	 * TODO: tests belongs to JTestContainer, which are JTest (maybe in the future they will have more in common...)
	 * @return the scenario
	 */
	public JTestContainer getContainerRoot() {
		if (!isEmpty()) {
			JTest firstElement = map.get(map.firstKey());
			if (firstElement instanceof JTestContainer && !ScenarioHelpers.isScenarioAsTestAndNotRoot(firstElement)) {
				return (JTestContainer)firstElement;
			}
			return firstElement.getParent();
		}
		return null;
	}

	public JTest getFirst() {
		if(!isEmpty()) {
			return map.get(map.firstKey());
		} else {
			return null;
		}
	}
	
	/**
	 * clears the container
	 * 
	 */

	public void clear() {
		map.clear();
	}

	/**
	 * check if a test is in the container
	 * 
	 * @param test
	 *            the test to check
	 * @return true if test is in the container
	 */
	public boolean contains(JTest test) {
		return map.containsValue(test);
	}

	/**
	 * 
	 * @return true if container is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * adds a test to the container
	 * 
	 * @param test =
	 *            JTest to add
	 * @param index =
	 *            index in test list
	 */
	public void addTest(JTest test) {
		if(test instanceof AntFlowControl) //Ant flow control has no UUID
			map.put(((AntFlowControl) test).getFlowFullUUID(), test);
		else
			map.put(test.getFullUUID(), test);
		if (test.isDisable())
			allMapped = false;
		if (test instanceof Scenario) {
			if (((Scenario)test).isScenarioAsTest() && !((Scenario)test).isRoot()){
				hasTestScenario = true;
			}else{
				hasScen = true;
				allScenarios.add((Scenario)test);
			}
			if (((Scenario) test).isRoot()){
				hasRoot = true;
			}
		} else if (test instanceof AntFlowControl) {
			hasFlowControl = true;
			if (test instanceof AntSwitchDefault){
				hasSwitchDefault = true;
			}else if (test instanceof AntIfElse){
				hasElse = true;
			}
		}
	}

	/**
	 * removes a test from the container
	 * 
	 * @param index =
	 *            index of test to remove
	 */
	public void removeTest(int index) {
		JTest test = map.remove(index);
		if (test instanceof Scenario){
			allScenarios.remove((Scenario)test);
		}
	}

	/**
	 * 
	 * @return num of tests in container
	 */
	public int getNumOfTests() {
		return map.size();
	}

	/**
	 * initializes a new run over the tests in the container
	 * 
	 * 
	 */
	public void initRun() {
		tmpMap = new TreeMap<String, JTest>(map);
	}

	/**
	 * 
	 * @return true if the current run on Test has more tests
	 */
	public boolean hasMore() {
		return (!tmpMap.isEmpty());
	}

	/**
	 * 
	 * @return the next lowest index Test
	 */
	public JTest getNext() {
		if (tmpMap == null)
			initRun();

		if (hasMore()) {
			curIndex = tmpMap.firstKey();
			test = tmpMap.get(curIndex);
			tmpMap.remove(curIndex);
			return test;
		}
		return null;
	}

	/**
	 * 
	 * @return the next highest index Test
	 */
	public JTest getLast() {
		if (tmpMap == null)
			initRun();

		if (hasMore()) {
			curIndex = tmpMap.lastKey();
			test = tmpMap.get(curIndex);
			tmpMap.remove(curIndex);
			return test;
		}
		return null;
	}

	/**
	 * cheks if all the tests are mapped
	 * 
	 * @return true if mapped
	 */
	public boolean isMapped() {
		return allMapped;
	}

	/**
	 * sets enable/disable to all tests in container
	 * 
	 * @param enable
	 *            to enable
	 */
	public void setEnabled(boolean enable) {
		this.initRun();
		while (hasMore())
			getNext().setDisable(!enable);
		this.initRun();
	}

	/**
	 * checks if the root is in the container
	 * 
	 * @return true if root is in the container
	 */
	public boolean hasRoot() {
		return hasRoot;
	}

	/**
	 * checks if there is a scenario in the container
	 * 
	 * @return true if there is a scenario in the container
	 */
	public boolean hasScenario() {
		return hasScen;
	}
	
	/**
	 * checks if there is a Test Scenario in the container
	 * 
	 * @return true if there is a scenario in the container
	 */
	public boolean hasTestScenario() {
		return hasTestScenario;
	}
	
	/**
	 * get all scenario tests in current container
	 * @return
	 */
	public ArrayList<Scenario> getAllScenarios(){
		return allScenarios;
	}

	public boolean hasFlowControl() {
		return hasFlowControl;
	}

	/**
	 * checks if container has "Else" , not "Else if"!
	 * @return
	 */
	public boolean hasElse() {
		return hasElse;
	}

	public boolean hasSwitchDefault() {
		return hasSwitchDefault;
	}
}
