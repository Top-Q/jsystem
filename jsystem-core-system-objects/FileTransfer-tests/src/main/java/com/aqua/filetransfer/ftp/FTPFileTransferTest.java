/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;

import com.aqua.filetransfer.ftp.FTPFileTransfer;

import junit.framework.SystemTestCase;

/**
 * Basic test that checks the FTPFileTransfer SystemObject when both local and remote machine
 * are the same machine. 
 */
public class FTPFileTransferTest extends SystemTestCase  {
	
	protected FTPFileTransfer fTPFileTransfer;
	
	/**
	 */
	public void setUp() throws Exception {
		super.setUp();
		fTPFileTransfer = (FTPFileTransfer)system.getSystemObject(getRemoteMachineSUTTag());
	}

	/**
	 */
	public void tearDown() throws Exception {
		fTPFileTransfer.closeFileTransferSession();
	}

	public void testCopyFileFromLocalMachineToRemoteMachineInputStreamFile() throws Exception{
		File f = new File("c:/FTPFileTransferTest.class");
		f.delete();
		assertTrue(!f.exists());
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(getClass().getResourceAsStream("FTPFileTransferTest.class"),f);
		assertTrue(f.exists());
		assertTrue(f.delete());
	}

	public void testCopyFileFromLocalMachineToRemoteMachineAsciiMode() throws Exception{
		File source = new File("testCopyFileFromLocalMachineToRemoteMachineFileFile.class");
		File destination = new File("c:/testCopyFileFromLocalMachineToRemoteMachineFileFile_dest.class");
		destination.delete();
		assertTrue(!destination.exists());
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		fTPFileTransfer.setAscii(true);
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source,destination);
		assertTrue(destination.exists());
		assertTrue(destination.delete());
		source.delete();
	}

	public void testCopyFileFromLocalMachineToRemoteMachineFile() throws Exception{
		File source = new File("testCopyFileFromLocalMachineToRemoteMachineFileFile.class");
		File destination = new File("c:/testCopyFileFromLocalMachineToRemoteMachineFileFile_dest.class");
		destination.delete();
		assertTrue(!destination.exists());
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source,destination);
		assertTrue(destination.exists());
		assertTrue(destination.delete());
		source.delete();
	}

	public void testCopyFileFromLocalMachineToRemoteMachineWithOpenSession() throws Exception{
		File source = new File("testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse.class");
		File destination = new File("c:/testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse_dest.class");
		File source2 = new File("testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse2.class");
		File destination2 = new File("c:/testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse2_dest.class");
		File source3 = new File("testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse3.class");
		File destination3 = new File("c:/testCopyFileFromLocalMachineToRemoteMachineFileFileWithOpenSessionFalse3_dest.class");
		
		destination.delete();
		assertTrue(!destination.exists());
		destination2.delete();
		assertTrue(!destination2.exists());
		destination3.delete();
		assertTrue(!destination3.exists());
		
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		FileUtils.copyURLToFile(url,source2);
		FileUtils.copyURLToFile(url,source3);
		fTPFileTransfer.closeFileTransferSession();
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source,destination);
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source2,destination2);
		fTPFileTransfer.copyFileFromLocalMachineToRemoteMachine(source3,destination3);
		fTPFileTransfer.closeFileTransferSession();
		assertTrue(destination.exists());
		assertTrue(destination.delete());
		assertTrue(destination2.exists());
		assertTrue(destination2.delete());
		assertTrue(destination3.exists());
		assertTrue(destination3.delete());
		source.delete();
		source2.delete();
		source3.delete();
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

	public void testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse() throws Exception {
		File source = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse.class");
		File destination = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse_dest.class");
		File source2 = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse2.class");
		File destination2 = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse_dest2.class");
		File source3 = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse3.class");
		File destination3 = new File("c:/testCopyFileFromRemoteMachineToLocalMachineWithOpenSessionFalse_dest3.class");
		destination.delete();
		assertTrue(!destination.exists());
		destination2.delete();
		assertTrue(!destination2.exists());
		destination3.delete();
		assertTrue(!destination3.exists());
		URL url = getClass().getResource("FTPFileTransferTest.class");
		FileUtils.copyURLToFile(url,source);
		FileUtils.copyURLToFile(url,source2);
		FileUtils.copyURLToFile(url,source3);
		fTPFileTransfer.closeFileTransferSession();
		fTPFileTransfer.copyFileFromRemoteMachineToLocalMachine(source, destination);
		fTPFileTransfer.copyFileFromRemoteMachineToLocalMachine(source2, destination2);
		fTPFileTransfer.copyFileFromRemoteMachineToLocalMachine(source3, destination3);
		assertTrue(destination.exists());
		assertTrue(destination.delete());
		assertTrue(destination2.exists());
		assertTrue(destination2.delete());
		assertTrue(destination3.exists());
		assertTrue(destination3.delete());
		source.delete();
		source2.delete();
		source3.delete();
		
	}
	
	public String getRemoteMachineSUTTag(){
		return "filetransfer_windows";
	}
}
