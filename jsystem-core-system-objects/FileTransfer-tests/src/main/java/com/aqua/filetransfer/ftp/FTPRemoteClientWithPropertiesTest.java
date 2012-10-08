/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.ftp;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

import jsystem.utils.ReflectionUtils;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;
import systemobject.terminal.Prompt;

public class FTPRemoteClientWithPropertiesTest extends SystemTestCase {

	protected FTPRemoteClient ftpRemoteClient;
	protected FTPServer ftpServer;

	/**
	 */
	public void setUp() throws Exception {
		super.setUp();
		createFileTransferPropertiesFile();
		ftpRemoteClient = (FTPRemoteClient)system.getSystemObject(getRemoteMachineSUTTag());
	}

	/**
	 */
	public void testCopyFileFromServerToRemoteClient() throws Exception{
		
		Field genralPromptsField = getField("ftpGeneralPrompts");
		Prompt[] generalPromptsObj = (Prompt[])genralPromptsField.get(ftpRemoteClient);
		assertTrue(generalPromptsObj.length == 4);
		String[] generalPrompts = new String[generalPromptsObj.length];
		int counter=0;
		for (Prompt p:generalPromptsObj){
			generalPrompts[counter]=p.getPrompt();
			counter++;
		}
		Set set = StringUtils.stringArrayToSet(generalPrompts);
		assertTrue(set.contains("ftp1"));
		assertTrue(set.contains("ftp2"));
		assertTrue(set.contains("ftp3"));
		assertTrue(set.contains("ftp>"));

		
		Field loginPromptsField = getField("ftpLoginPrompts");
		Prompt[] loginPromptsObj = (Prompt[])loginPromptsField.get(ftpRemoteClient);
		assertTrue(loginPromptsObj.length == 4);
		String[] loginPrompts = new String[loginPromptsObj.length];
		counter=0;
		for (Prompt p:loginPromptsObj){
			loginPrompts[counter]=p.getPrompt();
			counter++;
		}
		
		set = StringUtils.stringArrayToSet(loginPrompts);
		assertTrue(set.contains("login1"));
		assertTrue(set.contains("login2"));
		assertTrue(set.contains("login3"));
		assertTrue(set.contains("):"));

		Field passwordPromptsField = getField("ftpPasswordPrompts");
		Prompt[] passwordPromptsObj = (Prompt[])passwordPromptsField.get(ftpRemoteClient);
		assertTrue(passwordPromptsObj.length == 5);
		String[] passwordPrompts = new String[passwordPromptsObj.length];
		counter=0;
		for (Prompt p:passwordPromptsObj){
			passwordPrompts[counter]=p.getPrompt();
			counter++;
		}

		set = StringUtils.stringArrayToSet(passwordPrompts);
		assertTrue(set.contains("password1"));
		assertTrue(set.contains("password2"));
		assertTrue(set.contains("password3"));
		assertTrue(set.contains("for "+ftpRemoteClient.getFtpUserName()));
	}
	
	public String getRemoteMachineSUTTag(){
		return "remoteclient_windows";
	}
	
	private void createFileTransferPropertiesFile() throws Exception {
		Properties props = new Properties();
		props.put(FTPRemoteClient.FTP_PROMPTS,"ftp1,ftp2,ftp3");
		props.put(FTPRemoteClient.FTP_LOGIN_PROMPTS,"login1,login2,login3");
		props.put(FTPRemoteClient.FTP_PASSWORD_PROMPTS,"password1,password2,password3");
		FileOutputStream outStream = new FileOutputStream(FTPRemoteClient.FILE_TRANSFER_PROPERTIES_FILE_NAME);
		try {
			props.store(outStream,"");
		}finally {
			outStream.close();
		}
	}	
	
	private Field getField(String field) throws Exception {
		return ReflectionUtils.getField(field, FTPRemoteClient.class); 
	}
}
