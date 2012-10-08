/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class AddFailureNotification extends RunnerNotification {
	
	private static final long serialVersionUID = 2716339611635279200L;
	private int testIndex;
	private AssertionFailedError throwable;

	public int getTestIndex() {
		return testIndex;
	}

	public AddFailureNotification(Object source,int testIndex,AssertionFailedError t) {
		super(AddFailureNotification.class.getName(), source);
		this.throwable = t;
		this.testIndex = testIndex;
	}

	public void invokeDispatcher(JSystemListeners mediator){
		Test t = ScenariosManager.getInstance().getCurrentScenario().getTest(getTestIndex()).getTest();
		mediator.addFailure(t, getThrowable());
	}

	public AssertionFailedError getThrowable() {
		return throwable;
	}
}
