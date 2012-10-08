package com.aqua.jsystemobject.handlers;

import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemServer;
import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.clients.JReporterClient;
import com.aqua.jsystemobject.clients.JScenarioClient;

public enum JServerHandlers {
	SCENARIO(JScenarioHandler.class, JScenarioClient.class),
//	GUI(JGuiOperationsHandler.class, JGuiOperationsClient.class),
	APPLICATION(JApplicationHandler.class, JApplicationClient.class),
	REPORTER(JReporterHandler.class, JReporterClient.class),
	jsystem(JSystemServer.class , JSystemClient.class);
	
	Class handlerClass;
	Class clientClass;
	
	private JServerHandlers(Class handlerClass, Class clientClass) {
		this.handlerClass = handlerClass;
		this.clientClass = clientClass;
	}
	public Class getHandlerClass(){
		return handlerClass;
	}
	public String getHandlerClassName(){
		return handlerClass.getName();
	}
	
	public Class getClientClass(){
		return clientClass;
	}
	public String getClientClassName(){
		return clientClass.getName();
	}
}
