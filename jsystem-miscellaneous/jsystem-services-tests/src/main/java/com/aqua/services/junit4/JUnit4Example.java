/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.junit4;

import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JUnit4Example  extends SystemTestCase4{
	
	private String param;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		report.report("setUpBeforeClass");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		report.report("tearDownAfterClass");
		
	}

	@Before
	public void setUp() throws Exception {
		report.report("setUp");
		
	}

	@After
	public void tearDown() throws Exception {
		report.report("tearDown");
		
	}

	@Test
	public void myTestMethod() throws Exception{
		report.report("myTestMethod");
	}
	
	@Test
	@Ignore
	public void myTestMethodThatIsIgnored(){
		report.report("myTestMethodThatIsIgnored");
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

}
