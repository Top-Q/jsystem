/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.scenario;

import junit.framework.SystemTestCase;

public class PropertiesTest extends SystemTestCase {
	private int keyGeneraor  = 15;
	
	public int getKeyGeneraor() {
		return keyGeneraor;
	}

	public void setKeyGeneraor(int keyGeneraor) {
		this.keyGeneraor = keyGeneraor;
	}

	public void testProperties() throws Exception{
		report.addProperty("key1", "value1");
		report.addProperty("key2", "value2");
	}
	
	public void testProperties2() throws Exception{
		report.addProperty("key1", "second value");
		report.addProperty("key2", "another value");
	}
	
	public void testPropertiesKeyGenerator() throws Exception{
		for (int i=0 ; i<keyGeneraor ; i++){
			report.addProperty("key_num"+i, "val"+i);
		}
	}
	
	public void testPropertiesLongNameKey() throws Exception{
		report.addProperty("a long name key to test the boundries of all the involved objects related to properties", "value");
	}
	
	public void testPropertiesSpecialCharacters() throws Exception{
		report.addProperty("test#","val1");
		report.addProperty("test%","val1");
		report.addProperty("test&","val1");
		report.addProperty("test","val#");
		report.addProperty("test","val%");
		report.addProperty("test","val&");
	}
}
