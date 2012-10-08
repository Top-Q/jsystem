/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.ftpserver.FtpConfigImpl;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.config.PropertiesConfiguration;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.interfaces.IFtpConfig;
import org.apache.ftpserver.usermanager.BaseUser;

import com.aqua.filetransfer.utils.NetUtils;


import jsystem.framework.JSystemProperties;
import jsystem.framework.system.SystemObjectImpl;

/**
 * SystemObject which wraps Apache embedded FTP server.<br>
 * {@link http://incubator.apache.org/ftpserver/}<br>
 * <br>
 * <u>FTPServer implementation details</u><br>
 * The functionality of this system object includes:<br> 
 * 1. Setters & getters for FTP server configuration. 
 *    For example SUT please see {@linkplain ftpserver.xml}<br>
 * 2. Creation of default user. Default user name, defaultUserPassword and root folder 
 *    can be configured using SUT file. Otherwise default user's user name 
 *    is "aqua", defaultUserPassword "aqua" and root folder is current_folder/aquaftp21.<br> 
 * 3. The FTPServer SystemObject checks whether 
 *    the FTP server port is occupied if so, it assumes that another FTP server is
 *    already running; it logs a message to the reports system and continues.<br>
 *    
 * <u><b>Note:</u></b> In a machine which has more then one network interface and one of the network interfaces 
 *       is not available to expected client, there might be a need to assign explicitly the IP on which
 *       the server listens for FTP requests.
 *       In this case use the com.aqua.filetransfer.ftp.ftpserver=myip in the jsystem.properties file.<br>
 *       
 * @author Golan Derazon
 */
public class FTPServer extends SystemObjectImpl {

	private FtpServer server;

	private int port = 21;
	private boolean isRunning = false;
	/**
	 * This is the name of the host
	 * as seen outside the sub-net.
	 * Not sure is is needed here. 
	 */
	private String externalName;
	
	private String defaultUserName = "aqua";

	private String defaultUserPassword = "aqua";
	
	private String defaultUserHomeDirectory;
	
	private String propertiesPath = "res/conf/ftpd.properties";
	/**
	 * Initializes FTPServer object.
	 */
	public void init() throws Exception {
		super.init();
		updateExternalName();
		Configuration config = getConfiguration();
		// create FTP config
		IFtpConfig ftpConfig = new FtpConfigImpl(config);
		// create the server object and start it
		server = new FtpServer(ftpConfig);
		initializeDefaultUser();
		if (isBound()){
			report.report("Another FTP server is already active.");
		}
	}

	/**
	 * Starts FTP server.<br>
	 * If this server is already running, the method logs a message
	 * and returns true.<br>
	 * If FTP port is occupied the method assumes another 
	 * FTP server is running, it logs a message and returns false
	 * otherwise starts server and returns true.
	 */
	public boolean startServer() throws Exception {
		if (isRunning()){
			report.report("FTP server already running. Ignoring startServer operation");
			return true;
		}
		if (isBound()){
			report.report("Another FTP server is already active. Ignoring startServer operation");
			return false;
		}
		server.start();
		isRunning = true;
		return true;
	}

	/**
	 * Returns true if <b>this</b> server is active
	 * otherwise returns false.<br>
	 */
	public boolean isRunning() throws Exception {
		return isRunning;
	}
	
	/**
	 * Stops FTP server.
	 */
	public void stopServer() throws Exception {
		server.stop();
		isRunning = false;
	}

	/**
	 * Returns FTP server home directory.
	 */
	public File getServerRootDirectory() throws Exception {
		return new File(server.getFtpConfig().getUserManager().getUserByName(
				getDefaultUserName()).getHomeDirectory());
	}



	private Configuration getConfiguration() throws Exception {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(getPropertiesPath());
		
		if (stream == null){
			stream = new FileInputStream(getPropertiesPath());
		}
		
		String bindName = getBindName();
		if (bindName != null){
			report.report("FTP Server is listening on " + bindName + " port: " +getPort() + " ip address is " + InetAddress.getByName(bindName));
		}else {
			report.report("FTP Server is listening on all ips. port: " +getPort());
		}
		try {
			Properties props = new Properties();
			props.load(stream);
			props.setProperty("config.socket-factory.port", "" + port);
			if (bindName != null){
				props.setProperty("config.socket-factory.address",
						bindName);
				props.setProperty("config.data-connection.active.local-address",
						bindName);
				props.setProperty("config.data-connection.passive.address",
						bindName);
			}
			PropertiesConfiguration configuration = new PropertiesConfiguration(
					props);
			return configuration;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
	
	/**
	 * Returns the IP on which the FTP server should listen.
	 */
	private String getBindName() throws Exception {
		String configuredIp = JSystemProperties.getInstance().getPreference("com.aqua.filetransfer.ftp.ftpserver");
		if (configuredIp == null || "".equals(configuredIp)){
			return null;
		} else {
			return configuredIp;
		}
	}
	private void updateExternalName() throws Exception {
		String externalName = getExternalName(); 
		
		if ( externalName == null) {
			externalName = getBindName();
		}	
		
		if (externalName == null){
			externalName = getLocalMachineHostName();
		}
		
		setExternalName(externalName);
	}
	
	private String getLocalMachineHostName() throws Exception{
		return InetAddress.getLocalHost().getHostName();
	}
	/**
	 */
	private void initializeDefaultUser() throws Exception {
		BaseUser aquaUser = new BaseUser();
		aquaUser.setEnabled(true);
		aquaUser.setName(defaultUserName);
		aquaUser.setPassword(defaultUserPassword);
		aquaUser.setWritePermission(true);
		File defaultUserRoot;
		if (getDefaultUserHomeDirectory() == null){
			defaultUserRoot = new File("aquaftp" + getPort());
		}else {
			defaultUserRoot = new File(getDefaultUserHomeDirectory());
		}
		if (!defaultUserRoot.exists() && !defaultUserRoot.mkdirs()) {
			throw new Exception("Failed creating default user root folder: "
					+ defaultUserRoot.getAbsolutePath());
		}
		aquaUser.setHomeDirectory(defaultUserRoot.getAbsolutePath());
		setDefaultUserHomeDirectory(defaultUserRoot.getAbsolutePath());
		server.getFtpConfig().getUserManager().save(aquaUser);
	}
	
	/**
	 * Checks whether FTP's port is already bound by other 
	 * process.
	 */
	private boolean isBound() throws Exception {
		String addressToCheck = getBindName();
		if (addressToCheck != null){
			return NetUtils.isBound(addressToCheck,getPort());
		}else {
			return NetUtils.isBound(getPort());
		}
	}
	
	/***************************************************************************
	 * 
	 *  Setters & getters
	 * 
	 **************************************************************************/

	public String getDefaultUserPassword() {
		return defaultUserPassword;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDefaultUserName() {
		return defaultUserName;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}

	public String getDefaultUserHomeDirectory() {
		return defaultUserHomeDirectory;
	}

	public void setDefaultUserHomeDirectory(String rootFolder) {
		this.defaultUserHomeDirectory = rootFolder;
	}

	public void setDefaultUserName(String defaultUserName) {
		this.defaultUserName = defaultUserName;
	}

	public void setDefaultUserPassword(String defaultUserPassword) {
		this.defaultUserPassword = defaultUserPassword;
	}


	/***********************************************************************
	 * 
	 ***********************************************************************/
	public static void main(String[] args) throws Exception {
		
		
		
		FTPServer server = new FTPServer();
		server.init();
		server.startServer();
		synchronized (FTPServer.class) {
			FTPServer.class.wait();
		}
		server.stopServer();
	}

	public String getPropertiesPath() {
		return propertiesPath;
	}

	public void setPropertiesPath(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}
}
