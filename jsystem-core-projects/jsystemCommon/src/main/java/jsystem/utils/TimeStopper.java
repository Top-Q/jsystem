/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.Vector;

/**
 * a Stopper for measuring time.<br>
 * <br>
 * Usage: <br>
 * 1) init() - starts the timer<br>
 * 2) getTimeDiff\getTimeDiffInSec.... - get the time since last init.<br>
 * 3) (optional) lap - will save current time difference to <I>allTimes</I> vector
 * 
 * @author Nizan Freedman
 *
 */
public class TimeStopper {

	/**
	 * the time of the init
	 */
	long startTime;
	
	Vector<Long> allTimes;
	
	
	/**
	 * reset timer - set start time to current time
	 */
	public void init(){
		resetStartTime();
		allTimes = new Vector<Long>();
	}
	
	private void resetStartTime(){
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * get the difference from the last init
	 * @return
	 * 		time difference in milliseconds
	 */
	public long getTimeDiff(){
		return (System.currentTimeMillis() - startTime);
	}
	
	/**
	 * get the difference from the last init
	 * @return
	 * 		time difference in seconds
	 */
	public float getTimeDiffInSec(){
		return (float)getTimeDiff()/1000;
	}
	
	/**
	 * get the difference from the last init
	 * @return
	 * 		time difference in minutes
	 */
	public float getTimeDiffInMin(){
		return getTimeDiffInSec()/60;
	}
	
	/**
	 * mark a lap (time difference is saved and timer startTime is reset to current)<br>
	 * time difference is saved in <I>allTimes</I> vector
	 * 
	 * @return	the time in miliseconds till now
	 */
	public long lap(){
		long l = getTimeDiff();
		allTimes.add(l);
		resetStartTime();
		return l;
	}

	/**
	 * get all times measured with laps
	 * 
	 * @return	a vector of all lap times (in miliseconds difference)
	 */
	public Vector<Long> getAllTimes() {
		return allTimes;
	}
	
	/**
	 * get all times measured with laps
	 * 
	 * @return	a vector of all lap times (in seconds difference)
	 */
	public Vector<Float> getAllTimesInSeconds() {
		return getAllTimesConverted(1000);
	}
	
	/**
	 * get all times measured with laps
	 * 
	 * @return	a vector of all lap times (in minutes difference)
	 */
	public Vector<Float> getAllTimesInMinutes() {
		return getAllTimesConverted(1000 * 60);
	}
	
	private Vector<Float> getAllTimesConverted(int divider) {
		Vector<Float> seconds = new Vector<Float>();
		for (long time : allTimes){
			seconds.add((float)time/divider);
		}
		return seconds;
	}
	
	
}
