/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

/**
 * Used for memory measurement
 * 
 * @author Nizan Freedman
 *
 */
public class MemoryUtil {

	static long startMemory;

	public final static int KILO = 1024;

	public static double ConvertByteToMegaByte(long byteUnits){
		return (double)byteUnits/(KILO * KILO);
	}
	
	public static long ConvertMegaByteToByte(double megaByteUnits){
		return (long) (megaByteUnits*(KILO * KILO));
	}

	/**
	 * Get current free memory<br>
	 * The current memory is saved for Memory usage analysis
	 * @return Free memory in MegaBytes
	 */
	public static double sampleFreeMemory(){
		startMemory = Runtime.getRuntime().freeMemory();
		return ConvertByteToMegaByte( startMemory );
	}

	/**
	 * Return the Difference (in MegaBytes) between the first sampled free memory and current
	 * free memory
	 * @return	A positive number if there is less memory and a negative number if there is more
	 */
	public static double getMemoryUsage(){
		long diff = startMemory - Runtime.getRuntime().freeMemory();
		return ConvertByteToMegaByte( diff );
	}

	/**
	 * Wait till the given requested amount of MegaBytes is free in memory
	 * @param memory	in MegaBytes units
	 */
	public static void waitForMemory(double memory){
		long memoryInBytes = ConvertMegaByteToByte(memory);
		
		while (Runtime.getRuntime().freeMemory() < memoryInBytes){
			System.gc();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}