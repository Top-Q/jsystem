/*
 * Created on Dec 5, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.SystemTestCase;

/**
 * @author guy.arieli
 *
 */
public class TimeZoneTest extends SystemTestCase {
	public void testCalender(){
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		System.out.println(c.getTime().toString());
		
	}
}
