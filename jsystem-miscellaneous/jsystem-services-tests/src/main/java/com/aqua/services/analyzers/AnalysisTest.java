/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.analyzers;

import junit.framework.SystemTestCase;

import com.aqua.services.demo.WindowsStation;

/**
 * Demonstrates analysis capabilities.
 * Issues that are covered:
 * 
 * 1. basic analysis usage
 * 2. ignore exception
 * 3. silent
 */
public class AnalysisTest extends SystemTestCase {
	private WindowsStation station;
	
	private String fileToFind = "golan.cap";
	private String folder = "";
	
	public void setUp() throws Exception {
		station = (WindowsStation) system.getSystemObject("station");
		station.setThrowException(true);
		report.setFailToPass(false);
	}

	/**
	 * Checks whether file <code>fileToFind</code> exists in 
	 * <code>folder</code>.
	 * Demonstrates basic analysis usage
	 * @params.include fileToFind,folder
	 */
	public void testIsFileExists() throws Exception {
		station.dir(getFolder());
		station.analyze(new SimpleTextFinder(getFileToFind()));
	}

	/**
	 * Demonstrates analysis usage with ignore exceptions
	 * @params.include fileToFind,folder
	 */
	public void testIsFileExistsIgnoreException() throws Exception {
		station.setThrowException(false);
		station.dir(getFolder());
		SimpleTextFinder analyzer = new SimpleTextFinder(getFileToFind());
		station.analyze(analyzer,true,false);
	}

	/**
	 * Demonstrates analayzer activation without exception/log
	 * @params.include fileToFind,folder
	 */
	public void testIsFileExistsCheckAnalysisResults() throws Exception {
		station.dir(getFolder());
		boolean res = station.isAnalyzeSuccess(new SimpleTextFinder(getFileToFind()));
		report.report("Analysis results = " + res);
	}

	/**
	 * Demonstrates analysis which results with warning and 
	 * not with failure.
	 * @params.include fileToFind,folder
	 */
	public void testIsFileExistsWarning() throws Exception {
		station.dir(getFolder());
		station.analyze(new SimpleTextFinder(getFileToFind()),false,false,true);
	}

	public String getFileToFind() {
		return fileToFind;
	}

	/**
	 * My description
	 */
	public void setFileToFind(String fileToFind) {
		this.fileToFind = fileToFind;
	}
	
	/**
	 * My description
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * My description
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

}

