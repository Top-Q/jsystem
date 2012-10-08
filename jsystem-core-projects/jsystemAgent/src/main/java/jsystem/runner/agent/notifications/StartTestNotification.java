/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.TestInfo;

public class StartTestNotification extends RunnerNotification {
	private static final long serialVersionUID = -4468223086225369256L;
	private TestInfo testInfo;
	
	public StartTestNotification(Object source,TestInfo testinfo) {
		super(StartTestNotification.class.getName(), source);
		this.testInfo = testinfo;
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.startTest(testInfo);
	}
}
