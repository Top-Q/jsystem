package com.aqua.general;

import com.aqua.general.JSysTestCaseOld;

/**
 * Empty test which triggers fixture manager to navigate to
 * root fixture.
 * This test doesn't test anything.
 * @author goland
 */
public class EmptyTest extends JSysTestCaseOld {

	public EmptyTest() {
		super();
	}
	/**
	 * This test is currentlly not mapped in the sanity doc
	 * @throws Exception
	 */
	public void testToSetRootFixture() throws Exception{
		report.report("Empty test for navigating to RootFixture");
	}
}
