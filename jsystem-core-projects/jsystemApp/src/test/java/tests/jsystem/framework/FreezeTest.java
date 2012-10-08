/*
 * Created on 19/04/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework;

import jsystem.framework.TestFreezeException;
import junit.framework.SystemTestCase;

public class FreezeTest extends SystemTestCase {
	public void testFreeze() throws Exception{
		throw new TestFreezeException("xxx");
	}
}
