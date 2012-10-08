/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import javax.management.Notification;

import jsystem.framework.report.JSystemListeners;
import jsystem.runner.agent.server.RunnerAgentMBean;

/**
 * Base class for all runner agent JMX notifications.
 * Each notification which extends this base class should implement 
 * Notifications are created and dispatched by the agent,
 * When received by the client,  
 * @author goland
 */
public abstract class RunnerNotification extends Notification {
	private static final long serialVersionUID = -1454998534692015142L;
	
	private transient RunnerAgentMBean agentMBean;
	
	public RunnerNotification(String type, Object source) {
		super(type, source, 0);
	}
	
	public abstract void invokeDispatcher(JSystemListeners dispatcher);
	
	protected RunnerAgentMBean getAgentMBean() {
		return agentMBean;
	}
	public void setAgentMBean(RunnerAgentMBean agentMBean) {
		this.agentMBean = agentMBean;
	}
}
