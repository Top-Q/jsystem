/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.compare;

import jsystem.extensions.analyzers.compare.CompareValues;
import junit.framework.SystemTestCase;
import systemobject.tests.Device1;

public class CompareValuesTests extends SystemTestCase {

	Device1 device;

	@Override
	protected void setUp() throws Exception {
		device = (Device1) system.getSystemObject("device1");
	}

	public void testInt() {
		device.setTestAgainstObject(56);

		device.analyze(new CompareValues(56));
	}

	public void testBoolean() {
		device.setTestAgainstObject(true);

		device.analyze(new CompareValues(true));
	}

	public void testString() {
		device.setTestAgainstObject("foo");

		device.analyze(new CompareValues("foo"));
	}
}
