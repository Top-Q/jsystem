/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.launcher.StartRunner;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.agent.MBeanNames;
import jsystem.utils.FileLock;


import com.aqua.filetransfer.ftp.FTPServer;
import com.ignis.embeddedcatalina.EmbeddedCatalina;

/**
 * @author goland
 */
public class RunnerAgentMain implements StartRunner {
	
	private static Logger log = Logger.getLogger(RunnerAgentMain.class.getName());
	private volatile boolean restart;
	
	public void stop(){
		synchronized (RunnerAgentMain.class){
			RunnerAgentMain.class.notifyAll();
		}
	}

	public void restart(){
		restart = true;
		stop();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RunnerAgentMain main = new RunnerAgentMain();
		main.startRunner(args);
	}

	private void run() throws Exception {
		if (!checkLock()){
			log.info("Another runner engine is already running with this root folder. exiting");
			System.exit(1);
		}
		SutFactory.suppressGUI();
		log.info("Starting JSystem agent activation ... ");			
		System.setProperty(RunningProperties.JSYSTEM_AGENT,"true");
		initAgent();
		log.info("JSystem agent initialization has ended successfully.  Listening on port:" + System.getProperty("com.sun.management.jmxremote.port"));
		waitForShutdownSignal();
		log.info("Exiting ... ");
		int exitCode = restart ? 6 :0;
		System.exit(exitCode);
	}

	/**
	 * ITAI: This method was removed since it was using the Spring dependency that was removed due
	 * to log4j known security vulnerabilities. issue #360
	 * @throws Exception
	 */
	private void initAgent() throws Exception {

		
//		MBeanExporter	exporter	=	new	MBeanExporter();
//		MBeanServer		server	=	ManagementFactory.getPlatformMBeanServer();
//		exporter.setServer(server);
//
//		//agent
//		RunnerAgent agent = new RunnerAgent();
//		server.registerMBean(agent,MBeanNames.RUNNER_AGENT);
//
//		//ftp server
//		FTPServer ftpServer = new FTPServer();
//		ftpServer.setDefaultUserHomeDirectory("externalFiles");
//		ftpServer.setPort(getFtpPort());
//		ftpServer.init();
//		ftpServer.startServer();
//		exporter.registerManagedResource(ftpServer,MBeanNames.FTP_SERVER);
//
//		//servlet container
//		EmbeddedCatalina catalina = new EmbeddedCatalina();
//		catalina.setDefaultConnectorPort(getWebPort());
//		catalina.init();
//		catalina.start();
//
//		//main class
//		exporter.registerManagedResource(this,MBeanNames.AGENT_MAIN);
//		agent.init();
	}
			
	private void waitForShutdownSignal() throws Exception {
		synchronized (RunnerAgentMain.class) {
			RunnerAgentMain.class.wait();
		}
	}

	public void startRunner(String[] args) {
		try {
			JSystemProperties.getInstance(true).setJsystemRunner(true);
			JSystemProperties.getInstance();
			run();
		}catch (Throwable t){
			log.log(Level.SEVERE,"Error in jsystem agent.",t);
		}
	}

	
	private int getWebPort() {
		int webPortInt = 8383;
		String webPort = JSystemProperties.getInstance().getPreference(FrameworkOptions.AGENT_WEB_PORT);
		try {
			webPortInt = Integer.parseInt(webPort);
		} catch (Exception e) {
			log.fine("Error in WEB port parsing");
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.AGENT_WEB_PORT, "" + webPortInt);
		return webPortInt;
	}
	
	private int getFtpPort() {
		int ftpPortInt = 2121;
		String ftpPort = JSystemProperties.getInstance().getPreference(FrameworkOptions.AGENT_FTP_PORT);
		try {
			ftpPortInt = Integer.parseInt(ftpPort);
		} catch (Exception e) {
			log.fine("Error in FTP port parsing");
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.AGENT_FTP_PORT, "" + ftpPortInt);
		return ftpPortInt;
		
	}

	private boolean checkLock() throws Exception {
		FileLock lock = FileLock.getFileLock(CommonResources.LOCK_FILE);
		return lock.grabLock();
	}
}
