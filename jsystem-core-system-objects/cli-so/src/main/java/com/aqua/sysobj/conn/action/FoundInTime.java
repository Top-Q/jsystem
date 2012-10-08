/*
 * Created on Sep 24, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.action;

/**
 * @author guy.arieli
 *
 */
public class FoundInTime extends Action {
	long timeout;
	String toFind;
	public FoundInTime(String toFind, long time){
		this.timeout = time;
		this.toFind = toFind;
	}
	/* (non-Javadoc)
	 * @see com.aqua.sysobj.conn.action.Action#act()
	 */
	public void act() {
		if(System.currentTimeMillis() - startTime > timeout){
			report.report("notification wasn't found, notification: " + toFind, null, false);
			setActive(false);
			return;
		}
		if(getTestAgainstObject().toString().indexOf(toFind) >= 0){
			// the notification was found
			report.report("notification found, notification: " + toFind);
			setActive(false);
		}
	}

}
