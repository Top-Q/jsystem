/*
 * Created on Dec 10, 2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import junit.framework.SystemTestCase;

public class TestLoadError extends SystemTestCase {

	String className;

	public void testError() throws Exception {
		throw new Exception("Fail to load class: " + className);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
