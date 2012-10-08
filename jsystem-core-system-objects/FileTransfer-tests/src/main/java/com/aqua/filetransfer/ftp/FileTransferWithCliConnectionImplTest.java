/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import junit.framework.SystemTestCase;

public class FileTransferWithCliConnectionImplTest extends SystemTestCase {

	protected FTPFileTransfer fTPFileTransfer;
	protected FTPRemoteClient ftpRemoteClient;
	protected FTPServer ftpServer;

	/**
	 */
	public void setUp() throws Exception {
		super.setUp();
		ftpServer = (FTPServer)system.getSystemObject("ftpserver_2");
		ftpServer.startServer();
		ftpRemoteClient = (FTPRemoteClient)system.getSystemObject("remoteClientWithCliConnection");
		fTPFileTransfer = (FTPFileTransfer)system.getSystemObject("fileTransferWithCliConnection");
	}

	/**
	 */
	public void tearDown() throws Exception {
		ftpRemoteClient.closeFTPSession();
		fTPFileTransfer.closeFileTransferSession();
	}

	/**
	 */
	public void testCopyFileFromServerToRemoteClient() throws Exception{
		URL url = getClass().getResource("FTPRemoteClientTest.class");
		FileUtils.copyURLToFile(url, new File(ftpServer.getDefaultUserHomeDirectory(),"FTPRemoteClientTest.class"));
		ftpRemoteClient.copyFileFromLocalMachineToRemoteClient("FTPRemoteClientTest.class","./FTPRemoteClientTest2.class");
	}

	public void testCopyFileFromRemoteMachineToLocalMachine() throws Exception {
		File source = new File("c:/testCopyFileFromRemoteMachineToLocalMachine.class");
		File destination = new File("c:/testCopyFileFromRemoteMachineToLocalMachine_dest.class");
		destination.delete();
		assertTrue(!destination.exists());
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		fTPFileTransfer.copyFileFromRemoteMachineToLocalMachine(source, destination);
		assertTrue(destination.exists());
		assertTrue(destination.delete());
		source.delete();
	}
	
}
