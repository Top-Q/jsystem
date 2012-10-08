/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import jsystem.framework.distributedexecution.DistributedRunExecutor;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;

/**
 * Base class for Distributed executors.
 * @author goland
 */
public abstract class BaseJSystemDistributedExecutor implements DistributedRunExecutor {
	
	private Scenario rootScenario;
	private JTest    testToExecute;
	private String[] urls;
	private DistributedExecutionParameter[] parameters;
	
	@Override
	public void setTestToExecute(JTest test) {
		testToExecute = test;
		rootScenario = ScenarioHelpers.getRoot(test);
	}

	@Override
	public void setHostsParameters(DistributedExecutionParameter[] parameters) {
		this.parameters = parameters; 
		urls = DistributedExecutionHelper.getUrls(parameters);
	}
	

	protected Scenario getRootScenario() {
		return rootScenario;
	}

	protected JTest getTestToExecute() {
		return testToExecute;
	}

	protected String[] getUrls() {
		return urls;
	}

	protected DistributedExecutionParameter[] getParameters() {
		return parameters;
	}
}
