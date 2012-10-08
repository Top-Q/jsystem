/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import junit.framework.SystemTestCase;

/**
 * Basic test that checks the FTPFileTransfer SystemObject when both local and remote machine
 * are the same machine. 
 */
public class FTPServerPropertiesTest extends SystemTestCase  {
	
	private FTPFileTransfer fTPFileTransfer;
	private FTPServer server;

	/**
	 */
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 */
	public void tearDown() throws Exception {

	}

	public void testFtpServerWithProps() throws Exception{
		server = (FTPServer)system.getSystemObject("ftpserver_with_props");
		server.startServer();
		server = (FTPServer)system.getSystemObject("ftpserver_with_props2");
		server.startServer();
	}
	
	public void testFTPFileTransferWithProps() throws Exception{
		fTPFileTransfer = (FTPFileTransfer)system.getSystemObject("filetransfer_with_props");
	}

}
