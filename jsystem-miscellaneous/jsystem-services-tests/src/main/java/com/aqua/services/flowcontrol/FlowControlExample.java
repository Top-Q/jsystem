/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.flowcontrol;

import java.util.Random;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class FlowControlExample extends SystemTestCase4 {
	
	private int numberOfFiles;
	private int expectedNumberOfFiles;
	private String value;

	@Test
	public void activateTestedApplication() throws Exception{
		report.step("activating tested application");
	}

	@Test
	public void addUserToApplication() throws Exception{
		report.step("adding user ----------------------------------" + getValue());
	}

	@Test
	public void restartApplication() throws Exception{
		report.step("restart application");
	}

	@Test
	public void makeAWarning() throws Exception{
		report.report("stam warning",Reporter.WARNING);
	}

	@Test
	public void testThatMightFail() throws Exception{
		report.step("Tossing a boolean value");
		boolean exception = new Random(System.currentTimeMillis()).nextBoolean();
		if (exception){
			throw new Exception("example fail");
		}
		report.report("Did not throw an exception");
	}

	@Test
	@TestProperties(returnParam={"numberOfFiles","expectedNumberOfFiles"})
	public void checkResultsFolder() throws Exception{
		report.step("Checking results folder");
		if (getNumberOfFiles() == -1){
			setNumberOfFiles(new Random(System.currentTimeMillis()).nextInt(4));
		}
		report.step("---------------   " + getNumberOfFiles() + "   ----------------------");
	}
		
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public int getExpectedNumberOfFiles() {
		return expectedNumberOfFiles;
	}

	public void setExpectedNumberOfFiles(int expectedNumberOfFiles) {
		this.expectedNumberOfFiles = expectedNumberOfFiles;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
