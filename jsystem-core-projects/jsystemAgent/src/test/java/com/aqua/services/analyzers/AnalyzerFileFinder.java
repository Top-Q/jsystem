/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.analyzers;

import java.io.File;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * FileFinder analyzer
 * 
 * @author KobiG
 */

public class AnalyzerFileFinder extends AnalyzerParameterImpl {

	private String fileName = "";
	private boolean checkIfFileExists = true;
	private String messageFromUser;

	public AnalyzerFileFinder(String fileName) {
		this.fileName = fileName;
	}

	public AnalyzerFileFinder(String fileName,String message) {
		this.fileName = fileName;
		this.messageFromUser = message;
	}
	
	public AnalyzerFileFinder(String fileName, boolean checkIfFileExists, String message) {
		this.fileName = fileName;
		this.checkIfFileExists = checkIfFileExists;
		this.messageFromUser = message;
		}
	
	public AnalyzerFileFinder(String fileName, boolean checkIfFileExists) {
		this.fileName = fileName;
		this.checkIfFileExists = checkIfFileExists;
		}

//	public void analyze(){
//		analyze(null);
//	}
	
	@Override
	public void analyze() {

		File checkFile = new File(testAgainst.toString() + "/" + fileName);
		
		if (checkIfFileExists) { //positive check
			if (checkFile.exists()) {
				title = "File " + testAgainst.toString() + "/" + fileName
						+ " exists - as expected";
				message = messageFromUser;
				status = true;
			} else {
				title = "File " + testAgainst.toString() + "/" + fileName
						+ " doesn't exists - not as expected";
				status = false;
			}
		} else {  //negative check
			if (checkFile.exists()) {
				title = "File " + testAgainst.toString() + "/" + fileName
						+ " exists - not as expected";
				status = false;
			} else {
				title = "File " + testAgainst.toString() + "/" + fileName
						+ " doesn't exists - as expected";
				message = messageFromUser;
				status = true;
			}
		}

	}

}
