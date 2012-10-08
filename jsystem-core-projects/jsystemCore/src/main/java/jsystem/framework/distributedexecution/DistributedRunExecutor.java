/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.distributedexecution;

import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTest;

/**
 * Implementation of this class is supplied by the {@link DistributedExecutionPlugin}.
 * The executor is invoked in the test execution  JVM, it is first populated with remote
 * execution parameters, test to execute and then signaled to remotely execute test/scenario.
 * 
 * @see DistributedExecutionPlugin
 * @see DistributedExecutionParameter
 * 
 * @author goland
 */
public interface DistributedRunExecutor {
	
	/**
	 * Invoked by the appropriate ant task, to pass to the executor distributed execution 
	 * parameters as were populated by the user.
	 */
	public void setHostsParameters(DistributedExecutionParameter[] parameters);
	
	/**
	 * Invoked by the appropriate ant task, to pass to the executor the UUID of the test/scenario
	 * to execute.
	 */
	public void setTestToExecute(JTest test);
	
	/**
	 * Executes test/scenario remotely.
	 */
	public void execute() throws Exception;
}
