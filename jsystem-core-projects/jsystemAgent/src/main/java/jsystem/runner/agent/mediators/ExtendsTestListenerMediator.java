/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.mediators;

import java.util.logging.Logger;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.agent.notifications.AddWarningNotification;
import jsystem.runner.agent.notifications.NotificationLevel;
import jsystem.runner.agent.server.RunnerAgent;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * Listens for native extended jsystem events , converts them to JMX notifications
 * and dispatches the notifications.
 * @author goland
 */
public class ExtendsTestListenerMediator extends BaseMediator implements ExtendTestListener {
	private static Logger log = Logger.getLogger(ExtendsTestListenerMediator.class.getName());
	public ExtendsTestListenerMediator(RunnerAgent agent){
		super(agent);
	}
	@Override
	public void addWarning(Test test) {
		if (NotificationLevel.getCurrentNotificationLevel().compareTo(NotificationLevel.NO_FAIL) > 0){
			return;
		}		
		log.finest("addWarning(Test arg0- sent");
		int index =ScenariosManager.getInstance().getCurrentScenario().getGeneralIndex(test,false);
		sendNotification(new AddWarningNotification(runnerAgent().getClass().getName(),index));
	}

	@Override
	public void endRun() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startTest(TestInfo testInfo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}
}
