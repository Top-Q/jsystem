/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.reporter;

import junit.framework.SystemTestCase4;

import org.junit.Test;

public class ScenarioReportPropertiesTest extends SystemTestCase4 {
	
	private int level = 0;
	private String key = "key";
	private String value = "val";
	
	/**
	 * Demonstrates how to work with container properties
	 */
	@Test
	public void containerProperties() {
		report.setContainerProperties(getLevel(),getKey(),getValue());
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}

