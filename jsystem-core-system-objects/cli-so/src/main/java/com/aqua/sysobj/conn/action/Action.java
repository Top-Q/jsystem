/*
 * Created on Sep 24, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.action;

import jsystem.framework.system.SystemObjectImpl;

/**
 * Can be fed to the ActionsMonitor.
 * Every action will get the cli output every cycle.
 *  
 * @author guy.arieli
 *
 */
public abstract class Action extends SystemObjectImpl{
	protected boolean active = true;
	protected long startTime;
	
	public abstract void act();
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
