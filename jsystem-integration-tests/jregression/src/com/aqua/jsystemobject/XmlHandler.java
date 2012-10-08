package com.aqua.jsystemobject;

import java.util.Vector;
import jsystem.framework.system.SystemObjectImpl;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class XmlHandler extends SystemObjectImpl{
	private final static XmlHandler INSTANCE = new XmlHandler();
	String host = "127.0.0.1";
	private int port = 8082;
	private static final int retryConnectfNumber = 5;
	private static final int sleepDuraionInMilliSeconds = 2000;
	
	private XmlHandler(){}
	public static XmlHandler getInstance(){
		return INSTANCE;
	}
	
	public synchronized Object handleXmlCommand(String title, String command, Object... objects) throws Exception {
		if(objects.length == 0){
			return handleXmlCommand(title, command, new Vector<Object>());
		}
		Vector<Object> v = new Vector<Object>();
		for (Object object : objects){
			if (object == null){
				v.add("");
			}else{
				v.addElement(object);
			}
		}
		return handleXmlCommand(title, command, v);
	}
	
	/**
	 * transfer xml request to server
	 * 
	 */
	public synchronized Object handleXmlCommand(String title, String command, Vector<Object> v) throws Exception {
		int timeout = 0;
		Object o = null;
		report.report(title);
		while(true){
			try {
				report.report("trying to send xmlRpc command for the "+timeout+" time");
				o = execute(command, v);
			}catch (Exception e) {
				if(e.getMessage().contains("Connection refused")){
					report.report("received connection refused, retrying");
					if(timeout < retryConnectfNumber){
						Thread.sleep(sleepDuraionInMilliSeconds);
						timeout++;
						continue;
					}
				}
			}
			if (o instanceof XmlRpcException) {
				// server exception
				throw (XmlRpcException) o;
			}
			return o;
		}
	}
	
	/**
	 * creates an XmlRpcClient and calls it's execute method to connect
	 * to a server
	 * @param command
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object execute(String command, Vector<Object> params){
		try{
			System.out.println("inside execute with command = "+command + " on port "+port);
			XmlRpcClient client = new XmlRpcClient("http://" + host + ":" + port + "/RPC2");
			return client.execute(command, params);
		}
		catch(Exception e){
			return null;
		}
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
