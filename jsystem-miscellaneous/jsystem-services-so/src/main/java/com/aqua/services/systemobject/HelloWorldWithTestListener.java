/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.systemobject;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class HelloWorldWithTestListener extends SystemObjectImpl implements TestListener{
	
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

	@Override
	public void addError(Test arg0, Throwable arg1) {
		report.report("addError"); 
		
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		report.report("addFailure");
		
	}

	@Override
	public void endTest(Test arg0) {
		report.report("endTest");
	}

	@Override
	public void startTest(Test arg0) {
		report.report("startTest");
		
	}
}
