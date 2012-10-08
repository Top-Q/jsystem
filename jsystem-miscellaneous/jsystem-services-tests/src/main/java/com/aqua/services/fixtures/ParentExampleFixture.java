/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import jsystem.framework.fixture.Fixture;

public class ParentExampleFixture extends Fixture {
	
	public ParentExampleFixture() {
	}

	public void setUp() throws Exception {
		report.step(" in parent example fixture setup");
	}

	public void tearDown() throws Exception {
		report.step(" in parent example fixture tearDown");
	}

	public void failTearDown() throws Exception {
	}

}
