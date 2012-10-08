/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

import java.awt.event.ActionEvent;

import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.actionItems.InitReportersAction;

public class AgentInitReports extends InitReportersAction {

	/**
	 */
	private static final long serialVersionUID = 1L;
	private RunnerEngine agent;
	AgentInitReports(RunnerEngine agent){
		this.agent = agent;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		agent.initReporters();
	}

}
