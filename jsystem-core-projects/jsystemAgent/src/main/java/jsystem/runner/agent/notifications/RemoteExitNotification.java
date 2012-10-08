/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class RemoteExitNotification extends RunnerNotification {

	private static final long serialVersionUID = -1454998534692015142L;
	public RemoteExitNotification(Object source) {
		super(RemoteExitNotification.class.getName(), source);
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.remoteExit();
	}

}
