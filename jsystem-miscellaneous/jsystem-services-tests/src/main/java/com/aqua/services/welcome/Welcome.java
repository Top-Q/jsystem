/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.welcome;

import org.junit.Test;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;

public class Welcome extends SystemTestCase4 {

	/**
	 * Logs a welcome message.<br>
	 */
	@TestProperties(name="Welcome to JSystem. To run this test, press on the play button.")
	@Test
	public void welcome(){
		report.step("Welcome to JSystem framework.");
		report.report("How to continue from here:");
		report.report("You can start by reading JSystem documentation. Start with the <a href=\"../../doc/Chapter 3 Getting Started.pdf\">getting started</a> chapter,");
		report.report("continue with the <a href=\"../../doc/Chapter 2 JSystem Automation Framework General Description.pdf\">overview</a> chapter, and then do the <a href=\"../../doc/Chapter 11 JSystem Quick Start Project.pdf\">quick start project</a>"); 
		report.report("For questions go to our site: <a href=\"http://www.jsystemtest.org\">www.jsystemtest.org</a>, post a question to our <a href=\"http://sourceforge.net/forum/forum.php?forum_id=397999\">help forum</a>.");
		report.report("or send us a mail <a rel=\"nofollow\" href=\"mailto:jsystemtest@gmail.com\">jsystemtest@gmail.com</a>");
	}
}

