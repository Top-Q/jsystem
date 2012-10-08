/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;

import jsystem.framework.JSystemProperties;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

public class FTPClientTest extends SystemTestCase {

	private static FTPServer server;
	private FTPClient client;
	private File tempFile;
	
	public void setUp() throws Exception {
		if (server == null){
			JSystemProperties.getInstance().setPreference("com.aqua.filetransfer.ftp.ftpserver","127.0.0.1");
			server = new FTPServer();
			server.init();
			server.startServer();
		}
		client = (FTPClient)system.getSystemObject("ftpClient");
		client.connect();
	}
	
	public void tearDown() throws Exception{
		if (tempFile != null){
			tempFile.delete();
		}
		if (client != null){
			client.disconnect();
		}
	}
	public void testSleep() throws Exception {
		sleep(60000);
	}
	public void testPutFile() throws Exception {
		File serverPath = server.getServerRootDirectory();
		File fileInServer = new File(serverPath,"testPutFile.txt");
		assertTrue(!fileInServer.exists() || fileInServer.delete());
		tempFile = File.createTempFile("ftpClient",".txt");
		client.putFile(tempFile.getPath(),"testPutFile.txt");
		assertTrue(fileInServer.exists());
	}

	public void testGetFile()  throws Exception {
		tempFile = new File("testGetFile.txt");
		assertTrue(!tempFile.exists() || tempFile.delete());
		File serverPath = server.getServerRootDirectory();
		File fileInServer = new File(serverPath,"testGetFileRemote.txt");
		FileUtils.write(fileInServer.getPath(), "shalom");
		client.getFile("testGetFileRemote.txt",tempFile.getPath());
		assertTrue(tempFile.exists());
	}
	
	public void testMoveFile() throws Exception {
		File serverPath = server.getServerRootDirectory();
		tempFile = new File(serverPath,"testMoveFileAfterMove.txt");
		assertTrue(!tempFile.exists() || tempFile.delete());
		File fileInServer = new File(serverPath,"testMoveFile.txt");
		FileUtils.write(fileInServer.getPath(), "shalom");
		client.moveFile("testMoveFile.txt", "testMoveFileAfterMove.txt");
		assertTrue(tempFile.exists());
		assertTrue(!fileInServer.exists());
	}

	public void testDeleteFile()  throws Exception{
		File serverPath = server.getServerRootDirectory();
		File fileInServer = new File(serverPath,"testDeleteFile.txt");
		FileUtils.write(fileInServer.getPath(), "shalom");
		client.deleteFile("testDeleteFile.txt");
		assertTrue(!fileInServer.exists());
	}

	public void testMakeDirectory() throws Exception{
		File serverPath = server.getServerRootDirectory();
		File fileInServer = new File(serverPath,"testMakeDirectory/testMakeDirectory.txt");
		FileUtils.deltree(new File(serverPath,"testMakeDirectory"));
		assertTrue(!fileInServer.exists() || fileInServer.delete());
		tempFile = File.createTempFile("testMakeDirectory",".txt");
		client.makeDirectory("testMakeDirectory");
		client.putFile(tempFile.getPath(),"testMakeDirectory/testMakeDirectory.txt");
		assertTrue(fileInServer.exists());
	}

}
