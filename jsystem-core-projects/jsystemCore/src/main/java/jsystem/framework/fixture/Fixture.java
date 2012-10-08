/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.fixture;

import jsystem.utils.PackageUtils;
import junit.framework.SystemTestCase;

/**
 * What is a fixture? A fixture represents a state of the Software/System you
 * are testing. Lets say you are testing a web application. One fixture (and
 * obviously the basic) can be your machine without anything install on it, you
 * can define it as your root fixture. A second fixture can be the machine
 * install with the application you are testing. Additional fixture can be your
 * application configured with some data. You can use <code>setParentFixture</code>
 * to create a dependancy between 2 fixtures and to say that one fixture depends
 * on the other.
 * <p>
 * 
 * To define a fixture 2 things should be defined: <code>setUp</code> - the way to get to the
 * fixture and <code>tearDown</code> - the way from the fixture to it parent state.
 * You can also define <code>failTearDown</code> that will be used in navigation caused by
 * test failure.
 * <p>
 * 
 * In our model the fixture are arranged in tree. Every fixture can set it
 * parent fixture.
 * The <code>setParentFixture</code> should be called in the constractor on the
 * fixture. 
 * <p>
 * 
 * Why do you need fixtures? Every test, especially functional/system test, have
 * a fixture. In some of the cases the fixture is very complicated. A not so
 * good solution to the problem will be to enter the fixture setting to the test
 * itself. Fixture management gives better alternative.
 * <p>
 * 
 * @author Guy Arieli
 */
public abstract class Fixture extends SystemTestCase {
	public final static int SETUP_DIRECTION = 0;

	public final static int TEARDOWN_DIRECTION = 1;

	public final static int TEARDOWN_FAIL_DIRECTION = 2;

	private Class<?> parentFixture = RootFixture.class;

	private String name = null;

	/**
	 * failTearDown will be invoke if a test that failed defined a fixture to
	 * fail to. Then all the down navigation will be done using the failTearDown
	 * path. this method is optional and if not defined the tearDown will be
	 * called.
	 * 
	 * @exception Exception
	 */
	public void failTearDown() throws Exception {
		tearDown();
	}

	public void run(int direction) throws Throwable {
		switch (direction) {
		case SETUP_DIRECTION:
			report.step("Fixture: " + toString() + " setUp");
			setUp();
			break;
		case TEARDOWN_DIRECTION:
			report.step("Fixture: " + toString() + " tearDown");
			tearDown();
			break;
		case TEARDOWN_FAIL_DIRECTION:
			report.step("Fixture: " + toString() + " failTearDown");
			failTearDown();
			break;
		default:
			// todo: add default
		}
	}

	/**
	 * Get the parent fixture class.
	 * 
	 * @return Parent fixture class.
	 */
	public Class<?> getParentFixture() {
		return parentFixture;
	}

	/**
	 * Set the parent fixture class.
	 * 
	 * @param parentFixture
	 *            Parent fixture class.
	 */
	public void setParentFixture(Class<?> parentFixture) {
		this.parentFixture = parentFixture;
	}

	/**
	 * Get the fixture name (the default is the class name)..
	 * 
	 * @return Fixture name.
	 */
	public String getName() {
		if (name == null) {
			name = getClass().getName();
		}
		return name;
	}

	/**
	 * Set fixture name.
	 * 
	 * @param name
	 *            Fixture name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The fixture name without the package (used for presentation).
	 * 
	 * @return Fixture name.
	 */
	public String toString() {
		return PackageUtils.getOnlyClassName(getName());
	}
}
