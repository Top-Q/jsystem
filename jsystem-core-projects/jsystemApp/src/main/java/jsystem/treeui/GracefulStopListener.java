/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.runner.agent.server.RunnerEngine;

public class GracefulStopListener implements WaitDialogListener {
	RunnerEngine agent;
	private static Logger log = Logger.getLogger(GracefulStopListener.class.getName());
	
	public GracefulStopListener(RunnerEngine agent){
		this.agent = agent;
	}
	
	public void cancel() {
		try {
			agent.stop();
		} catch (Exception e) {
			log.log(Level.SEVERE,"Failed stopping execution. " + e.getMessage());

		}
	}

}
