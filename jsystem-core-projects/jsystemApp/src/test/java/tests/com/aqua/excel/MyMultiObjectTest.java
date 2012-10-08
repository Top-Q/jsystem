/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import jsystem.extensions.paramproviders.GenericObjectParameterProvider;
import jsystem.extensions.paramproviders.ObjectArrayParameterProvider;
import jsystem.extensions.paramproviders.SystemObjectParameterProvider;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase;

public class MyMultiObjectTest extends SystemTestCase {
	Test[] tests;
	Test test2;
	String doc = "<sut><node><class>tests.sysobj.Obj</class></node></sut>";
	
	public String getDoc() {
		return doc;
	}
	@UseProvider(provider=SystemObjectParameterProvider.class, config={"-disableRootEdit"})
	public void setDoc(String doc) {
		this.doc = doc;
	}

	public Test getTest2() {
		return test2;
	}
	@UseProvider(provider=GenericObjectParameterProvider.class)
	public void setTest2(Test test2) {
		this.test2 = test2;
	}

	public MyMultiObjectTest(){
		super();
		tests = new Test[2];
		tests[0] = new Test();
		tests[1] = new Test();
	}
	
	public void testDoNothing(){
		
	}
	
	public Test[] getTests() {
		return tests;
	}
	@UseProvider(provider=ObjectArrayParameterProvider.class)
	public void setTests(Test[] tests) {
		this.tests = tests;
	}
}
