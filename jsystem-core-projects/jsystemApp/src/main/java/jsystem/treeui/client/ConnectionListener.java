/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;

import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.RemoteAgentUIComponents;
import jsystem.treeui.error.ErrorPanel;

/**
 * Listens for RMI connection status changes.
 * Update UI when connection state changes,
 * In case of problem in connection, starts a monitor that polls
 * the connection.
 * @author goland
 *
 */
public class ConnectionListener implements NotificationListener {
	
	private static Logger log = Logger.getLogger(ConnectionListener.class.getName());
	private ConnectionMonitor monitor;
	private RunnerEngine engine;
	private Thread thread;
	private volatile boolean doMonitor = false;

	public ConnectionListener(){
		monitor = new ConnectionMonitor();
		thread = new Thread(monitor);
		thread.start();
	}
	
	public void handleNotification(Notification notification, Object handback) {
		RemoteAgentUIComponents.setConnectionStatus(notification.getType());
		if (notification.getType().equals(JMXConnectionNotification.CLOSED)){
			startMonitor();
		}
		
		if (notification.getType().equals(JMXConnectionNotification.OPENED)){
			stopMonitor();
		}
	}
	
	public synchronized void connect() throws Exception {
		try {
			engine.init();
		}catch (Exception e){
			RemoteAgentUIComponents.setConnectionStatus(JMXConnectionNotification.FAILED);
			startMonitor();
			throw e;
		}
		engine.addListener(this);
		RemoteAgentUIComponents.setConnectionStatus(JMXConnectionNotification.OPENED);
		ErrorPanel.disposeErrorDialog();
		log.info("Connected to agent. " );	
		stopMonitor();
	}
	
	private synchronized void startMonitor() {
		doMonitor = true;
	}
	
	public synchronized void stopMonitor(){
		doMonitor = false;
	}

	private synchronized boolean doMonitor(){
		return doMonitor ;
	}
	
	public synchronized void setEngine(RunnerEngine engine ){
		this.engine = engine;
	}

	/**
	 */
	class ConnectionMonitor implements Runnable {
		
		public void run() {
			log.info("Connection monitor thread is up... ");
			while (true){
				try {
					if (doMonitor()){
						connect();
					}
				}catch (Exception e){
					log.fine("Failed opening connection to agent " + e.getMessage());
				}
				try {
					Thread.sleep(5000);
				}catch (InterruptedException e){
					
				}		
			}
		}		
	}
}

