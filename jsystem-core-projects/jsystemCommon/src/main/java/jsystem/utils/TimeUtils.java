/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date and Calendar Utilities
 * 
 * @author Nizan Freedman
 *
 */
public class TimeUtils {

	/**
	 * convert a given long to date and return the String representation
	 * 
	 * @param toConvert	the long value to convert
	 * @return
	 */
	public static String convertLongToDateString(long toConvert){
		Date d = new Date(toConvert);
		return d.toString();
	}
	
	/**
	 * convert a given date to long representation
	 * 
	 * @param year	
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return	the long representation of the given time
	 */
	public static long convertDateToLong(int year, int month, int day, int hour, int minute, int second){
		Date d = getDate(year, month, day, hour, minute, second);
		return d.getTime();
	}
	
	/**
	 * get Date object with given values
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date getDate(int year, int month, int day, int hour, int minute, int second){
		Calendar c = new GregorianCalendar();
		c.set(year, month-1, day);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.AM_PM, Calendar.AM);
		return c.getTime();
	}
}
