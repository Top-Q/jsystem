/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class StartFixturingNotification extends RunnerNotification {
	private static final long serialVersionUID = 3572213398025085026L;
	public StartFixturingNotification(Object source) {
		super(StartFixturingNotification.class.getName(), source);
	}
	public void invokeDispatcher(JSystemListeners mediator){
		mediator.startFixturring();
	}
}
