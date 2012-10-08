/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.net.URL;

import junit.framework.SystemTestCase;

import org.apache.commons.io.FileUtils;

/**
 * Basic test that checks the FTPFileTransfer SystemObject when both local and remote machine
 * are the same machine. 
 */
public class LinuxFTPFileTransferTest extends SystemTestCase  {
	
	private long sleep;
	private boolean ascii;
	protected FTPFileTransfer fTPFileTransfer;

	public void setUp() throws Exception {
		super.setUp();
		fTPFileTransfer = (FTPFileTransfer)system.getSystemObject(getRemoteMachineSUTTag());
		fTPFileTransfer.setAscii(isAscii());
	}	

	public void tearDown() throws Exception {
		fTPFileTransfer.closeFileTransferSession();
		super.tearDown();
	}
	
	public void testCopyFileFromLocalMachineToRemoteMachineInputStreamFile() throws Exception{
		File f = new File("FTPFileTransferTest.class");
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(getClass().getResourceAsStream("FTPFileTransferTest.class"),f);
	}

	public void testSleep() throws Exception{
		Thread.sleep(sleep);
	}
	
	public void testCopyFileFromLocalMachineToRemoteMachineFile() throws Exception{
		File source = new File("testCopyFileFromLocalMachineToRemoteMachineFileFile.class");
		File destination = new File("/root/testCopyFileFromLocalMachineToRemoteMachineFileFile_dest.class");
		destination.delete();
		assertTrue(!destination.exists());
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source,destination);
		source.delete();
	}

	public void _testCopyFileFromRemoteMachineToLocalMachine() throws Exception {
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

	
	public String getRemoteMachineSUTTag(){
		return "filetransfer_linux";
	}
	public boolean isAscii() {
		return ascii;
	}
	public void setAscii(boolean ascii) {
		this.ascii = ascii;
	}
	public long getSleep() {
		return sleep;
	}
	public void setSleep(long sleep) {
		this.sleep = sleep;
	}
}
