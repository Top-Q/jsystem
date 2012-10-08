/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.systemProperties;

import jsystem.framework.JSystemProperties;
import junit.framework.SystemTestCase;

/**
 * Demonstrates usage of jsystem.properties API
 */
public class JSystemPropertiesExample extends SystemTestCase {
	
	/**
	 */
	public void testSetJSystemProp() throws Exception {
		JSystemProperties.getInstance().setPreference("com.aqua.filetransfer.ftp.ftpserver", sut().getValue("sysObj/element"));
	}

}

