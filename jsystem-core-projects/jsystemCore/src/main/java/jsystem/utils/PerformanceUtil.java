/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Utility for performance measuring and reporting.<br>
 * Usage:<br>
 * 
 * call to the <I>startMeasure()</I> method returns an Index.<br>
 * call to the <I>endMeasure(index, toPrint)</I> method will locate the index and printout the 
 * given message with the time measured.<br>
 * 
 * <br>
 * currently, only print to logger using info level.<br>
 * in the future, we can use this for different statistics
 * 
 * @author Nizan Freedman
 *
 */
public class PerformanceUtil {

	private static Logger log = Logger.getLogger(PerformanceUtil.class.getName());
	private static int counter = 0;
	
	private static HashMap<Integer, Long> times;
	static{
		times = new HashMap<Integer, Long>();
	}
	
	/**
	 * Start a new time measure
	 * 
	 * @return	the index for the measure (needed for <I>endMeasure</I> method)
	 */
	public synchronized static int startMeasure(){
		long time = System.currentTimeMillis();
		counter++;
		times.put(counter, time);
		return counter;
	}
	
	/**
	 * Printout the given message with time, by the given index<br>
	 * <B>Note: the index is removed after this call so several calls are not supported!</B>
	 * 
	 * @param index	the index given by <I>startMeasure</I>
	 * @param toPrint	The message to print with the time
	 */
	public synchronized static void endMeasure(Integer index,String toPrint){
		long now = System.currentTimeMillis();
		Long time = times.get(index);
		if (time == null){
			return;
		}
		double diff = now-time;
		
		log.info(toPrint + " Took " + (double)diff/1000 + " seconds.");
		times.remove(index);
	}
	
}
