/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

/**
 * used for different numbers utilities, like randomizing...
 * 
 * @author nizanf
 */
public class NumberUtils {

	/**
	 * get a random double value in a given range
	 * 
	 * @param start	range lower value
	 * @param end	range high value
	 * @return	a random double in the range
	 */
	public static double getRandomDoubleValueInRange(double start, double end){
		double dif = end-start;
		double num = Math.random()*dif;
		return num+start;
	}
	
	/**
	 * get a random integer value in a given range
	 * 
	 * @param start	range lower value
	 * @param end	range high value
	 * @return	a random integer in the range
	 */
	public static int getRandomIntegerValueInRange(double start, double end){
		return (int)Math.round(getRandomDoubleValueInRange(start, end));
	}
	
	/**
	 * get a random long value in a given range
	 * 
	 * @param start	range lower value
	 * @param end	range high value
	 * @return	a random long in the range
	 */
	public static long getRandomLongValueInRange(double start, double end){
		return Math.round(getRandomDoubleValueInRange(start, end));
	}
	
	/**
	 * get a random float value in a given range
	 * 
	 * @param start	range lower value
	 * @param end	range high value
	 * @return	a random flopat in the range
	 */
	public static float getRandomFloatValueInRange(double start, double end){
		return Float.parseFloat(getRandomDoubleValueInRange(start, end)+""); 
	}
	
}
