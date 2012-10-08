/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.distributedexecution;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.DistributedExecutionParameter;


/**
 * Implementation of this interface are used by the runner 
 * to gather distributed execution parameters from the user
 * and to perform distributed execution.<br>
 * The plug-in is used by the system in two JVMs:<br>
 * 1. runner JVM. When user selects a test/scenario, the runner code
 *    calls the method {@link #getDistributedExecutionParameters()}. The method should 
 *    return an array of {@link DistributedExecutionParameter}. These parameters 
 *    are shown to the user.<br>
 *    Note that in order for the distributed execution to work the array <b>must</b> 
 *    include a clone of {@link DistributedExecutionHelper.AGENTS_SELECT} parameters.<br>
 * 2. Test execution JVM. When user runs the scenario, one of the executing ant tasks {@link JSystemTask} 
 *    or {@link JSystemAntTask} check whether test/scenario is associated with distributed execution parameters
 *    if so, the {@link #getDistributedRunExecutor()} is invoked, The method should return an implementation of the 
 *    interface {@link DistributedRunExecutor} interface, the executor is populated with
 *    execution parameters as supplied by the user signaled to execute.   
 *    <br>
 *    <br>
 * To set the plug-in implementation set the property {@link FrameworkOptions#DISTRIBUTED_EXECUTION_PLUGIN}
 * in jsystem.properties to implementation class.
 * <br>
 * 
 * @see DistributedRunExecutor
 * @see DefaultDistributedExecutionPlugin
 * @see DefaultDistributedExecutor
 * @see DistributedExecutionPluginWithSuccessControl
 * @see DistributedExecutionParameter
 * 
 * @author goland
 */
public interface DistributedExecutionPlugin {
	
	/**
	 * Return an array of distributed execution parameters.<br>
	 * Parameters are fetched by the parameters panel and presented to the user.
=	 */
	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception;
	
	/**
	 * Return executor class. Given agent URL the executor should connect 
	 * to the agent and invoke test/scenario.
	 * Executor is invoked in the tests JVM (as opposed to runner JVM.
	 */
	public DistributedRunExecutor getDistributedRunExecutor() throws Exception;
}
