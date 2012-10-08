package com.aqua.jsystemobject;

import java.util.logging.Logger;

import jsystem.utils.StringUtils;

import org.apache.xmlrpc.WebServer;

import com.aqua.jsystemobject.handlers.JApplicationHandler;
import com.aqua.jsystemobject.handlers.JReporterHandler;
import com.aqua.jsystemobject.handlers.JScenarioHandler;
import com.aqua.jsystemobject.handlers.JServerHandlers;



public class JServer {

	public static JScenarioHandler scenarioHandler;
	public static JApplicationHandler applicationHandler;
	public static JSystemServer jsystem;
	public static JReporterHandler logsHandler;
	public static Logger log = Logger.getLogger(JServer.class.getName());
	/**
	 * indicates whether runner was launched
	 */
	public static boolean isRunnerActive = false;
	
	public String toString() {
		return "web server";

	}
	
	/**
	 * close the web server connection
	 * kill the java process of JServer
	 * @return
	 * @throws Exception
	 */
	public static int exit() throws Exception {
		isRunnerActive = false;
		(new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
					if (webServer != null) {
						try {
							webServer.shutdown();
						} catch (Exception ignore) {
							// ignore
						}
					}
				} catch (InterruptedException e) {
					log.warning(StringUtils.getStackTrace(e));
				}
				try {
					System.exit(0);
				} catch (Throwable t) {
					// ignore
				}
			}
		}).start();
		return 0;
	}

	
	private static int port = 8082;

	public static int getPort(){
		return port;
	}
	
	static WebServer webServer;

	/**
	 * Launch the server side the first argument should be the port to use.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		log.info("i am the main for JServer");
		log.info("Server will use port: " + port);
		try {
			webServer = new WebServer(port);
			
			jsystem = new JSystemServer();
			
			//create the instances of handlers in server
			applicationHandler = new JApplicationHandler();
			scenarioHandler = new JScenarioHandler();
			
			
			logsHandler = new JReporterHandler();
			//register handlers in server
			webServer.addHandler(JServerHandlers.SCENARIO.getHandlerClassName(),scenarioHandler);
			webServer.addHandler(JServerHandlers.APPLICATION.getHandlerClassName(),applicationHandler);
			webServer.addHandler("jsystem", jsystem);
			webServer.addHandler(JServerHandlers.REPORTER.getHandlerClassName(), logsHandler);
			webServer.start();
			System.out.println("webserver successfully started!!! + listening on port " +  port);
		} catch (Exception e) {
			log.warning("failed in webserver handler adding or creation on port= "+port + "\n\n"+StringUtils.getStackTrace(e));
		}
	}
}
