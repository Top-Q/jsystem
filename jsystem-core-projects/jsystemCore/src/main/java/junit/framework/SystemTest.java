/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

import jsystem.framework.sut.Sut;

public interface SystemTest extends Test, NamedTest {

	/**
	 * Returns the Sut instance to work with
	 * in order to get information about the setup.
	 */
	public abstract Sut sut();

	/**
	 * Get the fixture this test is bind to.
	 * 
	 * @return The bond fixture class.
	 */
	public abstract Class<?> getFixture();

	/**
	 * Set the fixture to be used when executing this test. It's the framework
	 * responsibility to navigate to the requested fixture. The test can and
	 * should assume it's running under this fixture configurations.
	 * 
	 * @param fixture
	 *            The fixture to bind to.
	 */
	public abstract void setFixture(Class<?> fixture);

	public abstract String getFixtureName();

	/**
	 * Set the fixture class name to be used when executing this test. It's the
	 * framework responsibility to navigate to the requested fixture. The test
	 * can and should assume it's running under this fixture configurations.
	 * 
	 * @param fixtureClassName
	 */
	public abstract void setFixtureName(String fixtureClassName);

	/**
	 * Get the fixture to fall to if the test fail.
	 * 
	 * @return The fixture to fall to class.
	 */
	public abstract Class<?> getTearDownFixture();

	/**
	 * Set the fixture to fall to in case of test fail. If exist the
	 * failTearDown method will be executed (if not the tearDown will be used).
	 * 
	 * @param tearDownFixture
	 *            The fail to fixture.
	 */
	public abstract void setTearDownFixture(Class<?> tearDownFixture);

	public abstract void setPass(boolean isPass);

	/**
	 * @return True if test passed, with <b>NO</b> accordance to test flags
	 * such as Negative test\Known issue, etc...
	 */
	public abstract boolean isPass();
	
	/**
	 * @return True if test passed, <b>with accordance to test flags
	 * such as Negative test\Known issue, etc...</b>
	 */
	public abstract boolean isPassAccordingToFlags();

	/**
	 * Go to sleep
	 * 
	 * @param time
	 *            sleep time
	 */
	public abstract void sleep(long time);

	/**
	 * 
	 * @return the test execution steps.
	 */
	public abstract String getExecutedSteps();

	/**
	 * Add an execution step to the test step repository. <b>Warning:</b> This
	 * method should only be used internally by the framework.
	 * 
	 * @param step
	 *            the step to be added.
	 */
	public abstract void addExecutedSteps(String step);

	/**
	 * 
	 * @return the test fail cause as found by the report title.
	 */
	public abstract String getFailCause();

	/**
	 * Add a fail cause to the test. <b>Warning:</b> This method should only be
	 * used internally by the framework.
	 * 
	 * @param failCause
	 *            the step to be added.
	 */
	public abstract void addFailCause(String failCause);

	/**
	 * Clear the fail cause buffer. <b>Warning:</b> This method should only be
	 * used internally by the framework.
	 */
	public abstract void clearFailCause();

	/**
	 * Clear the test documentation <b>Warning:</b> This method should only be
	 * used internally by the framework.
	 */
	public abstract void clearDocumentation();

	/**
	 * Clear the test steps.
	 * 
	 */
	public abstract void clearSteps();

	/**
	 * Get the test sources javadoc.
	 * 
	 * @return test documentation ad found in the javadoc.
	 */
	public abstract String getTestDocumentation();

	/**
	 * Use to set the test documentation. <b>Warning:</b> This method should
	 * only be used internally by the framework.
	 * 
	 * @param testDocumantation
	 *            the test documentation as will be seen in the reports.
	 */
	public abstract void setTestDocumentation(String testDocumantation);

	/**
	 * Gets the name of a TestCase
	 * @return the name of the TestCase
	 */
	public abstract String getName();
	/**
	 * Sets the name of a TestCase
	 * @param name the name to set
	 */
	public abstract void setName(String name);

	public abstract String getFullUUID();
	public abstract void setFullUUID(String fullUUID);
	
	public abstract void jsystemTestPreExecution(SystemTest test) throws Throwable;
	public abstract void jsystemTestPostExecution(Test test);
	public abstract TestResult getTestResult();

	public abstract void initFlags();
	
}