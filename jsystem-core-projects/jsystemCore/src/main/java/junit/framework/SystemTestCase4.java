/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import java.util.HashMap;
import java.util.Map;

import jsystem.extensions.handlers.UIEventHandler;
import jsystem.extensions.handlers.ValidationHandler;
import jsystem.framework.ShutdownManager;
import jsystem.framework.analyzer.AnalyzerImpl;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ValidationError;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JSystemJUnit4ClassRunner.class)
public class SystemTestCase4 extends SystemTestCaseImpl implements SystemTest, UIEventHandler, ValidationHandler {
	public static AnalyzerImpl analyzer = null;
	private String name;
	static {
		ShutdownManager.init();
		analyzer = new AnalyzerImpl();
		analyzer.setTestAgainstObject("");
	}

	@Before
	public void defaultBefore() throws Throwable {
		jsystemTestPreExecution(this);
	}

	@After
	public void defaultAfter() throws Throwable {

	}

	public void run(TestResult result) {
		// not implemented for JUnit 4 test cases
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int countTestCases() {
		return 1;
	}

	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public String getMethodName() {
		return getName();
	}

	@Override
	public ValidationError[] validate(HashMap<String, Parameter> map, String methodName) throws Exception {
		return null;
	}

	@Override
	public void handleUIEvent(HashMap<String, Parameter> map, String methodName) throws Exception {

	}
}