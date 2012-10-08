/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.system.SystemObjectImpl;

/**
 * This class implements the basic operation of ftp client.
 * put,get,move,delete,mkdir,cd, and to change the type of the file(Binary/Ascii) 
 * @author Yossi Labaton & Golan Derazon
 */
public class FTPClient extends SystemObjectImpl {
	
	private static Logger log = Logger.getLogger(FTPClient.class.getName());
	private String server;
	private int port = 21;
	private String username;
	private String password;
	protected org.apache.commons.net.ftp.FTPClient ftpClient;

	public void init() throws Exception {
		super.init();
		ftpClient = new org.apache.commons.net.ftp.FTPClient();
	}

	public void close(){
		try {
			disconnect();
		}catch (Exception e){
			log.log(Level.WARNING,"Failed disconnecting from ftp server " + e.getMessage());
		}
		super.close();
	}
	
	public void connect() throws SocketException, IOException {
		ftpClient.connect(server,port);
		ftpClient.login(username, password);
	}

	public void disconnect() throws Exception {
		ftpClient.disconnect();
	}

	public void changeToBinary() throws IOException {
		ftpClient.setFileType(org.apache.commons.net.ftp.FTPClient.BINARY_FILE_TYPE);
	}

	public void changeToAscii() throws IOException {
		ftpClient.setFileType(org.apache.commons.net.ftp.FTPClient.ASCII_FILE_TYPE);
	}

	/**
	 * Fetches file from remote url (of the ftp server directory) and copies it
	 * to destination folder. 
	 * 
	 * @param remoteFileName
	 * @param localName
	 * @throws IOException
	 */

	public void getFile(String remoteFileName, String localName) throws IOException {
		FileOutputStream fos = new FileOutputStream(localName);
		try {
			ftpClient.retrieveFile(remoteFileName, fos);
		} finally {
			fos.close();
		}
	}

	/**
	 * Puts file source to url (The url of the ftp server) the directory of the
	 * ftp server. 
	 * 
	 * @param localfileName
	 * @param remoteName
	 * @throws IOException
	 */

	public void putFile(String localfileName, String remoteName) throws IOException {
		report.report("Put " + localfileName + " in " + remoteName);
		FileInputStream fis = new FileInputStream(localfileName);
		try {
			ftpClient.storeFile(remoteName, fis);
		} finally {
			fis.close();
		}
	}

	/**
	 * Rename a file in remote machine. The path of the file, relates to the ftp
	 * server directory. url - The url of the ftp server directory - The
	 * directory of the ftp server.
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */

	public void moveFile(String from, String to) throws IOException {
		report.report("Move " + from + " to");
		ftpClient.rename(from, to);
	}

	/**
	 * Deletes a file from remote machine. The path of the file, relates to the
	 * ftp server directory. url - The url of the ftp server directory - The
	 * directory of the ftp server.
	 * 
	 * @param pathname
	 * @throws IOException
	 */

	public void deleteFile(String pathname) throws IOException {
		report.report("Delete From FTP: " + pathname);
		ftpClient.deleteFile(pathname);
	}

	/**
	 * makes a new directory url (The url of the ftp server).
	 * 
	 * @param newDir
	 * @throws Exception
	 */
	public void makeDirectory(String newDir) throws Exception {
		report.report("Make directory " + newDir);
		ftpClient.makeDirectory(newDir);
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
