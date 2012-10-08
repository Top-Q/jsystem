/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import junit.framework.NamedTest;
import junit.framework.Test;

public class TestUtils {

	public static String getTestName(Test test) {
		String testName;
		String testClass = test.getClass().getName();
		//String packageName = StringUtils.getPackageName(testClass);
		if (test instanceof NamedTest) {
			testName = ((NamedTest)test).getClassName() + "." + ((NamedTest)test).getMethodName();
		} else {
			testName = StringUtils.getClassName(testClass);
		}
		return testName;
	}
}
