/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

public class ClassSearchUtilTest {

	/**
	 */
	@Test
	public void testGetPropertyFromClassPath() throws Exception {
		assertNotNull(ClassSearchUtil.getPropertyFromClassPath("META-INF/jsystemCore.build.properties", "jversion"));
	}

	/**
	 */
	@Test(expected = IOException.class)
	public void testGetPropertyFromClassPathPropertyThatDoesntExist() throws Exception {
		assertNull(ClassSearchUtil.getPropertyFromClassPath("META-INF/jsystemCore.build.properties", "jversionxxx"));
	}

	/**
	 */
	@Test(expected = IOException.class)
	public void testGetPropertyFromClassPathResourceDoesntExist() throws Exception {
		ClassSearchUtil.getPropertyFromClassPath("META-INF/jsystemXXX.build.properties", "jversionxxx");
	}

}
