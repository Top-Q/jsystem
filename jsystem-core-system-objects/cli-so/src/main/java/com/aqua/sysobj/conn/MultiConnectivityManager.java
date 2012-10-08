/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import com.aqua.sysobj.conn.CliConnection;

import jsystem.framework.system.SystemObjectImpl;

public class MultiConnectivityManager extends SystemObjectImpl {
	public CliConnection[] connections;
	
	public void init() throws Exception{
		super.init();
		if(connections != null){
			for(int i = 0; i < connections.length; i++){
				if(connections[i].isConnectOnInit() && !connections[i].isConnected()){
					connections[i].connect();
				}
			}
		}
	}
	public void close(){
		if(connections == null){
			return;
		}
		for(int i = 0; i < connections.length; i++){
			connections[i].close();
		}
		
	}
}
