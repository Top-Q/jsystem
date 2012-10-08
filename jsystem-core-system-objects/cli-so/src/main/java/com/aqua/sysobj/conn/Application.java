/*
 * Created on 21/07/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package com.aqua.sysobj.conn;

import jsystem.framework.system.SystemObject;
import jsystem.framework.system.SystemObjectImpl;

/**
 * Application is an abstract class which refers to any functionality / feature of the
 * tested product.
 * 
 * @author guy.arieli
 * 
 */
public abstract class Application extends SystemObjectImpl implements ApplicationInter {
	/**
	 * used to manage all the connectivity aspects, hold all the connections.
	 */
	public ConnectivityManager conn;

	protected boolean forceIgnoreAnyErrors = false;

	public void init() throws Exception {
		super.init();
		if (conn == null) { // if it was not init
			/*
			 * the ConnectivityManager is init directly from the parent system
			 * object.
			 */
			
			SystemObject so = this;

			while ((sut.getAllValues(so.getXPath() + "/conn").size() == 0)
					&& (so.getParent() != null))
				so = so.getParent();

			conn = (ConnectivityManager) system.getSystemObject(so.getXPath(), "conn", this);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.sysobj.conn.ApplicationInter#getConnectivityManager()
	 */
	public ConnectivityManager getConnectivityManager() {
		return conn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.sysobj.conn.ApplicationInter#setConnectivityManager(com.aqua.sysobj.conn.ConnectivityManager)
	 */
	public void setConnectivityManager(ConnectivityManager conn) {
		this.conn = conn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.sysobj.conn.ApplicationInter#isIgnoreAnyErrors()
	 */
	public boolean isForceIgnoreAnyErrors() {
		return this.forceIgnoreAnyErrors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aqua.sysobj.conn.ApplicationInter#setIgnoreAnyErrors(boolean)
	 */
	public void setForceIgnoreAnyErrors(boolean ignoreAnyErrors) {
		//System.out.println("setForceIgnoreAnyErrors: "  + (ignoreAnyErrors ? " TRUE": " FALSE"));
		report.report("Setting Force Ignore Any Errors (for the next command) to "  + (ignoreAnyErrors ? " TRUE": " FALSE"));
		this.forceIgnoreAnyErrors = ignoreAnyErrors;
	}

}
