/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * In order to avoid parsing the annotations ourselves, we inherit an internal
 * class from JUnit that does that and modify its functions for public access.
 * 
 * @author Gooli
 */
public class JUnit4TestCaseParser extends TestClass {

	public JUnit4TestCaseParser(Class<?> klass) {
		super(klass);
	}
	
	@Override
	public List<Method> getTestMethods() {
		return getAnnotatedMethods(Test.class);
	}

	@Override
	public List<Method> getBefores() {
		return getAnnotatedMethods(BeforeClass.class);
	}

	@Override
	public List<Method> getAfters() {
		return getAnnotatedMethods(AfterClass.class);
	}	

}
