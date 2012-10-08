package com.aqua.jsystemobjects.clients;

import org.jsystem.objects.clients.ClientBasic;
import org.jsystem.objects.xmlrpc.XmlRpcHelper;

public abstract class BaseClient extends ClientBasic {
	
	protected int port;
	protected Process p;
	protected String userDir;
	
	protected BaseClient(XmlRpcHelper connectionHandler){
		super(connectionHandler);
	}
	
	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public Process getP() {
		return p;
	}


	public void setP(Process p) {
		this.p = p;
	}


	public String getUserDir() {
		return userDir;
	}


	public void setUserDir(String userDir) {
		this.userDir = userDir;
	}

}
