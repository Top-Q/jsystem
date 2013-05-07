/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import java.util.logging.Logger;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * 
 * JUnit4TestAdapterForJSystem wraps a JUnit 4 (annotated) test class so it can be run
 * by the Ant task JUnitTestRunner that expects JUnit 3 style classes.
 * 
 * This class is an enhanced version of junit.framework.JUnit4TestAdapter that comes 
 * packaged with JUnit 4. It adds the ability to run a single method from a JUnit 4 class
 * that the original implementation of JUnit4TestAdapter lacks.
 * 
 * @author Gooli
 *
 */
public class JUnit4TestAdapterForJSystem implements Test {

	private Class<?> testClass;
	private String methodName;
	
	private static Logger log = Logger.getLogger(JUnit4TestAdapterForJSystem.class.getName());
	
	public JUnit4TestAdapterForJSystem(Class<?> testClass, String methodName) {
		this.testClass = testClass;
		this.methodName = methodName;
	}

	public Class<?> getTestClass() {
		return testClass;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getFullUUID()  {
		if (SystemTest.class.isAssignableFrom(testClass)) {
			try {
				return ((SystemTest)testClass.newInstance()).getFullUUID();
			} catch (Exception e) {
				return "";
			}
		}
		else
		{
			return "";
		}
	}

	@Override
	public int countTestCases() {
		return 1;
	}

	@Override
	public void run(TestResult result) {
		Request request = Request.classWithoutSuiteMethod(testClass);
		request = request.filterWith(new MethodFilter(testClass, methodName));
		Runner runner = request.getRunner();
		if(runner instanceof ErrorReportingRunner ){
			if (isInitializationError((ErrorReportingRunner)runner)){
				request = Request.classWithoutSuiteMethod(junit.framework.ExecutionErrorTests.class);
				request = request.filterWith(new MethodFilter(junit.framework.ExecutionErrorTests.class, "testNotFound"));
				runner = request.getRunner();
			}
		}
		runner.run(getNotifier(result));
	}
	
	
	private boolean isInitializationError(ErrorReportingRunner runner) {
		
		Description description = runner.getDescription();
		for(Description desc :description.getChildren()){
			if (desc.getDisplayName().startsWith("initializationError")){
				return true;
			}
		}
		
		return false;
	}

	private RunNotifier getNotifier(final TestResult result) {
		JsystemRunNotifier notifier = new JsystemRunNotifier();
		notifier.addAdapterListener(this, result);
		notifier.setTestClass(testClass);
		notifier.setMethodName(methodName);
		return notifier;
	}
}
