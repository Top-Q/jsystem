/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.examples.filetransfer;

import java.io.File;
import java.net.URL;

import junit.framework.SystemTestCase;
import org.apache.commons.io.FileUtils;
import com.aqua.filetransfer.ftp.FTPFileTransfer;
import com.aqua.filetransfer.ftp.FTPRemoteClient;

/**
 * The following jars should be added to the class path in order to work
 * with the file transfer:
     /runner/thirdparty/lib/commons-io-1.3.1.jar 
 	 /runner/thirdparty/commonLib/commons-net.jar 
	 /runner/thirdparty/lib/ftpserver-dev.jar 
	 /runner/thirdparty/lib/ganymed.jar 
	 /runner/thirdparty/commonLib/junit.jar 
	 /runner/thirdparty/lib/log4j.jar 
	 /runner/thirdparty/lib/tftp4java-client.jar 
	 /runner/thirdparty/lib/tftp4java-server.jar 
	 /runner/thirdparty/commonLib/xalan.jar 
	 /runner/thirdparty/lib/commons-logging-1.0.4.jar 
	 /runner/lib/cli.jar 
	 /runner/lib/infra.jar 
	 /runner/thirdparty/commonLib/qdox.jar 
	 /runner/lib/jsystem.jar 

   SUT file example:
   
   	<filetransfer>
		<class>com.aqua.filetransfer.ftp.FTPFileTransfer</class>
		<cliConnection>
			<class>com.aqua.sysobj.conn.LinuxDefaultCliConnection</class> 
			<user>root</user>
			<password>aquaroot</password>
			<host>10.0.0.50</host>
		</cliConnection>
		<localHostExternalName edit="enable">10.0.0.16</localHostExternalName>
	</filetransfer>

   
 * @author goland
 */
public class FileTransferExamples extends SystemTestCase {
	
	public void setUp() throws Exception {
		//assertTrue(new File("aquaftp21").delete());
	}
	
	/**
	 * Test that demonstrates creation of FTPFileTrasfer SystemObject,
	 * transfare of a file from a remote machine to this machine and addition
	 * of a link to the copied file to the html log. 
	 * The remote machine in this test case is the local machine. 
	 */
	public void testCopyFileFromRemoteMachineAndAddLink() throws Exception {
		//create and initialize system object.
		//Please note that an alternative way to init the system object is from 
		//an SUT using the system.getSystemObject method.
		FTPFileTransfer fileTransfer = (FTPFileTransfer)system.getSystemObject("filetransfer_example");
		//FTPFileTransfer fileTransfer = new FTPFileTransfer("127.0.0.1","simpleuser","simpleuser");
		/**
		    to run on linux with ssh:
		    
			fileTransfer.setPort(22);
			fileTransfer.setProtocol("ssh");
			fileTransfer.setOperatingSystem("linux");
		
		    //if the remote machine can't open ftp session to 
		    //this machine set the ip explicitelly:
			fileTransfer.setLocalHostExternalName("10.0.0.16");
		*/
		
		
		fileTransfer.init();
		
		
		try {
			//in this test case the remote machine in this machine.
			//in the makeSureFileExists() method I make sure a file to copy 
			//exists on the remote machine after that I create 
			//a source File which points to the path of the file on the remote machine
			File source = new File(makeSureFileExists());
			
			//The destination File entity points to the location to which I want to copy
			//the file. In this case it's the reports folder of the test.
			File destination = new File(report.getCurrentTestFolder(),"copyOFFtpfiletransfer.xml");
			
			//actual file copy
			fileTransfer.copyFileFromRemoteMachineToLocalMachine(source, destination);
			
			//adding a link to the file.
			report.addLink("copyOFFtpfiletransfer", "copyOFFtpfiletransfer.xml");
		}finally{
			fileTransfer.close();
		}
	}
	
	/**
	 * Demonstrates a copy of a file from one remote machine to another.
	 * In order for the operation to work an FTP server has to exist on  
	 * the remote machine.
	 * 
	 * Both remote machines in this test case are the local machine. 
	 */
	public void testCopyFromRemoteMachineToRemoteMachine() throws Exception {
		//Since we assume the FTP server runs on one of the machine, and since
		// for the purpose of this example local host is both FTP server and FTP client,
		//I'm starting the FTP server.
		startFtpServer();
		
		//making sure I have a file to copy from FTP client to the server.
		String filePathOnRemoteFTPClient = makeSureFileExists();
		
		//destination file name.
		String filePathOnRemoteFTPServer = "stam.xml";
		
		//constructing and initializing RemoteFTPClient object.
		FTPRemoteClient remoteClient = (FTPRemoteClient)system.getSystemObject("remoteclient_example");
		//FTPRemoteClient remoteClient = new FTPRemoteClient("127.0.0.1","simpleuser","simpleuser",java.net.InetAddress.getLocalHost().getHostAddress());
		remoteClient.init();
		
		//Please note that the name of the method is confusing the LocalMachine is not really
		//local machine but the machine on which the FTP server is running.
		remoteClient.copyFileFromRemoteClientToLocalMachine(filePathOnRemoteFTPClient, filePathOnRemoteFTPServer);
		assertTrue(new File("aquaftp21/stam.xml").exists());
		remoteClient.close();
	}

	
	private void startFtpServer() {
		Thread t = new Thread(){
			public void run(){
				try {
					com.aqua.filetransfer.ftp.FTPServer.main(null);
				}catch (Exception e){
					report.report("Failed initilazing FTP server",e);
				}
			}
		};
		t.start();
		sleep(3000);
	}
	
	private String makeSureFileExists() throws Exception {
		File source = new File("ftpfiletransfer.xml");
		URL url = getClass().getResource("/com/aqua/filetransfer/ftp/ftpfiletransfer.xml");
		FileUtils.copyURLToFile(url,source);
		return source.getAbsolutePath();
	}
}
