package com.aqua.base;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Summary;
import jsystem.utils.ClassSearchUtil;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class GlobalStaticTests extends SystemTestCase4 {
	private String succeeded;
	private String[] jarsToFetchRelease = {}; 
	private int reportsServerPort = 8080;
	private InetAddress address; 
	
	public GlobalStaticTests() {
		super();
	}
	
	@Test
	@TestProperties(returnParam = {"succeeded"}, paramsInclude = {"succeeded","reportsServerPort"})
	public void isReportServerUp() throws Exception{
		Socket socket = null; 
		try{
			address = InetAddress.getByName("172.20.0.22");
			socket = new Socket(address, reportsServerPort);//if succeeded in creating a socket it means that the application is up.
			succeeded = "true";
			socket.close();
			report.step("report server is up");
		}
		catch(IOException ex){//failed opening a socket hence no need to close socket or stream.
			succeeded = "false";
			report.step("report server is down");
		}
	}

	@Test
	public void getJarsVersions() throws Exception {
		for (String jar:getJarsToFetchRelease() ){
			Summary.getInstance().setProperty(jar,ClassSearchUtil.getPropertyFromClassPath("META-INF/"+jar+".build.properties","jversion"));
			report.report(jar +" : "  + ClassSearchUtil.getPropertyFromClassPath("META-INF/"+jar+".build.properties","jversion"));
		}
	}
	
	public String[] getJarsToFetchRelease() {
		return jarsToFetchRelease;
	}

	public void setJarsToFetchRelease(String[] jarsToFetchRelease) {
		this.jarsToFetchRelease = jarsToFetchRelease;
	}

	public String getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(String succeeded) {
		this.succeeded = succeeded;
	}

	public int getReportsServerPort() {
		return reportsServerPort;
	}

	public void setReportsServerPort(int port) {
		this.reportsServerPort = port;
	}
	
	
}
