/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class ExecutionEndedNotification extends RunnerNotification {

	private static final long serialVersionUID = -1454998534692015142L;
	private String scenarioName;
	public ExecutionEndedNotification(Object source, String scenarioName) {
		super(ExecutionEndedNotification.class.getName(), source);
		this.scenarioName = scenarioName;
	}
	public String getScenarioName() {
		return scenarioName;
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.executionEnded(getScenarioName());
	}

}
