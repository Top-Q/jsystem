/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import org.junit.runner.manipulation.Filter;
import org.junit.runner.Description;

class MethodFilter extends Filter {
	
	private Class<?> testClass;
	private String methodName;
	
	public MethodFilter(Class<?> testClass, String methodName) {
		this.testClass = testClass;
		this.methodName = methodName;
	}
	
	@Override
	public String describe() {
		return testClass.getName() + "." + methodName;
	}

	@Override
	public boolean shouldRun(Description description) {
		return description.equals(
				Description.createTestDescription(testClass, methodName));
	}
}
