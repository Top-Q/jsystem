/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.treeui;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import junit.framework.SystemTestCase;
import tests.sysobj.Obj;

/**
 * This class holds temporary tests that are created during testing and most likely should be removed once
 * testing is over.
 * Still it is very useful to use such class to share test cases between the team members.
 * Anyway, feel free to do whatever you want with this class.  
 * 
 * @author yoram.shamir
 *
 */
public class TempTests extends SystemTestCase {

	/**
	 * This test simply waits 30 seconds allowing the user to verify that the Init reporters buttons are
	 * disabled.
	 *   
	 * @throws Exception
	 */
	public void testEnableDisable() throws Exception{
		sleep(30 * 1000);
	}
	
	public void testThrowException() throws Exception {
		if (isThrowException()) {
			throw new Exception("Testing 1 2 3");
		}
	}
	
	/**
	 * This test simply compares an input number. Run it with JRunner twice. First with input = 4 so it fails
	 * then with input = 2 so pass. Verify that the Failure mark is shown then disappears.
	 *  
	 * @throws Exception
	 */
	public void testAnalyzerFail() throws Exception{
		Obj so = new Obj();
		so.setTestAgainstObject(testAgainst);
		so.analyze(new NumberCompare(compareOption.EQUAL, 2, 0));
	}

	private int testAgainst = 2;

	public int getTestAgainst() {
		return testAgainst;
	}

	public void setTestAgainst(int testAgainst) {
		this.testAgainst = testAgainst;
	}

	private boolean throwException = false;

	public boolean isThrowException() {
		return throwException;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}
	
}
