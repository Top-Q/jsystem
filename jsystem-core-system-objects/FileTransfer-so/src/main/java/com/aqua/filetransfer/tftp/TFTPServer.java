/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.tftp;

import java.net.InetAddress;

import com.aqua.filetransfer.utils.NetUtils;
import com.globalros.tftp.common.VirtualFileSystem;
import com.globalros.tftp.server.EventListener;
import jsystem.framework.system.SystemObjectImpl;

/**
 * SystemObject which wraps tftp4java TFTP server.<br>
 * {@linkplain http://sourceforge.net/projects/tftp4java/}
 * <br>
 * <u>FTPServer implementation details</u><br>
 * The functionality of this system object includes:<br> 
 * 1. Setters & getters for TFTP server configuration. 
 *    For example SUT please see {@linkplain tftpserver.xml}<br>
 * 3. The TFTPServer SystemObject checks whether 
 *    the FTP server port is occupied if so, it assumes that another TFTP server is
 *    already running; it logs a message to the reports system and continues.<br>
 *           
 * @author Golan Derazon
 */
public class TFTPServer extends SystemObjectImpl implements EventListener{

	private com.globalros.tftp.server.TFTPServer server;

	private int port = 69;		
	private String homeDirectory = ".";
	
	/**
	 * Construct a TFTPServer with default values.<br>
	 * port = 69
	 * home directory = current dir.
	 */
	public TFTPServer(){	
	}
	
	/**
	 * Construct a TFTPServer with <code>homeDir</code>.br>
	 * port = 69
	 */
	public TFTPServer(String homeDir){
		setHomeDirectory(homeDir);
	}
	
	/**
	 * Initializes TFTPServer object.
	 */
	public void init() throws Exception {
		super.init();
	      VirtualFileSystem vfs = new FileSystem(homeDirectory);
	      server = new com.globalros.tftp.server.TFTPServer(vfs, this);
	      server.setPoolSize(5);
	      server.setPort(port);		
	}

	/**
	 * Starts TFTP server.<br>
	 * If TFTP port is already bound logs a message amessage
	 * and returns false; otherwise it starts the server and returns true.
	 */
	public boolean startServer() throws Exception {
		if (NetUtils.isBound(getPort())){
			report.report("TFTP port is already boud, TFTP server is probably already running");
			return false;
		}
		server.start();
		return true;
	}
	
	/**
	 * Stops TFTP server.
	 */
	public void stopServer() throws Exception {
		server.stop();
	}
	
	/**
	 * Logs success/failure message to reports system.<br>
	 * TFTP server EventListener implementation. See TFTP server 
	 * documentation for more details. 
	 */
	public void onAfterDownload(InetAddress addr, int port, String fileName, boolean ok) {
		report.report("Downloaded to"+ addr.getHostAddress() + " Port: " + port + " File name " + fileName + " Status: " +  (ok ? "Okay" : "Error"));
	}
	
	/**
	 * Logs success/failure message to reports system.<br>
	 * TFTP server EventListener implementation. See TFTP server 
	 * documentation for more details. 
	 */
	public void onAfterUpload(InetAddress addr, int port, String fileName, boolean ok) {
		report.report("Uploaded from " + addr.getHostAddress() + " Port: " + port + " File name " + fileName + " Status: " +  (ok ? "Okay" : "Error"));		
	}
	
	/**
	 * Setters & Getters
	 */
	
	public String getHomeDirectory() {
		return homeDirectory;
	}

	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	/***************************************************************************
	 * 
	 */
	public static void main(String[] args) throws Exception {
		TFTPServer server = new TFTPServer();
		server.init();
		server.startServer();
		synchronized (TFTPServer.class) {
			TFTPServer.class.wait();
		}
		server.stopServer();
	}

}
