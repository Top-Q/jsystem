/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

/**
 * 
 */
public class FileUtilsTest extends SystemTestCase {

	public void testConvertToUnixPath() {
		assertEquals("./temp",FileUtils.replaceSeparator(".\\temp"));
	}

	public void testConvertToWindowsPath() {
		assertEquals(".\\temp",FileUtils.convertToWindowsPath("./temp"));
	}

}
