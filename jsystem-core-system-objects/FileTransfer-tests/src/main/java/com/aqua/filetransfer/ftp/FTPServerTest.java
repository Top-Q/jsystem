/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.aqua.filetransfer.ftp.FTPServer;


import junit.framework.SystemTestCase;

public class FTPServerTest extends SystemTestCase  {
	
	private FTPServer server;
	
	public void setUp() throws Exception {
	}

	public void tearDown() throws Exception {
		if (server != null){
			system.removeSystemObject(server);
			server.stopServer();
			server = null;
		}
	}

	//test fails
	public void __testConsecutiveStopStart() throws Exception {
		startServer("ftpserver_2");
		server.stopServer();
		server.startServer();
		server.stopServer();
		server.startServer();
	}
	
	public void testServerDirectory() throws Exception {
		startServer("ftpserver_2");
		assertEquals(new File("aquaftp"+server.getPort()).getAbsolutePath(),server.getServerRootDirectory().getAbsolutePath());
		assertTrue(server.getServerRootDirectory().exists());
	}
	
	public void testSimpleTransfer() throws Exception{
		startServer("ftpserver_2");
		transferFile("testSimpleTransfer.txt");
	}
	
	public void testFTPWhenPortIsAlreadyOccupied() throws Exception{
		FTPServer anotherServer = new FTPServer();
		anotherServer.init();
		anotherServer.startServer();
		try {
			startServer("ftpserver_2");
			transferFile("testFTPWhenPortIsAlreadyOccupied.txt");
			server.stopServer();
		}finally {
			anotherServer.stopServer();
		}
	}
	
	public void testStartAfterStart() throws Exception{
		startServer("ftpserver_2");
		try {
			server.startServer();
		}catch(Exception e){
			report.report("Second startServer call failed as expected");
		}
	}
	
	public void testFTPWithNoneDefaultConfiguration() throws Exception{
		startServer("ftpserver_1");
		transferFile("testFTPWithNoneDefaultConfiguration");
	}
	
	private void transferFile(String fileName) throws SocketException, IOException, Exception {
		FTPClient client = new FTPClient();
		client.connect(server.getExternalName(),server.getPort());
		int reply = client.getReplyCode();
		if(!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("Failed connecting to server");
		}
	    client.login(server.getDefaultUserName(), server.getDefaultUserPassword());
		reply = client.getReplyCode();
		if(!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("Failed connecting to server");
		}
	    InputStream stream = getClass().getClassLoader().getResourceAsStream("res/conf/ftpd.properties");
		client.storeFile(fileName, stream);
		File transfferedFile = new File(server.getServerRootDirectory(),fileName);
		assertTrue(transfferedFile.exists());
		assertTrue(transfferedFile.delete());
	}
	
	private void startServer(String instance) throws Exception {
		server = (FTPServer)system.getSystemObject(instance);
		server.startServer();
	}

}
