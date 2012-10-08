/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;


public class EndRunNotification extends RunnerNotification {
	
	private static final long serialVersionUID = -4105599679361512263L;
	
	public EndRunNotification(Object source) {
		super(EndRunNotification.class.getName(), source);
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.endRun();
	}
}
