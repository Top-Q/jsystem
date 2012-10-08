/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import junit.framework.SystemTestCase;

public class InfraExampleTests extends SystemTestCase {
	
	public void testKeepAlive() throws Exception {
		int timeout = 1000000;
		report.report("Testing keep alive <enter>");
		long startTime = java.lang.System.currentTimeMillis();
		report.report(getName() + " waiting for device to timeout: " + timeout/1000 + " seconds");
		while(true){
			if(timeout > 0 && java.lang.System.currentTimeMillis() - startTime > timeout){
				break;//break after 7.5 minutes
			}
			Thread.sleep(10000);
			report.report(" waiting for device to timeout. Time elapsed " + ((java.lang.System.currentTimeMillis() - startTime)/1000)+ " sec");
		}
//		X.basic.showVersion();
	}
}
