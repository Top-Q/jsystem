/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import jsystem.framework.scenario.RunnerTest;
import junit.framework.SystemTest;

/**
 * 
 * @author goland
 */
public class TestInfo {
	
	public RunnerTest getTestInfo(SystemTest test) throws Exception {
		return new RunnerTest(test.getClassName(),test.getMethodName());
	}
}
