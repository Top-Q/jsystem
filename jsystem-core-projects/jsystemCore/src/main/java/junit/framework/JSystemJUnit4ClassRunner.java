/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.RunningProperties;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * 
 * This is a special runner for JUnit 4 test written for JSystem. It delegates test running
 * events to the ListenersManager by using several quite ugly hacks to translate the events
 * sent by JUnit 4's RunListener and convert them into JUnit 3's TestListener events which
 * ListenersManager expects.
 *  
 * @author Gooli
 *
 */
public class JSystemJUnit4ClassRunner extends JUnit4ClassRunner {
	
	private Logger log = Logger.getLogger(JSystemJUnit4ClassRunner.class.getName());
	
	/**
	 * 
	 * This Annotation-derived hack of a class allows to add information to a Description
	 * object.
	 * 
	 * The events passed while the test is running (testStarter, testFinished, etc.) carry only
	 * a Description object. Since the Description constructor is private, the only way way
	 * to add information to it (that I can see) is to add an Annotation object to it.
	 *  
	 * @author Gooli
	 *
	 */
	private class TestInfoAnnotation implements Annotation {
		private final String className;
		private final String methodName;
		
		public TestInfoAnnotation(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
		}
		
		public Class<? extends Annotation> annotationType() {
			return TestInfoAnnotation.class;
		}

		public String getClassName() {
			return className;
		}

		public String getMethodName() {
			return methodName;
		}
	}
	
	/**
	 * 
	 * JUnit 3's TestListener expects a Test object to be passed with every event. This class
	 * is a dummy Test object that holds information about the running test - the class name,
	 * the method name and the uuid.
	 * 
	 * @author Gooli
	 *
	 */
	public class TestInfo implements NamedTest {
		private String className;
		private String methodName;
		
		public TestInfo(Description description) {
			// Get our special annotation object with the info we need
			TestInfoAnnotation testInfo = (TestInfoAnnotation) description.getAnnotation(TestInfoAnnotation.class);
			if (testInfo != null) {
				className = description.getClassName();
				methodName = description.getMethodName();
				test.setName(methodName);
			}
		}

		@Override
		public String getClassName() {
			return className;
		}

		@Override
		public String getMethodName() {
			return methodName;
		}

		@Override
		public String getFullUUID() {
			String fullUUID;
			// check if value was already initiated
			// get parent scenario full unique id first
			String parentFullUUID = System.getProperty(RunningProperties.UUID_PARENT_TAG);
			if (parentFullUUID == null){
				return null;
			}
			while (parentFullUUID.startsWith(".")){
				parentFullUUID = parentFullUUID.substring(1);
			}
			// get test unique id
			String uuid = System.getProperty(RunningProperties.UUID_TAG);
			fullUUID = parentFullUUID.equals("") ? uuid : parentFullUUID + "." + uuid;
			return fullUUID;
		}
		
		@Override
		public int countTestCases() {
			throw new RuntimeException("TestInfo.countTestCases should never be called.");
		}

		@Override
		public void run(TestResult arg0) {
			throw new RuntimeException("TestInfo.run should never be called.");
		}
		
		public SystemTest getSystemTest() {
			return test;
		}
	}
	
	/**
	 * 
	 * This class converts events from JUnit 4's RunListener to JUnit 3's TestListener.
	 * 
	 * @author Gooli
	 *
	 */
	private class TestListenerAdapter extends RunListener {
		private TestListener testListener;
		
		TestListenerAdapter(TestListener testListener) {
			this.testListener = testListener; 
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			super.testFailure(failure);
			if (failure.getException() instanceof AssertionFailedError){
				testListener.addFailure(new TestInfo(failure.getDescription()),(AssertionFailedError)failure.getException());
			}else{
				testListener.addError(new TestInfo(failure.getDescription()), failure.getException());
			}
			
		}

		@Override
		public void testFinished(Description description) throws Exception {
			description = fixDescriptionIfExecutionError(description);
			super.testFinished(description);
			testListener.endTest(new TestInfo(description));
			jsystemEndTest();
		}

		@Override
		public void testStarted(Description description) throws Exception {
			description = fixDescriptionIfExecutionError(description);
			super.testStarted(description);
			TestInfo info = new TestInfo(description);
			methodName = info.getMethodName();
			testListener.startTest(info);
		}
		
		/**
		 * This handles the case in which a test was not found and it was
		 * replaced with test from ExecutionErrorTests class. Since we don't
		 * want that the test that appears in the report would the inner one
		 * (ExecutionErrorTests.testNotFoune), we are replacing it with the
		 * original one. this affects only execution with one JVM
		 * 
		 * @param description
		 *            Test description
		 * @return unchanged description or a new one.
		 * @author Itai Agmon
		 */
		private Description fixDescriptionIfExecutionError(Description description) {
			Class<?> testClassForDescripton = testClass;
			//ITAI: If we failed to initialize the test class than it could be null
			//In this stage and would cause the createTestDescription to fail on
			//null pointer exception. To avoid it we replace it with a different 
			//test class.
			if (null == testClassForDescripton){
				testClassForDescripton = ExecutionErrorTests.class;
			}
			description = Description.createTestDescription(testClassForDescripton, methodName, description.getAnnotations()
					.toArray(new Annotation[] {}));
			return description;
		}
	}
	
	private Class<?> testClass;
	private SystemTest test;
	private String methodName;
	private RunNotifier notifier;
	
	private void jsystemEndTest(){
		TestResult result = ((SystemTestCaseImpl) test).testResult;
		if (result.wasSuccessful() && ListenerstManager.getInstance().getLastTestFailed()){ // add report error to errors
			addFailure(notifier, new AssertionFailedError("Fail report was submitted"));
		}
		test.jsystemTestPostExecution(test);
	}
	

	/**
	 * added in order to get the test instance
	 */
	protected Object createTest() throws Exception {
		test = (SystemTest) getTestClass().getConstructor().newInstance();
		TestResult result = null;
		if (notifier instanceof JsystemRunNotifier){ // run from JRunner
			result = ((JsystemRunNotifier)notifier).getTestResult();
			((SystemTestCaseImpl) test).setTestResult(result);
		}else{ // run from eclipse
			result = new TestResult();
			notifier.addListener(JsystemRunNotifier.getAddapterListener(test, result));
		}
		((SystemTestCaseImpl) test).setTestResult(result);
		return test;
	}
	
	/**
	 * Set up listener adapter and run the test.
	 */
	@Override
	public void run(RunNotifier notifier) {
		if (notifier instanceof JsystemRunNotifier) {
			testClass = ((JsystemRunNotifier) notifier).getTestClass();
			methodName = ((JsystemRunNotifier) notifier).getMethodName();
		} else {
			//ITAI : This was the original line before I added the first block.
			//I am not sure if this is still needs to be here
			testClass = getTestClass().getJavaClass();
		}
		notifier.addListener(new TestListenerAdapter(ListenerstManager.getInstance()));
		this.notifier = notifier;
		super.run(notifier);
	}
	
	private void addFailure(RunNotifier notifier, Throwable t){
		notifier.fireTestFailure(new Failure(Description.createTestDescription(testClass, methodName),t));
	}

	/**
	 * This terrible hack uses a custom Annotation object to add information to the otherwise
	 * sealed Description object.
	 */
	@Override
	protected Description methodDescription(Method method) {
		Annotation [] annotations = testAnnotations(method);
		annotations = Arrays.copyOf(annotations, annotations.length + 1);
		annotations[annotations.length - 1] = new TestInfoAnnotation(method.getDeclaringClass().getName(), method.getName());
		return Description.createTestDescription(getTestClass().getJavaClass(), testName(method), annotations);
	}
	
	public JSystemJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
}
