/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import jsystem.framework.distributedexecution.DistributedExecutionPlugin;
import jsystem.framework.distributedexecution.DistributedRunExecutor;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.DistributedExecutionParameter;

/**
 * Default implementation of the {@link DistributedExecutionPlugin}.<br>
 * Exposes only one parameter- the {@link DistributedExecutionHelper#AGENTS}
 * which allows the user to select the agents on which the test/scenario will
 * be executed.<br>
 * Returns the {@link DefaultDistributedExecutor} and plug-in executor.
 * 
 * @see DefaultDistributedExecutor
 * @author goland
 */
public class DefaultDistributedExecutionPlugin implements DistributedExecutionPlugin {

	@Override
	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception {
		DistributedExecutionParameter hostsList = (DistributedExecutionParameter)DistributedExecutionHelper.AGENTS_SELECT.cloneParameter();
		return new DistributedExecutionParameter[]{hostsList};
	}

	@Override
	public DistributedRunExecutor getDistributedRunExecutor() throws Exception {
		return new DefaultDistributedExecutor();
	}
}
