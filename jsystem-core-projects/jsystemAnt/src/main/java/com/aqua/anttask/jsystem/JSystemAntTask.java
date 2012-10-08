/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import java.util.Iterator;
import java.util.Vector;

import jsystem.framework.distributedexecution.DistributedRunExecutor;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.utils.ReflectionUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Extension to the ant's 'ant' task for the jsystem platform.
 * The ant task was extended to support distributed execution.
 * In order to support distributed execution, the {@link #execute()} method was hooked.
 * The hook performs the following:
 * 1. Fetches scenario uuid.
 * 2. check whether scenario is associated with distributed execution parameters.
 * 3. verify that we are not running the scenario in an agent
 * 4. verify that the user did not turn off the distributed execution
 * 
 * steps 2-4 are done in the method {@link DistributedExecutionHelper.#doRemoteExecution(String)}
 * 
 * 5. If all condition are filled, fetches from the distributed execution plug-in the distributed executor 
 *    and signals it to execute the scenario remotely.
 * 6. Otherwise lets the {@link Ant} task to execute the scenario.  
 * @author goland
 */
public class JSystemAntTask extends Ant {
	/**
	 * Execution of a JSystem scenario
	 */
	public void execute() throws BuildException {
		String subScenarioUUID = "";
		try {
			subScenarioUUID = getSubScenarioUniqueId();
		}catch (Exception e) {
			ListenerstManager.getInstance().report("Failed fetching scenario unique ID. " + e.getMessage(), e);
			throw new BuildException(e);
		}
		
		boolean scenarioDisabled = ScenarioHelpers.isTestDisabled(subScenarioUUID, System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME));
		boolean isScenarioAsTest = "true".equals(ScenarioHelpers.getTestProperty(subScenarioUUID,System.getProperty(RunningProperties.CURRENT_SCENARIO_NAME),RunningProperties.SCENARIO_AS_TEST_TAG));
		// This flag indicated whether the current scenario raised the flag or not for later flag removal
		boolean wasScenarioAsTestFlagUpdated = false;

		if(scenarioDisabled && isScenarioAsTest){
    		log("Scenario execution disabled by jsystem");
    		return;
		} 
		
		if (ScenarioHelpers.wasCurrentScenarioSignaledToAbort()){
			String message = "Current scenario was signaled to abort. Skipping scenario execution."; 
			log(message);
			ListenerstManager.getInstance().report(message);
    		return;		
		}

		if (ScenarioHelpers.shouldTestBeSkipped()){
			/**
			 * If scenario is executed as part of a scenario as test context test and one of the steps(tests) failed,
			 * execution of scenario as test should not continue, thus we are skipping execution 
			 * of tests.
			 * If this is the first sub-scenario in a new "scenarioAsTest" scenario should be executed even 
			 * if last test failed, that is why I have added the scenarioAsTestStart (solves bug 248)
			 */

			String message = "One of the Parent scenarios is executed as a test and One of the previous steps (internal tests or scenarios) failed. Skipping scenario execution. "; 
			log(message);
			ListenerstManager.getInstance().report(message);
			return;
		}
		
		
		try {
			wasScenarioAsTestFlagUpdated = ScenarioHelpers.signalScenarioAsTestStart(isScenarioAsTest, subScenarioUUID);

			if (!DistributedExecutionHelper.doRemoteExecution(subScenarioUUID)){
				try{
					super.execute();
				}finally{
					executeEndScenarioOperations(wasScenarioAsTestFlagUpdated, subScenarioUUID);
				}
				
				return;
			}
			try{
				executeRemote(subScenarioUUID);
			}finally{
				executeEndScenarioOperations(wasScenarioAsTestFlagUpdated, subScenarioUUID);
			}
		}catch (Throwable e){
			ListenerstManager.getInstance().report("Failed in remote execution. " + e.getMessage(), e);
			throw new BuildException(e);
		}
	}
	
	private void executeEndScenarioOperations(boolean wasScenarioAsTestFlagUpdated, String subScenarioUuid){
		if (wasScenarioAsTestFlagUpdated){
			ScenarioHelpers.removeScenarioAsTestFlag();
		}
		ScenarioHelpers.setScenarioAbortFlag(subScenarioUuid, false);
	}
	
	private void executeRemote(String uuid) throws Exception {
		DistributedRunExecutor executor = DistributedExecutionHelper.getPopulatedExecutor(uuid);
		executor.execute();
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private String getSubScenarioUniqueId() throws IllegalAccessException, Exception {
		//hook to get task properties
		Vector<Property> properties = (Vector<Property>)ReflectionUtils.getField("properties", Ant.class).get(this);
		String parentUUID = ""; 
		String myUUID = "";
		Iterator< Property> iter = properties.iterator();
		while (iter.hasNext()){
			Property prop = iter.next();
			if (prop.getName().equals(RunningProperties.UUID_PARENT_TAG)){
				parentUUID = prop.getValue(); 
			}
			if (prop.getName().equals(RunningProperties.UUID_TAG)){
				myUUID = prop.getValue(); 
			}
		}
		String fullId = parentUUID+"."+myUUID;
		while (fullId.startsWith(".")){
			fullId = fullId.substring(1);
		}
		return fullId;
	}	
}
