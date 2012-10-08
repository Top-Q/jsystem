/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.mediators;

import java.io.IOException;
import java.util.logging.Logger;

import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestReporter;
import jsystem.runner.agent.notifications.NotificationLevel;
import jsystem.runner.agent.notifications.ReportNotification;
import jsystem.runner.agent.server.RunnerAgent;
/**
 * Listens for test report events, converts them to JMX notifications
 * and dispatches the notifications.
 * @author goland
 */
public class TestReporterMediator extends BaseMediator implements TestReporter {
	private static Logger log = Logger.getLogger(TestReporterMediator.class.getName());
	public TestReporterMediator(RunnerAgent agent){
		super(agent);
	}
	
	public boolean asUI() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void initReporterManager() throws IOException {
		// TODO Auto-generated method stub

	}

	public void report(String title, String message, boolean isPass,boolean bold) {
		report(title,message,isPass ? Reporter.PASS:Reporter.FAIL,bold);
	}

	public void report(String title, String message, int status, boolean bold) {
		if (NotificationLevel.getCurrentNotificationLevel().compareTo(NotificationLevel.ALL_ONLY_TITLE) > 0){
			return;
		}
		if (NotificationLevel.getCurrentNotificationLevel().equals(NotificationLevel.ALL_ONLY_TITLE)){
			message = null;
		}
		log.finest("report(String title, String message, boolean status, boolean bold)");
		sendNotification(new ReportNotification(runnerAgent().getClass().getName(),title,message,status,bold));
	}

}
