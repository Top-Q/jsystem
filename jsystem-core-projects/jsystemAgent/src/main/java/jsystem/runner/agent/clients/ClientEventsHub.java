/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationListener;

import jsystem.runner.agent.notifications.RunnerNotification;
import jsystem.runner.agent.server.RunnerAgentMBean;

/**
 * Listener for JMX notifications.<br>
 * If the notification is a {@link RunnerNotification}, it lets 
 * the notification handle the dispatching of the event to remote agent client
 * listeners, otherwise, it passes the notification to {@link AgentClientListenersManager#handleNotification(Notification, Object)}
 * 
 * @author goland
 */
public class ClientEventsHub implements NotificationListener {
	private static Logger log = Logger.getLogger(ClientEventsHub.class.getName());
	private AgentClientListenersManager listeners;
	private RunnerAgentMBean agentMBean;
	
	public ClientEventsHub(AgentClientListenersManager listeners){
		this.listeners = listeners;
	}
	
	public void handleNotification(Notification notification, Object handback) {
		log.fine("Got event: " + notification.getType() + " " + notification.getSource());
		if (notification instanceof RunnerNotification){
			((RunnerNotification)notification).setAgentMBean(agentMBean);
			((RunnerNotification)notification).invokeDispatcher(listeners);
		}else {
			listeners.handleNotification(notification, handback);
		}
	}
	
	public RunnerAgentMBean getAgentMBean() {
		return agentMBean;
	}
	
	public void setAgentMBean(RunnerAgentMBean agentMBean) {
		this.agentMBean = agentMBean;
	}
}
