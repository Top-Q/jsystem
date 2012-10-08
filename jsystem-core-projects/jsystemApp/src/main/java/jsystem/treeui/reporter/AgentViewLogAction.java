/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

import java.awt.event.ActionEvent;
import java.net.URL;

import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.actionItems.ViewLogAction;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.BrowserLauncher;

public class AgentViewLogAction extends ViewLogAction {
	private static final long serialVersionUID = 1L;
	private RunnerEngine agent;
	public AgentViewLogAction(RunnerEngine agent){
		super();
		this.agent = agent;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			URL url = agent.getLogUrl();
			BrowserLauncher.openURL(url.toString());
		} catch (Exception e1) {
			ErrorPanel
					.showErrorDialog("Failed to activate log browser."+ "Please define/update the 'browser' property in the jsystem.properties file.",
							e1, ErrorLevel.Warning);
		}
	}

}
