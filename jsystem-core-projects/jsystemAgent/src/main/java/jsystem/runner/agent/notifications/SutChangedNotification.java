/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class SutChangedNotification extends RunnerNotification {

	private static final long serialVersionUID = -1454998534692015142L;
	private String sutName;
	public SutChangedNotification(Object source, String sutName) {
		super(SutChangedNotification.class.getName(), source);
		this.sutName = sutName;
	}
	public String getSutName() {
		return sutName;
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.sutChanged(getSutName());
	}

}
