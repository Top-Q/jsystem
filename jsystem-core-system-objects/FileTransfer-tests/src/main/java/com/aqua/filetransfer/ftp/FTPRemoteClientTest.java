/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.net.URL;

import jsystem.framework.JSystemProperties;
import junit.framework.SystemTestCase;

import org.apache.commons.io.FileUtils;

public class FTPRemoteClientTest extends SystemTestCase {

	protected FTPRemoteClient ftpRemoteClient;
	protected FTPServer ftpServer;
	
	/**
	 */
	public void setUp() throws Exception {
		super.setUp();
		ftpServer = (FTPServer)system.getSystemObject("ftpserver_2");
		ftpServer.startServer();
		ftpRemoteClient = (FTPRemoteClient)system.getSystemObject(getRemoteMachineSUTTag());
	}

	/**
	 */
	public void tearDown() throws Exception {
		ftpRemoteClient.closeFTPSession();
	}

	public void testCopyFileFromServerToRemoteClient() throws Exception{
		URL url = getClass().getResource("FTPRemoteClientTest.class");
		FileUtils.copyURLToFile(url, new File(ftpServer.getDefaultUserHomeDirectory(),"FTPRemoteClientTest.class"));
		ftpRemoteClient.copyFileFromLocalMachineToRemoteClient("FTPRemoteClientTest.class","./FTPRemoteClientTest2.class");
	}

	public void testCopyFileFromServerToRemoteClientWithJsystemProperties() throws Exception{
		URL url = getClass().getResource("FTPRemoteClientTest.class");
		FileUtils.copyURLToFile(url, new File(ftpServer.getDefaultUserHomeDirectory(),"FTPRemoteClientTest.class"));
		String ftpServer = ftpRemoteClient.getFtpServerHostName();
		try {
			JSystemProperties.getInstance().setPreference(FTPRemoteClient.LOCAL_HOST_ADDRESS_PROPERTY, "128.0.0.1");
			ftpRemoteClient.copyFileFromLocalMachineToRemoteClient("FTPRemoteClientTest.class","./FTPRemoteClientTest2.class");
			assertTrue(false);
		}catch (Exception e){
			//good
		}finally {
			ftpRemoteClient.setFtpServerHostName(ftpServer);
			JSystemProperties.getInstance().setPreference(FTPRemoteClient.LOCAL_HOST_ADDRESS_PROPERTY, "");
		}
	}

	/**
	 * Verifies that coping file from remote machine to local machine
	 * works as expected.
	 * Depends on {@link #testCopyFileFromServerToRemoteClient()} 
	 */
	public void testCopyFileFromRemoteClientToServer() throws Exception{
		File f = new File(ftpServer.getDefaultUserHomeDirectory(),"FTPRemoteClientTest3.class");
		f.delete();
		assertTrue(!f.exists());
		ftpRemoteClient.copyFileFromRemoteClientToLocalMachine(".\\FTPRemoteClientTest2.class","FTPRemoteClientTest3.class");
		assertTrue(f.exists());
		assertTrue(f.delete());
		ftpRemoteClient.closeFTPSession();
	}
	
	
	public String getRemoteMachineSUTTag(){
		return "remoteclient_windows";
	}
}
