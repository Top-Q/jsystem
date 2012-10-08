/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.reports;

import junit.framework.SystemTestCase;

public class TestReportsPublish extends SystemTestCase{

	private String param1 = "param1Value";
	private int    param2 = 100;
	
	/**
	 * testTestTrafficOnPort javadoc
	 */
	public void testTestTrafficOnPort(){
		report.step("step 1");
		report.step("step 2");
		report.step("step 3");
	}
	
	/**
	 * testTestTrafficWithHttp javadoc
	 */
	public void testTestTrafficWithHttp(){
		report.step("step 1");
		report.step("step 2");
		report.step("step 3");
	}
	
	/**
	 * testTestTrafficWithHttps javadoc
	 */
	public void testTestTrafficWithHttps(){
		report.step("step 1");
		report.step("step 2");
		report.step("step 3");
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public int getParam2() {
		return param2;
	}

	public void setParam2(int param2) {
		this.param2 = param2;
	}

}
