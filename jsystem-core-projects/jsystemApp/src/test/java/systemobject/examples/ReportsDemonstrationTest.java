/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import jsystem.framework.report.ReporterHelper;
import junit.framework.SystemTestCase;

public class ReportsDemonstrationTest extends SystemTestCase {

	public void testWorkWithProperties() throws Exception {
		ReporterHelper.addLinkProperty(report, "http://www.one.co.il", "externalLink", "myLink");
		ReporterHelper.addLinkProperty(report, "http://www.one.com", "externalLink1",null);
	}

}

