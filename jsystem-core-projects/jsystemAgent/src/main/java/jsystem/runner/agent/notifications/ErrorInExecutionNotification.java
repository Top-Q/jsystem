/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;
import jsystem.runner.ErrorLevel;


public class ErrorInExecutionNotification extends RunnerNotification {

	private static final long serialVersionUID = -1454998534692015142L;
	private String title;
	private String message;
	private ErrorLevel level;
	public ErrorInExecutionNotification(Object source,String title,String message,ErrorLevel level) {
		super(ErrorInExecutionNotification.class.getName(), source);
		this.title = title;
		this.message = message;
		this.level = level;
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.errorOccured(title, message, level);
	}

}
