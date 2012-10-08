/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;

import junit.framework.SystemTestCase;

/**
 * @author goland
 */
public class FileLockTest extends SystemTestCase {

	/**
	 * Simple FileLock functionality test
	 */
	public void testFileLock() throws Exception {
		FileLock fl = FileLock.getFileLock(new File("myFile.txt"));
		boolean grab = fl.grabLock();
		assertTrue(grab);
		assertTrue(!fl.grabLock());
		fl.releaseLock();
		assertTrue(fl.grabLock());
	}
}
