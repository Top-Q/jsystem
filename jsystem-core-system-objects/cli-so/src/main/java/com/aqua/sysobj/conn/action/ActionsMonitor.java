/*
 * Created on Sep 24, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.action;

import java.util.Vector;

import com.aqua.sysobj.conn.CliConnection;

import jsystem.framework.monitor.Monitor;

/**
 * @author guy.arieli
 *
 */
public class ActionsMonitor extends Monitor {
	Vector<Action> actions;
	long cycleTime = 4000;
	CliConnection cli;
	/**
	 * @param name
	 */
	public ActionsMonitor(CliConnection cli) {
		super("Actions Monitors");
		actions = new Vector<Action>();
		this.cli = cli;
	}
	
	public void addAction(Action action){
		synchronized(actions){
			actions.addElement(action);
			action.setStartTime(System.currentTimeMillis());
		}
	}

	public long getCycleTime() {
		return cycleTime;
	}
	public void setCycleTime(long cycleTime) {
		this.cycleTime = cycleTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			long startCycle = System.currentTimeMillis();
			String cliString = cli.getCliBuffer();
			cli.cleanCliBuffer();
			synchronized(actions){
				for (int i = 0; i < actions.size(); i++){
					Action action = (Action)actions.elementAt(i);
					if (!action.isActive()){
						actions.remove(action);
					}
				}
				for (int i = 0; i < actions.size(); i++){
					Action action = (Action)actions.elementAt(i);
					action.setTestAgainstObject(cliString);
					try {
						action.act();
					} catch (Throwable t){
						
					}
				}
				long sleepTime = cycleTime - ( System.currentTimeMillis() - startCycle);
				if(sleepTime > 0 ){
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
			
		}
	}

}
