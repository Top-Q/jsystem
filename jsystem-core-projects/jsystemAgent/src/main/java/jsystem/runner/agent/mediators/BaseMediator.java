/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.mediators;

import javax.management.Notification;

import jsystem.runner.agent.server.RunnerAgent;

/**
 * Base class for agent mediators.
 * @author goland
 */
public abstract class BaseMediator {

	private RunnerAgent runnerImpl;
	
	protected BaseMediator(RunnerAgent agent){
		runnerImpl = agent;
	}
	
	protected  void sendNotification(Notification notification) {
		runnerImpl.sendNotification(notification);
	}

	protected RunnerAgent runnerAgent(){
		return runnerImpl;
	}
}
