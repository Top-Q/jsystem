/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

public interface ApplicationInter {

	public abstract ConnectivityManager getConnectivityManager();

	public abstract void setConnectivityManager(ConnectivityManager conn);

	public abstract boolean isForceIgnoreAnyErrors();

	public abstract void setForceIgnoreAnyErrors(boolean ignoreAnyErrors);

}