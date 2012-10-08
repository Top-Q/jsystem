/*
 * Created on 24/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase;

public class FailToPassTest extends SystemTestCase {
	public void testSetFailToPass(){
		report.setFailToPass(true);
		report.report("this report originaly failed", " ", false);
	}
	public void testSetFailToWarning() throws Exception{
		report.setFailToWarning(true);
		report.report("this report originaly failed", " ", false);
		report.report("this report originaly failed", " ", false);
		report.report("this report originaly failed", " ", false);
		throw new Exception("test fail");
		//report.report(report.getCurrentTestFolder());
	}
	public void testWarning(){
		report.report("this report originaly warn", " ",Reporter.WARNING,false);
		report.report("this report originaly warn", " ",Reporter.WARNING,false);
		
		report.report(report.getCurrentTestFolder());
	}
}
