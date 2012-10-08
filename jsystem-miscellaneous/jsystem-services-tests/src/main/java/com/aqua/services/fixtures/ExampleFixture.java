/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import jsystem.framework.fixture.Fixture;

public class ExampleFixture extends Fixture {
	
	public ExampleFixture() {
		setParentFixture(ParentExampleFixture.class);
	}

	public void setUp() throws Exception {
		report.step(" in example fixture setup");
	}

	public void tearDown() throws Exception {
		report.step(" in example fixture tearDown");
	}

	public void failTearDown() throws Exception {
	}

}
