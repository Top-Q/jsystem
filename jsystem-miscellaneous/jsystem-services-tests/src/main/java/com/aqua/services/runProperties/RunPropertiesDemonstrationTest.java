/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.runProperties;

import jsystem.framework.RunProperties;
import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

public class RunPropertiesDemonstrationTest extends SystemTestCase {

	public RunPropertiesDemonstrationTest(){
	}
	
	/**
	 * Temporary summary properties are properties which are
	 * added to the summary report of the current run.
	 * In the next run temporary summary properties are 
	 * deleted.
	 * 
	 * System temporary summary properties:
	 * 1. Version,Build - will appear in the publish data dialog
	 */
	public void testTemporarySummaryProperty() throws Exception {
		Summary.getInstance().setVersion("1.2.1");	
		Summary.getInstance().setTempProperty("tempProperty", "tempPropValue");
		Summary.getInstance().setTempProperty("Build", "myBuild");
	}

	/**
	 * Summary properties are properties which are
	 * added to the summary report of all runs until 
	 * property is set to empty string.
	 */
	public void testSummaryProperty() throws Exception {
		Summary.getInstance().setProperty("permanentProperty", "propValue");
		Summary.getInstance().setProperty("Station", "station1");
		
	}

	/**
	 * Run properties are properties which
	 * purpose is to help tests to share information.
	 * When running in run.mode 1 (1 jvm for all tests) , this is not a problem since
	 * tests can share data in JVM's memory, but when running in run.mode 2,4 
	 * (jvm for each test and jvm for each internal scenario), tests can't
	 * share data in JVM's memory, this is when the run properties can be used.
	 */
	public void testRunProperties() throws Exception {
		RunProperties.getInstance().setRunProperty("myProp", "val1");
		
	}

	/**
	 * When running this test after {@link #testRunProperties()},
	 * the value of val will be 'val1'
	 */
	public void testRunPropertiesSecondTest() throws Exception {
		String val = RunProperties.getInstance().getRunProperty("myProp");
		assertEquals(val,"val1");
	}

}
