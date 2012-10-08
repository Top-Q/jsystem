/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report.html;

import junit.framework.SystemTestCase;

public class JuniterJavadocBug extends SystemTestCase{
	 
	 public void test1(){
	  /*
	  //DEBUG: print the about to be sent frame in its array of bytes format
	  report.step ("*************************");
	  report.step(TrafficUtil.convertIntArrToHexString(f.getFrame()));
	  report.step("*************************");
	  report.step(f.toString());
	  report.step("*************************"); 
	  */

	  report.report("test 1");
	 }
	 
	 public void test2(){
	 	
	 }
	 public void testColloredTitles(){
		 report.report("!DOCTYPE HTML<font COLOR=\"#aaaaaa\">hello</font>");
	 }
	}

