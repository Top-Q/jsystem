/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.report.JSystemListeners;

public class ReportNotification extends RunnerNotification {
	private static final long serialVersionUID = -4468223086225369256L;
	private String title;
	private String message;
	private int status;
	private boolean bold;
	public ReportNotification(Object source,String title,String message,int status,boolean bold) {
		super(ReportNotification.class.getName(), source);
		this.title = title;
		this.message = message;
		this.status = status;
		this.bold = bold;
	}
	public void invokeDispatcher(JSystemListeners dispatcher){
		dispatcher.report( title, message,  status, bold);
	}
}
