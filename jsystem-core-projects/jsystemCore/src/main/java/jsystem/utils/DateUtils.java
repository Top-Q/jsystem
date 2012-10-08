/*
 * Created on Dec 5, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author guy.arieli
 * 
 */
public class DateUtils {
	
	public static final String[] DATE_FORMATS = new String[] {
		"dd/MM/yy, HH:mm:ss",
		"EEE MMM d hh:mm:ss z yyyy"
	};
	
	public static String getDate() {
		return Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
	}

	public static String getDate(long date) {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(date);
		return c.getTime().toString();
	}

	public static String getDate(long date, DateFormat format) {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(date);
		Date d = c.getTime();
		return format.format(d);

	}
	
	/**
	 * Go over all date formats and try to format the given date string
	 * @param dateString	
	 * @return
	 * @throws ParseException	if no format matches
	 */
	public static Date parseDate(String dateString) throws ParseException{
		ParseException exception = null;
		for (String format : DATE_FORMATS){
			try{
				return new SimpleDateFormat(format).parse(dateString);
			}catch (ParseException e) {
				exception = e;
			}
		}
		throw exception;
	}

}
