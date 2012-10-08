/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import jsystem.framework.distributedexecution.DistributedRunExecutor;
import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.Parameter.ParameterType;

/**
 * Implementation of the {@link DefaultDistributedExecutionPlugin} interface which adds
 * user control on the test pass condition.<br>
 * The condition is "PassIfOnePass" - if set to true, the runner tree will show test
 * as passed if it passed on one of the agents. When set to false, the runner will show test
 * as passed only if it passed on all agents.
 * 
 * @see SuccessConditionDistributedExecutor
 * @author goland
 */
public class DistributedExecutionPluginWithSuccessControl extends DefaultDistributedExecutionPlugin{
	public static final String PASS_IF_ONE_PASS = "PassIfOnePass";
	public static final String GET_RUN_PROPERTIES = "GetRunProperties";
	
	@Override
	public DistributedExecutionParameter[] getDistributedExecutionParameters() throws Exception {
		DistributedExecutionParameter[] params = super.getDistributedExecutionParameters();
		DistributedExecutionParameter[] paramsToRet = new DistributedExecutionParameter[3];
		paramsToRet[0] = params[0];
		
		DistributedExecutionParameter failCondition = new DistributedExecutionParameter(PASS_IF_ONE_PASS,ParameterType.BOOLEAN,Boolean.TRUE);
		failCondition.setDefaultValue(Boolean.TRUE);
		failCondition.setDescription("Success if all instances of a test in all agents pass");
		paramsToRet[1] = failCondition;

		DistributedExecutionParameter getRunProps = new DistributedExecutionParameter(GET_RUN_PROPERTIES,ParameterType.BOOLEAN,Boolean.FALSE);
		failCondition.setDefaultValue(Boolean.FALSE);
		failCondition.setDescription("When set to true, agents run.properties are merged into runner's run properties");
		paramsToRet[2] = getRunProps;
		
		return paramsToRet;
	}

	@Override
	public DistributedRunExecutor getDistributedRunExecutor() throws Exception {
		return new SuccessConditionDistributedExecutor();
	}

}
