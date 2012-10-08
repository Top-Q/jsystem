/*
 * Created on Oct 21, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase;


/**
 * @author guy.arieli
 *
 */
public class WarningTest extends SystemTestCase {
	public void testReportLevels(){
		report.report("This is a warning","This is the warning message", Reporter.WARNING, false);
	}
	public void testReportFail(){
		report.report("This is a fail","This is the warning message",  false);
	}
}
