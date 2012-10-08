/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.Test;

public class AddErrorNotification extends RunnerNotification {
	
	private static final long serialVersionUID = 2716339611635279200L;
	private int testIndex;
	private Throwable throwable;

	public int getTestIndex() {
		return testIndex;
	}

	public AddErrorNotification(Object source,int testIndex,Throwable t) {
		super(AddErrorNotification.class.getName(), source);
		this.throwable = t;
		this.testIndex = testIndex;
	}

	public void invokeDispatcher(JSystemListeners mediator){
		Test t = ScenariosManager.getInstance().getCurrentScenario().getTest(getTestIndex()).getTest();
		mediator.addError(t, getThrowable());
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
