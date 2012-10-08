/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class EndFixturingNotification extends RunnerNotification {

	private static final long serialVersionUID = -1492064453059139725L;

	public EndFixturingNotification(Object source) {
		super(EndFixturingNotification.class.getName(), source);
	}

	public void invokeDispatcher(JSystemListeners mediator){
		mediator.endFixturring();
	}
}
