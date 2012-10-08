/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import jsystem.framework.JSystemProperties;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;
import jsystem.utils.ReflectionUtils;
import jsystem.utils.StringUtils;
import systemobject.terminal.Cli;
import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliConnection;
import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.CliFactory;

/**
 * <b>SystemObject for running FTP client on a remote machine.</b><br>
 * The main purpose of this system object is to enable file transfer
 * without assuming an FTP server is running on the remote machine.<br>
 * In a typical usage of this SystemObject, an embedded FTP server 
 * will be activated on the local machine.
 * A {@link Cli}  session is opened with the remote client the session  
 * activates the FTP client on the remote machine. <br>
 * 
 * <u>Using FTPRemoteClient</u><br>
 * SystemObject can be instantiated from sut file or directly in the code.
 * Once initiated copy operations can be used.
 * The copy operations identifies whether a connection is already open if not
 * a connection is opened.<br>
 * In many cases the remote server (telnet/ssh) limits number of connections;
 * use the {@link #closeFTPSession()} to close connection when needed.<br>
 *
 * Passivation: since TAS 4.9 the sys object support passivation. Please note that passivation 
 * is only supported when the remote client is a linux machine.
 * In case the built-in prompts are not enough to open an FTP session
 * with the FTP server you are using the system object also supports adding additional FTP prompts.
 * To do that write a property file called {@link #FILE_TRANSFER_PROPERTIES_FILE_NAME}
 * (in run directory) and add to it the following properties:
 * {@link #FTP_LOGIN_PROMPTS} - comma seperated prompts which identifies that 
 * the FTP server waits for the user to enter the login user name
 *
 * {@link #FTP_PASSWORD_PROMPTS} - comma seperated prompts which identifies that 
 * the FTP server waits for the user to enter the password
 *
 * {@link #FTP_PROMPTS} - comma seperated prompts which identifies that 
 * the FTP server is waiting for an ftp command
 *
 * Since TAS 4.9 cli connectivity parameters to can be set using CliConnection.
 * This can be done either by passing a CliConnection to the FtpRemoteClient constructor              
 * or setting the <code>cliConnection</code> member through the SUT file.
 * When connectivity parameters are set using a CliConnection other connectivity 
 * parameters are ignored (host,operatingSystem,protocol,port,user,password).
 * 
 * FTP Server address:
 * -------------------
 * FTP Server address is fetched as following:
 * If the user gave value to the member {@link #ftpServerHostName} through the SUT file
 * or by activating it's setter this will be the server to which the remote ftp client will
 * try to connect.
 * Next, when connecting, the system object will try to fetch the property {@value #LOCAL_HOST_ADDRESS_PROPERTY}
 * from the jsystem.properties file, if the property was set it will use it as server address
 * otherwise, the system object uses java API to get local machine host name and uses it as server address.  
 */
public class FTPRemoteClient extends SystemObjectImpl {
	public static final String FILE_TRANSFER_PROPERTIES_FILE_NAME = "filetransfer.properties";
	public static final String FTP_PROMPTS = "ftp.prompts";
	public static final String FTP_LOGIN_PROMPTS = "ftp.login.prompts";
	public static final String FTP_PASSWORD_PROMPTS = "ftp.password.prompts";
	public static final String LOCAL_HOST_ADDRESS_PROPERTY = "local.host.external.name";
	
	public  CliConnection cliConnection;
	private Cli 	cli;
	
	private String 	host;
	private String  operatingSystem = CliFactory.OPERATING_SYSTEM_WINDOWS;
	private String  protocol = "telnet";
	private int 	port = 23;
	private String 	user;
	private String 	password;
	private String  ftpServerHostName;
	private String  ftpUserName="aqua";
	private String  ftpPassword="aqua";
	private boolean ascii ;
	private Prompt[] ftpGeneralPrompts;
	private Prompt[] ftpLoginPrompts;
	private Prompt[] ftpPasswordPrompts;
	private java.net.InetAddress localMachine;
	private boolean promptOn = true;
	
	/**
	 */
	public FTPRemoteClient(CliConnection cliConn,String ftpServerHostName) throws Exception{
		cliConnection = cliConn;
		setFtpServerHostName(ftpServerHostName);
		
	}

	/**
	 * Constructs a FTPRemoteClient for working on local machine as the remote machine.<br>
	 * Used for testing purposes.
	 */
	public FTPRemoteClient() throws Exception{
		localMachine =	java.net.InetAddress.getLocalHost();	
		setHost(localMachine.getHostName());
	}
	/**
	 * Constructs a FTPRemoteClient were remote machine is this machine.
	 * The FTPRemoteClient assumes Aqua's embedded FTP server is running on 
	 * this machine.
	 */
	public FTPRemoteClient(String user,String password) throws Exception {
		this();
		setUser(user);
		setPassword(password);
	}
	
	/**
	 * Constructs a FTPRemoteClient were remote machine is <code>host</code>.
	 * The FTPRemoteClient assumes Aqua's embedded FTP server is running on 
	 * this machine.
	 */
	public FTPRemoteClient(String host,String telnetUser,String telnetPassword,String ftpServerHostName) throws Exception{
		this(telnetUser,telnetPassword);
		setHost(host);
		setFtpServerHostName(ftpServerHostName);
	}
	
	/**
	 * Initializes {@link FTPRemoteClient} members and verifies that
	 * a telnet connection can be opened to the remote client and 
	 * that the remote client can open a FTP connection to the server.<br>
	 * All connections are closed when initialization is done.
	 * @see SystemObjectImpl#init()
	 */
	public void init() throws Exception {
		super.init();
		initPrompts();
	}
	
	/**
	 * Closes connection to remote machine.
	 */
	public void closeFTPSession(){
		closeFtp();
		closeCli();
	}
	
	/**
	 * Copies a file from FTP server machine(in most cases it will be the local machine)
	 * to the remote client.<br>
	 * Source file path should be relative to FTP user home directory and not absolute
	 * file path.
	 * Destination can be either absolute destination path or relative to client's
	 * user directory.<br>
	 */
	public void copyFileFromLocalMachineToRemoteClient(String source, String destination) throws Exception {		
		StringBuffer stringbuffer = new StringBuffer("get ");
		destination = adjustPath(destination);
		
		stringbuffer.append(source);
		stringbuffer.append(" ");
		stringbuffer.append(destination);
		copyFileViaFTP(stringbuffer.toString());
	}

	/**
	 * Copies all files from FTP server machine(in most cases it will be the local machine)
	 * to the remote client.<br>
	 * 
	 * @param filesPath - String Array (String...) of full file path.<br>
	 * @throws Exception
	 */
	public void copyAllFilesFromLocalMachineToLocalRemote(String... filesPath) throws Exception{
		copyAllFilesViaFTP("mget ", filesPath);
	}

	/**
	 * Copies a file from the remote client to FTP server machine(in most cases it will be 
	 * the local machine)
	 * 
	 * Source file path can be either absolute destination path or relative to client's
	 * user directory.
	 * Destination should be relative to FTP user home directory and not absolute
	 * file path.
	 */
	public void copyFileFromRemoteClientToLocalMachine(String source, String destination) throws Exception {
		source = adjustPath(source);
		
		StringBuffer stringbuffer = new StringBuffer("put ");
		stringbuffer.append(source);
		stringbuffer.append(" ");
		stringbuffer.append(destination);
		copyFileViaFTP(stringbuffer.toString());
	}
	
	/**
	 * Copies all files from remote client to FTP server machine(in most cases it will be
	 *  the local machine).<br>
	 * 
	 * @param filesPath - String Array (String...) of full file path.<br>
	 * @throws Exception
	 */
	public void copyAllFilesFromRemoteMachineToLocalMachine(String... filesPath) throws Exception{
		copyAllFilesViaFTP("mput ", filesPath);
	}
	
	private void copyFileViaFTP(String command) throws Exception {
		openFTPSession();
		setAsciiMode(isAscii());
		setPromptMode(isPromptOn());
		runCliCommand(command);
	}
	
	private void copyAllFilesViaFTP(String command, String... filesPath) throws Exception {
		StringBuffer stringBuffer = new StringBuffer(command);
		openFTPSession();
		setAsciiMode(isAscii());
		setPromptMode(isPromptOn());
		for(String currentFilePath : filesPath){
			String source = adjustPath(currentFilePath);
			stringBuffer.append(source);
			stringBuffer.append(" ");
		}
		runCliCommand(stringBuffer.toString());
	}
	
	private void runCliCommand(String command) throws Exception{
		cli.command(command , 1000 *60 * 5,true,false,null,ftpGeneralPrompts);
		if (cli.getResult().indexOf("226") < 0){
			throw new Exception("Failed in files transfer");
		}
	}

	/**
	 * Changes ftp session mode to passive
	 */
	public void passivate(boolean isPassive) throws Exception {
		openFTPSession();
		for (int i = 0; i < 2;i++){
			cli.command("passive",1000*60,true,false,null,ftpGeneralPrompts);
			String result = cli.getResult().toLowerCase();
			boolean on = result.indexOf("on") >= 0;
			boolean off = result.indexOf("off")>=  0;
			boolean notSupported = result.indexOf("invalid")>=  0;
			if (notSupported){
				throw new Exception("Passivation not supported");
			}
			if ((isPassive && on) ||(!isPassive && off) ){
				break;
			}
		}
	}	
	
	/**
	 * Terminates FTPRemoteClient.
	 */
	public void close() {
		closeFTPSession();
		super.close();
	}	

	/**
	 * Opens FTP session
	 */
	private void openFTPSession() throws Exception {
		initCli();
		ftpLogin();
	}
	/**
	 */
	private void initCli() throws Exception {
		if (cli == null){
			if (cliConnection != null){
				initCliFromCliConnectionImpl();
				return;
			}
			
			Prompt p = new Prompt();
			p.setPrompt(">");
			p.setCommandEnd(true);
			cli = 
				CliFactory.createCli(getHost(),getOperatingSystem(), getProtocol(),getUser(),getPassword(),new Prompt[]{p});
		}
	}

	private void initCliFromCliConnectionImpl() throws Exception{
		if (!cliConnection.isConnected()){
			cliConnection.connect();
		}
		cli = (Cli)ReflectionUtils.getField("cli", CliConnectionImpl.class).get(cliConnection);
	}

	/**
	 */
	private void closeFtp(){
		try {
			cli.command("bye", 1000 *2 ,true,false,null,new Prompt[]{new Prompt("bye.",true)});
			if (cli.getResult().indexOf("221") < 0){
				report.report("Did not find success code 221");
			}
		}catch (Exception e){
			report.report("Could not find prompt after closing session. " + e.getMessage());			
		}
	}

	/**
	 */
	private void closeCli(){
		if (cli != null){
			try {
				if (cliConnection != null){
					closeCliConnectionImpl();
				}
				cli.close();
			}catch (Exception e){
				report.report("Failed closing telnet connection",e);
			}
		}
		cli=null;
	}

	private void closeCliConnectionImpl() throws Exception{
		if (cliConnection.isConnected()){
			cliConnection.disconnect();
		}
	}
	
	
	/**
	 * Starts FTP client and performs login.
	 */
	private void ftpLogin() throws Exception{
		cli.command("");
		String result = cli.getResult();
		for (String ftpPrompt:promptsToStringArray(ftpGeneralPrompts)){
			if (result.indexOf(ftpPrompt) >=0 ){
				//we are already logged in
				return;
			}
		}
		String serverAddress = getFTPServerAddress();
		cli.command("ftp " + serverAddress, 1000*60,true,false,null,ftpLoginPrompts);
		if (cli.getResult().indexOf("220") < 0){
			throw new Exception("Failed connecting to FTP server.("+serverAddress+"). Please verify that there is a ping between the remote client to the runner machine");
		}
		cli.command(getFtpUserName(),1000*60,true,false,null,ftpPasswordPrompts);
		if (cli.getResult().indexOf("331") < 0){
			throw new Exception("Failed in login process");
		}
		cli.command(getFtpPassword(),1000*60,true,false,null,ftpGeneralPrompts);
		if (cli.getResult().indexOf("230") < 0){
			throw new Exception("User not authorized to login");
		}
	}
	/**
	 * Changes ftp session mode (ascii/binary)
	 */
	private void setAsciiMode(boolean isAscii) throws Exception {
		String command = "binary";
		if (isAscii){
			command="ascii";
		}
		cli.command(command,1000*60,true,false,null,ftpGeneralPrompts);
		if (cli.getResult().indexOf("200") < 0){
			throw new Exception("Failed changing to binary mode");
		}
	}
	
	/**
	 * Changes the FTP session mode ( on / off )
	 * @param promptOn
	 * @throws Exception
	 */
	private void setPromptMode(boolean promptOn) throws Exception{
		String command = "prompt off";
		if (promptOn){
			command="prompt on";
		}
		cli.command(command,1000*60,true,false,null,ftpGeneralPrompts);
		if (cli.getResult().indexOf("Interactive") < 0){
			throw new Exception("Failed changing prompt mode");
		}
	}

	public boolean isPromptOn() {
		return promptOn;
	}
	
	public void setPromptOn(boolean promptOn) {
		this.promptOn = promptOn;
	}

	/**
	 * Adjusts file path to operating system.
	 */
	private String adjustPath(String path) {
		if (CliFactory.OPERATING_SYSTEM_WINDOWS.equals(getOperatingSystem())){
			String toReturn = FileUtils.convertToWindowsPath(path);
			if (!toReturn.startsWith("\"")){
				toReturn =  "\""+toReturn+"\"";
			}
			return toReturn;
		}else {
			return FileUtils.replaceSeparator(path);
		}
	}
	
	/**
	 * 
	 */
	private void initPrompts() throws Exception {
		String[] defaultFTPPrompts = new String[]{"ftp>"};
		String[] defaultLoginPrompts = new String[]{"):"};
		String[] defaultPasswordPrompts = new String[]{"for "+getFtpUserName(),"Password:"};
		
		if (!new File(FILE_TRANSFER_PROPERTIES_FILE_NAME).exists()){
			ftpGeneralPrompts = stringArrayToPrompts(defaultFTPPrompts);
			ftpLoginPrompts = stringArrayToPrompts(defaultLoginPrompts);
			ftpPasswordPrompts = stringArrayToPrompts(defaultPasswordPrompts);
			return;
		}
		
		Properties props = new Properties();
		FileInputStream stream = new FileInputStream(FILE_TRANSFER_PROPERTIES_FILE_NAME);
		try {
			props.load(stream);
		}finally{
			try{stream.close();}catch(Exception e){};
		}
		
		String ftpPrompts = props.getProperty(FTP_PROMPTS);
		String[] ftpPromptsAsStringArray = StringUtils.split(ftpPrompts, ";, ");
		ftpPromptsAsStringArray = StringUtils.mergeStringArrays(new String[][]{ftpPromptsAsStringArray,defaultFTPPrompts}); 
		ftpGeneralPrompts = stringArrayToPrompts(ftpPromptsAsStringArray); 

		String _ftpLoginPrompts = props.getProperty(FTP_LOGIN_PROMPTS);
		String[] ftpLoginPromptsAsStringArray = StringUtils.split(_ftpLoginPrompts, ";, ");
		ftpLoginPromptsAsStringArray = StringUtils.mergeStringArrays(new String[][]{ftpLoginPromptsAsStringArray,defaultLoginPrompts}); 
		ftpLoginPrompts = stringArrayToPrompts(ftpLoginPromptsAsStringArray);
		
		String _ftpPasswordPrompts = props.getProperty(FTP_PASSWORD_PROMPTS);
		String[] ftpPasswordPromptsAsStringArray = StringUtils.split(_ftpPasswordPrompts, ";, ");
		ftpPasswordPromptsAsStringArray = StringUtils.mergeStringArrays(new String[][]{ftpPasswordPromptsAsStringArray,defaultPasswordPrompts}); 
		ftpPasswordPrompts  = stringArrayToPrompts(ftpPasswordPromptsAsStringArray);
	}
	
	private String[] promptsToStringArray(Prompt[] prompts){
		if (prompts == null){
			return new String[0];
		}
		String[] res = new String[prompts.length];
		int i=0;
		for (Prompt p:prompts){
			res[i]=p.getPrompt();
			i++;
		}
		return res;
	}
	
	private Prompt[] stringArrayToPrompts(String[] promptsAsString){
		if (promptsAsString == null){
			return new Prompt[0];
		}
		Prompt[] res = new Prompt[promptsAsString.length];
		int i=0;
		for (String s:promptsAsString){
			res[i]=new Prompt(s,false);
			res[i].setCommandEnd(true);
			i++;
		}
		return res;
	}

	private String getFTPServerAddress(){
		if (!StringUtils.isEmpty(getFtpServerHostName())){
			return getFtpServerHostName();
		}
		if (!StringUtils.isEmpty(JSystemProperties.getInstance().getPreference(LOCAL_HOST_ADDRESS_PROPERTY))){
			return JSystemProperties.getInstance().getPreference(LOCAL_HOST_ADDRESS_PROPERTY);
		}
		return localMachine.getHostName();
	}
	/**********************************************************************
	 *  FTPRemoteClient setters and getters
	 *********************************************************************/
	
	public String getHost() {
		return host;
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
	
	public void setHost(String remoteHost) {
		this.host = remoteHost;
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
	
	public String getFtpServerHostName() {
		return ftpServerHostName;
	}

	public void setFtpServerHostName(String ftpServerHostName) {
		this.ftpServerHostName = ftpServerHostName;
	}

	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	public boolean isAscii() {
		return ascii;
	}
	public void setAscii(boolean ascii) {
		this.ascii = ascii;
	}
	
}
