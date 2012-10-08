/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import junit.framework.SystemTestCase;

/**
 * Monitors the allowed idle time of a machine.
 * (Many devices forces log out in case of the maximum idle time has passed)
 * In order to activate this monitor the maxIdleTime tag(in miliSeconds) should be added to the SUT file.
 * under conn / cli 
 * 
 * Note that the actual keep alive 'Enter' will be done at idleTime * 0.9
 *
 */
public class IdleMonitor extends Thread {
	CliConnection cli;
	long timeout;
	boolean stop = false;
	
	/**	 
	 * @param cli CliConnection 
	 * @param timeout (miliSeconds) the maximum idleTime
	 */
	public IdleMonitor(CliConnection cli, long timeout){
		this.cli = cli;
		this.timeout = timeout;
	}
	
	public void run(){
		System.out.println("Idle monitor was started");
		while(!stop){
			long lastCommandTime = cli.getLastCommandTime();
			if(lastCommandTime == 0){
				try {
					Thread.sleep(timeout/2);
				} catch (InterruptedException e) {
					return;
				}
				continue;
			}
			if(System.currentTimeMillis() - lastCommandTime > (timeout * 0.9)){
				CliCommand cmd = new CliCommand();
				cmd.setCommands(new String[]{""});
				cli.command(cmd);
				if(cmd.isFailed()){
					SystemTestCase.report.report(cli.getName() + " idle monitor failed");
				} else {
					SystemTestCase.report.report(cli.getName() + " idle monitor keepalive success");
				}
			} else {
				try {
					long toSleep = (long)(timeout * 0.9) - (System.currentTimeMillis() - lastCommandTime);
					if(toSleep > 0){
						Thread.sleep(toSleep);
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	public void setStop(){
		stop = true;
	}
}
