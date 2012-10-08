/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.treeui.TestRunner;
import jsystem.treeui.actionItems.SaveScenarioAction;

/**
 * manage the scenarios navigation. listen to navigation change and
 * support forward and backword navigation.<p>
 * Support singleton access.
 * @author guy.arieli
 *
 */
public class ScenarioNavigationManager implements ScenarioListener{
	
	static {
		manager = new ScenarioNavigationManager();
	}
	
	
	private static ScenarioNavigationManager manager;
	
	public static ScenarioNavigationManager getInstance(){
		return manager;
	}
	
	/**
	 * A list of all the scenarios in the navigation
	 */
	private List<String> scenarios = new ArrayList<String>();
	
	boolean blockEvents = false;
	
	/**
	 * Index to the current scenario in the scenarios list
	 */
	int currentScenarioIndex;
	
	
	private ScenarioNavigationManager(){
		/*
		 * Register as listener
		 */
		RunnerListenersManager.getInstance().addListener(this);
		scenarios.add(ScenariosManager.getInstance().getCurrentScenario().getName());
		currentScenarioIndex = 0;
	}
	public void init(){
		currentScenarioIndex = 0;
		scenarios = new ArrayList<String>();
	}
	@Override
	public synchronized void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		if(!changeType.equals(ScenarioChangeType.CURRENT)){
			return;
		}
		if(blockEvents){
			notify();
			return;
		}
		if(scenarios.size() > 0){
			if (scenarios.size() < currentScenarioIndex + 1){
				scenarios.clear();
			}else{
				scenarios = scenarios.subList(0, currentScenarioIndex + 1);
			}
		}
		scenarios.add(current.getName());
		currentScenarioIndex++;
	}
	/**
	 * Check if navigated forward is supported
	 * @return true if supported
	 */
	public boolean canNavitageForward(){
		return scenarios.size() > (currentScenarioIndex + 1);
	}
	
	/**
	 * Check if navigated backword is supported
	 * @return true if supported
	 */
	public boolean canNavitageBackward(){
		return currentScenarioIndex > 0;
	}
	
	/**
	 * Navigate to the next scenario
	 */
	public synchronized void navigateForward(){
		blockEvents = true;
		if(canNavitageForward()){
			currentScenarioIndex++;
			String scenarioName = scenarios.get(currentScenarioIndex);
			if(!Scenario.isScenario(File.separator+scenarioName+".xml")){
				scenarios.remove(currentScenarioIndex);
				currentScenarioIndex--;
				navigateForward();
			} else {
				//Added in order to resolve bug #246
				try {
					SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
				} catch (Exception e1) {
					Log.error(e1.getMessage());
				}
				TestRunner.treeView.tableController.loadScenario(scenarios.get(currentScenarioIndex), false);
				try {
					wait();
				} catch (InterruptedException e) {
				}

			}
		}
		blockEvents = false;
	}
	
	/**
	 * Navigate backword
	 */
	public synchronized void navigateBackward(){
		blockEvents = true;
		if(canNavitageBackward()){
			currentScenarioIndex--;
			String scenarioName = scenarios.get(currentScenarioIndex);
			if(!Scenario.isScenario(File.separator+scenarioName+".xml")){
				scenarios.remove(currentScenarioIndex);
				currentScenarioIndex++;
				navigateBackward();
			} else {
				//Added in order to resolve bug #246
				try {
					SaveScenarioAction.getInstance().saveCurrentScenario();
				} catch (Exception e1) {
					Log.error(e1.getMessage());
				}
				TestRunner.treeView.tableController.loadScenario(scenarioName, false);
				try {
					wait();
				} catch (InterruptedException e) {
				}				
			}
		}
		blockEvents = false;
	}
	@Override
	public void scenarioDirectoryChanged(File directory) {
		init();
	}
	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {
		// TODO Auto-generated method stub
		
	}
	
}
