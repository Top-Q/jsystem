/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.fixtureui;

import jsystem.runner.loader.LoadersManager;
import junit.framework.SystemTestCase;

public class FixtureNavigation extends SystemTestCase {
	private Class<?> fixture = null;

	private Class<?> failToFixture = null;

	private Exception e = null;

	public void testGoToFixture() throws Exception {
		if (e != null) {
			throw e;
		}
	}

	public void testFailToFixture() throws Exception {
		setPass(false);
		if (e != null) {
			throw e;
		}
	}

	public void setGoFixture(String fixture) {
		try {
			ClassLoader cl = LoadersManager.getInstance().getLoader();
			this.fixture = cl.loadClass(fixture);
			setFixture(this.fixture);
		} catch (ClassNotFoundException e) {
			this.e = e;
		}
	}

	public String getGoFixture() {
		if (fixture == null) {
			return null;
		}
		return fixture.getName();
	}

	public void setFailToFixture(String fixture) {
		try {
			ClassLoader cl = LoadersManager.getInstance().getLoader();
			this.failToFixture = cl.loadClass(fixture);
			setTearDownFixture(this.failToFixture);
		} catch (ClassNotFoundException e) {
			this.e = e;
		}
	}

	public String getFailToFixture() {
		if (failToFixture == null) {
			return null;
		}
		return failToFixture.getName();
	}

}
