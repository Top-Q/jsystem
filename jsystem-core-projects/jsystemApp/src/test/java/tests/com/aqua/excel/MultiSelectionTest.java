/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import junit.framework.SystemTestCase;

public class MultiSelectionTest extends SystemTestCase {
	Test[] tests;
	Test test2;
	public Test getTest2() {
		return test2;
	}
	public void setTest2(Test test2) {
		this.test2 = test2;
	}
	public Test[] getTests() {
		return tests;
	}
	public void setTests(Test[] tests) {
		this.tests = tests;
	}
	public MultiSelectionTest(){
		super();
		tests = new Test[2];
		tests[0] = new Test();
		tests[1] = new Test();
	}
	
	public void testMultiSelection(){
		
	}
}
