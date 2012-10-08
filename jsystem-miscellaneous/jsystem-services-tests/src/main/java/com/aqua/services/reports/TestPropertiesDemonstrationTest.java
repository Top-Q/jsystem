/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.reports;

import java.io.File;
import java.util.Random;

import jsystem.framework.report.ReporterHelper;
import junit.framework.SystemTestCase;

public class TestPropertiesDemonstrationTest extends SystemTestCase {

	public void setUp() throws Exception {
	}

	/**
	 * Demonstrates how to add a test property.
	 * Tests properties will later be shown in the reports web application
	 * in the custom report builder.
	 * The user can select properties for his custom report.
	 * Make sure you keep properties values parsable to int/float/double
	 * This will enable creating nice graphs when exporting report
	 * to excel sheet.
	 */
	public void testAddProperty() throws Exception {
		report.addProperty("packet-loss", "0.05");
		report.addProperty("bandwidth", "30");
	}
	
	/**
	 * Link properties are properties which link to a report file
	 * or to a general URL.
	 */
	public void testLinkProperties() throws Exception {
		//demonstrates usage of utility method that copies the file
		//into reports folder and adds a property link to it
		ReporterHelper.copyFileToReporterAndAddLinkProperty(report, 
				new File("jsystem.properties"), //file that will be copied to test's report folder
				"linkToFile", // name of property
				"jsystem properties"); //link's title

		//demonstrates a utility method that assumes file was
		//copied into the report folder and just adds a property link to it
		ReporterHelper.addLinkProperty(report, 
				"jsystem.properties", //name of the file that was copied to the report folder 
				"linkToFileWithoutCopy", // name of property
				"jsystem properties title"); //link's title

		//demonstrates a general link property addition
		ReporterHelper.addLinkProperty(report, "http://www.one.co.il",
				"linkToOneSports", // name of property
				"one sports"); //link's title

	}

	/**
	 * Demo benchmark report.
	 */
	public void testBenchmarkExample() throws Exception {
		double packetLoss = getPacketLoss();
		int bandwidth = getBandwidth();
		int averageCpuUsage = getAverageCpuUsage();
		int peakCpuUsage = getPeakCpuUsage();
		int averageUploadRate = getAverageUploadRate();
		int averageDownloadRate = getAverageDownloadRate();
		
		report.addProperty("packet-loss","" + packetLoss);
		report.addProperty("bandwidth", ""+ bandwidth);
		report.addProperty("average-cpu-usage", ""+ averageCpuUsage);
		report.addProperty("peak-cpu-usage", ""+ peakCpuUsage);
		report.addProperty("average-upload-rate", ""+ averageUploadRate);
		report.addProperty("average-download-rate", ""+ averageDownloadRate);
		ReporterHelper.copyFileToReporterAndAddLinkProperty(report, 
				new File("jsystem.properties"), //file that will be copied to test's report folder
				"Log", // name of property
				"Link to Log"); //link's title
		
	}

	private double getPacketLoss(){
		return  new Random().nextInt(10)+new Random().nextDouble()*10;
	}
	
	private int getBandwidth(){
		return new Random().nextInt(100);
	}
	private int getAverageCpuUsage(){
		return new Random().nextInt(100);
	}
	private int getPeakCpuUsage(){
		return new Random().nextInt(100);
	}
	private int getAverageUploadRate(){
		return new Random().nextInt(100);
	}
	private int getAverageDownloadRate(){
		return new Random().nextInt(100);
	}

}

