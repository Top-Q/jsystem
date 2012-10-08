/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.systemobject;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;

public class HelloWorld extends SystemObjectImpl{
	
	private String message;
	private String fileName;
	public void init() throws Exception {
		super.init();
		report.report("Hello world init");
	}
	
	public void close(){
		report.report("Hello world close");
		super.close();
	}
	
	public void getHelloMessage() throws Exception {
		report.report("Hello Message",getMessage(),true);
	}

	public void readFromFile() throws Exception {
		String textFromFile = FileUtils.read(getFileName());
		setTestAgainstObject(textFromFile);
	}

	public void writeToFile(String text) throws Exception {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
