/*
 * Created on 19/07/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.HashMap;

import jsystem.framework.system.SystemObjectImpl;

/**
 * @author guy.arieli
 *
 */
public class ConnectivityManager extends SystemObjectImpl {
	public static final int CONN_CLI = 0;
	public static final int CONN_SNMP = 1;
	public static final int CONN_CONSOLE = 2;
	public static final int CONN_NAP = 3;
	public static final int CONN_CORBA = 4;
	
	/**
	 * The node host
	 */
	private String host;
	
	public CliConnection cli;
	public SnmpConnection snmp;
	public CliConnection console;
	
	HashMap<String,CliConnection> cliConnections = new HashMap<String, CliConnection>(); 
	
	public void init() throws Exception{
		super.init();
		/**
		 * if host was set and cli is not null and the host
		 * was not set for cli
		 */ 
		if(host != null && cli != null && cli.getHost() == null){
			cli.setHost(host);
		}
	}
	public CliConnection getCli(){
		return cli;
	}
	public CliConnection getCli(String name){
		return cliConnections.get(name);
	}
	public SnmpConnection getSnmp(){
		return snmp;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return Returns the console.
	 */
	public CliConnection getConsole() {
		return console;
	}
	/**
	 * @param console The console to set.
	 */
	public void setConsole(CliConnection console) {
		this.console = console;
	}
}
