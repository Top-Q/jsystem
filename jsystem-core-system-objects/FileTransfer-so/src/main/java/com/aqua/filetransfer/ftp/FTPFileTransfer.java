/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.aqua.sysobj.conn.CliConnection;
import com.aqua.sysobj.conn.CliFactory;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

/**
 * <b>Utility SystemObject for transferring files from the machine on which the
 * tests are running to any remote machine and the opposite direction.</b><br><br>
 * 
 * FTPFileTransfer uses telnet/ssh and FTP protocols for the file transfer hence
 * it is multi-platform, in addition, it doesn't rely on a FTP server on the
 * remote machine; it activates an embedded FTP server which runs on the local
 * machine and uses the {@link TFTPRemoteClient} to open FTP connection from the
 * remote machine to the embedded FTP server. <u><b><br><br>
 * 
 * Working with FTPFileTransfer:</b></u><br>
 * Make sure connectivity parameters are set correctly and use the different copy methods.
 * The copy operations identifies whether a connection is already open if not
 * a connection is opened.<br>
 * In many cases the remote server (telnet/ssh) limits number of connections;
 * use the {@link #closeFileTransferSession()} to close connection when needed.<br>
 * 
 * <u>FTPFileTransfer members:</u><br>
 * remoteHost: the remote host to/from files will be copied.<br>
 * operatingSystem: the operating system of the remote host. By default the value is {@link CliFactory#OPERATING_SYSTEM_WINDOWS}<br> 
 * protocol: the protocol for the connection with the remote host (one of
 * telnet/ssh/rs232). Default: telnet<br>
 * port: port for the connection to the remote host. Default is 23<br>
 * user: user for the connection to the remote host.<br>
 * password: password for the connection to the remote host.<br><br>
 * cliConnection - a CliConnection implementation for connecting to the remote 
 * client. If defined, the connectivity members above are ignored.
 * localHostExternalName: the host name of the local machine. The default value 
 * of this member is the host name as supplied by {@link java.net.InetAddress#getLocalHost}<br> 
 * ftpUserName: ftp user name for the FTP connection which is opened from the
 * remote host to the ftp server. Default: aqua<br>
 * ftpPassword: ftp password for the FTP  connection which is opened from the 
 * remote host to the ftp server.Default: aqua<br>
 * ftpServerHome: user's FTP server home directory. Default=current directory/aquaftp21<br>
 * passivate: If true ftp sessions will work in passivate mode. Please note that
 * passivation mode is set in the initiation of the FTP session. Please note that passivation works 
 * only on linux ftp clients.
 * FTP parameters should be used if an FTP server which is not aqua's FTP server
 * is already running on the machine.<br><br>
 * 
 * <u><b>FTPFileTransfer implementation details:</b></u><br>
 * The FTPFileTransfer uses the {@link FTPServer} and the
 * {@link TFTPRemoteClient}. The {@link FTPServer} is used to run embedded FTP
 * server on the local machine; {@link TFTPRemoteClient} is used to open FTP
 * connection from the remote machine to this machine and to do the file
 * transfer operation.<br>
 * <br>
 * <u>Note:</u> The object is not thread safe.
 * 
 * @author Golan Derazon
 * 
 */
public class FTPFileTransfer extends SystemObjectImpl{
	private FTPServer ftpServer;
	private FTPRemoteClient remoteHostClient;
	public  CliConnection   cliConnection;
	
	private String  serverPropertiesPath; 
	private String 	remoteHost;
	private String  localHostExternalName;
	private boolean ascii ;
	
	private String  operatingSystem = CliFactory.OPERATING_SYSTEM_WINDOWS;
	private String  protocol="telnet";	
	private int 	port=23;
	private String 	user;
	private String 	password;	
	
	private String  ftpUserName;
	private String  ftpPassword;
	private String  ftpServerHome;
	private boolean prompt = true;

	/**
	 * Constructs FTPFileTransfer object with CliConnection.
	 */
	public FTPFileTransfer(CliConnection cliConnection) throws Exception {
		this.cliConnection = cliConnection;  
	}
	/**
	 * Constructs FTPFileTransfer object which can copy files to/from local machine to local machine
	 * (the default constructor will be used mainly for testing).<br>
	 * When constructing FTPFileTransfer using this constructor it is assumed
	 * that external FTP server is not running on this machine.  
	 */
	public FTPFileTransfer() throws Exception {
		java.net.InetAddress localMachine =	java.net.InetAddress.getLocalHost();	
		setRemoteHost(localMachine.getHostAddress());
	}
	/**
	 * Constructs FTPFileTransfer object which can copy files to/from <code>remoteHose</code>.<br>
	 * When constructing FTPFileTransfer using this constructor it is assumed
	 * that external FTP server is not running on this machine.  
	 */
	public FTPFileTransfer(String remoteHost,String user,String password) throws Exception{
		this();
		setRemoteHost(remoteHost);
		setUser(user);
		setPassword(password);
		
	}
	
	/**
	 * Initializes {@link FTPFileTransfer} members<br> 
	 * verifies that a connection can be opened to the remote client and 
	 * that the remote client can open a FTP connection to the server.<br>
	 * All connections are closed at the end of the initialization.
	 */
	public void init() throws Exception {
		super.init();
		ftpServer = new FTPServer();
		if (getServerPropertiesPath() != null){
			ftpServer.setPropertiesPath(getServerPropertiesPath());
		}
		ftpServer.init();
		ftpServer.startServer();
		if (getFtpUserName() == null){
			setFtpUserName(ftpServer.getDefaultUserName());
		}
		if (getFtpPassword() == null){
			setFtpPassword(ftpServer.getDefaultUserPassword());
		}
		if (getFtpServerHome() == null){
			setFtpServerHome(ftpServer.getDefaultUserHomeDirectory());
		}
		
		if (cliConnection != null){
			remoteHostClient = new FTPRemoteClient(cliConnection,ftpServer.getExternalName());	
			remoteHostClient.setOperatingSystem(getOperatingSystem());
		}else {
			remoteHostClient = new FTPRemoteClient(getRemoteHost(),getUser(),getPassword(),ftpServer.getExternalName());
			remoteHostClient.setPort(getPort());
			remoteHostClient.setOperatingSystem(getOperatingSystem());
			remoteHostClient.setProtocol(getProtocol());
		}
		remoteHostClient.setFtpUserName(getFtpUserName());
		remoteHostClient.setFtpPassword(getFtpPassword());
		if (getLocalHostExternalName() != null){
			remoteHostClient.setFtpServerHostName(getLocalHostExternalName());
		}
		remoteHostClient.init();
	}

	/**
	 * Sets passivation mode 
	 */
	public void passivate(boolean passive) throws Exception {
		remoteHostClient.passivate(passive);
	}
	
	/**
	 * Closes connection to remote machine.
	 */
	public void closeFileTransferSession() throws Exception{
		remoteHostClient.closeFTPSession();
	}
	
	/**
	 * Copies stream to remote machine.<br><br>
	 * 
	 * Destination can be either absolute destination path or relative to client's
	 * user directory.<br>
	 * Examples:<br>
	 * 1. Absolute destination: new File("C:/automation/test.txt"). Will copy the stream to c:/automation/test.txt
	 *    on the remote machine. If folder automation doesn't exist the operation will fail.<br>
	 * 2. Relative destination: new File("test.txt"). Will copy the stream to user_dir/test.txt.<br><br>
	 *   
	 * @see TFTPRemoteClient#copyFileFromLocalMachineToRemoteClient(String, String)
	 */
	public void copyFileFromLocalMachineToRemoteMachine(InputStream source,File destination) throws Exception {
		String fileName = destination.getPath();
		File f = new File(getFtpServerHome(),""+System.currentTimeMillis());
		f.deleteOnExit();
		org.apache.commons.io.IOUtils.copy(source,new FileOutputStream(f));
		remoteHostClient.setAscii(isAscii());
		remoteHostClient.setPromptOn(isPrompt());
		remoteHostClient.copyFileFromLocalMachineToRemoteClient(f.getName(), fileName);		
	}
		
	/**
	 * Copies file to remote machine.
	 * @see #copyFileFromLocalMachineToRemoteMachine(InputStream, File)
	 */
	public void copyFileFromLocalMachineToRemoteMachine(File source,File destination) throws Exception {		
		copyFileFromLocalMachineToRemoteMachine(new FileInputStream(source), destination);
	}

	/**
	 * Copies Multiply files to remote machine.<br><br>
	 * You may use wildcard ('*') or specific files path
	 * @param filesPath - A files path array (java.lang.String...) 
	 * @throws Exception
	 */
	public void copyAllFilesFromLocalMachineToRemoteMachine(String... filesPath) throws Exception{
		remoteHostClient.setPromptOn(isPrompt());
		remoteHostClient.setAscii(isAscii());
		remoteHostClient.copyAllFilesFromLocalMachineToLocalRemote(filesPath);
	}

	/**
	 * Copies file from remote machine to this machine<br><br>
	 * 
	 * Source can be either absolute destination path or relative to client's
	 * user directory.<br>
	 * Examples:<br>
	 * 1. Absolute destination: new File("C:/automation/test.txt"). Will copy the stream to c:/automation/test.txt
	 *    on the remote machine. If folder automation doesn't exist the operation will fail.<br>
	 * 2. Relative destination: new File("test.txt"). Will copy the stream to user_dir/test.txt.<br>
	 * Destination can be either absolute destination path or relative to current dir<br>
	 * <br>
	 * 
	 * @see TFTPRemoteClient#copyFileFromRemoteClientToLocalMachine(String, String)
	 */
	public void copyFileFromRemoteMachineToLocalMachine(File source,File destination) throws Exception {
		String fileName = source.getPath();
		File f = new File(""+System.currentTimeMillis());
		f.deleteOnExit();
		remoteHostClient.setPromptOn(isPrompt());
		remoteHostClient.setAscii(isAscii());
		remoteHostClient.copyFileFromRemoteClientToLocalMachine(fileName,f.getName());
		File transferredFilePath= new File(getFtpServerHome(),f.getName());
		if (!transferredFilePath.exists()){
			throw new Exception("File transfer completed successfully but file was not found on local file system. Please check whether there is another none aqua active FTP" +
					"server on this machine. If so either shut it down or update the ftpServerHome to the server's home.");
		}
		FileUtils.copyFile(transferredFilePath,destination);
	}

	/**
	 * Copies Multiply files from remote machine.<br><br>
	 * You may use wildcard ('*') or specific files path
	 * @param filesPath - A files path array (java.lang.String...) 
	 * @throws Exception
	 */
	public void copyAllFilesFromRemoteMachineToLocalMachine(String... filesPath) throws Exception{
		remoteHostClient.setPromptOn(isPrompt());
		remoteHostClient.setAscii(isAscii());
		remoteHostClient.copyAllFilesFromRemoteMachineToLocalMachine(filesPath);
	}
	
	/**
	 * Shuts down the FTP server and 
	 * closes connection to remote machine.
	 */
	public void close() {
		try {
			ftpServer.stopServer();
		}catch (Exception e){
			report.report("Failed closing FTP server",e);
		}
		remoteHostClient.close();
	}	
	
	/*************************************************************************
	 *  Setters & getters
	 ************************************************************************/
	
	public String getFtpPassword() {
		return ftpPassword;
	}	
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}	
	public String getFtpUserName() {
		return ftpUserName;
	}	
	public void setPrompt(boolean prompt){
		this.prompt = prompt;
	}
	public boolean isPrompt() {
		return prompt;
	}
	public boolean isAscii() {
		return ascii;
	}
	public void setAscii(boolean ascii) {
		this.ascii = ascii;
	}
	public String getLocalHostExternalName() {
		return localHostExternalName;
	}
	public void setLocalHostExternalName(String localHostExternalName) {
		this.localHostExternalName = localHostExternalName;
	}
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String telnetPassword) {
		this.password = telnetPassword;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int telnetPort) {
		this.port = telnetPort;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String telnetUser) {
		this.user = telnetUser;
	}

	public String getFtpServerHome() {
		return ftpServerHome;
	}

	public void setFtpServerHome(String ftpServerHome) {
		this.ftpServerHome = ftpServerHome;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}
	public String getServerPropertiesPath() {
		return serverPropertiesPath;
	}
	public void setServerPropertiesPath(String serverPropertiesPath) {
		this.serverPropertiesPath = serverPropertiesPath;
	}
}
