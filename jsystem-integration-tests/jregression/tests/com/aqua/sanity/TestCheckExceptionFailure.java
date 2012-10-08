package com.aqua.sanity;

import junit.framework.Assert;
import junit.framework.SystemTestCase;

public class TestCheckExceptionFailure extends SystemTestCase {

	public TestCheckExceptionFailure() {
		super();
	}
	
	public void testExceptionFailure()throws Exception{
		throw new Exception("this is the exception");
	}
	
	public void testFileAssert()throws Exception{
		Assert.assertTrue("this is the assert fail message",false);
	}
}
