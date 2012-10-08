/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

/**
 * Simple class that collects result messages. It's implemented as Singleon
 * patern, so instance of ResultCollector you get from static variable
 * <code> ResultCollector.collector </code>
 */
public class ResultCollector {
	/**
	 * Instance of ResultCollector
	 */
	public static ResultCollector collector = new ResultCollector();

	private StringBuffer sb = new StringBuffer();

	private ResultCollector() {

	}

	/**
	 * Add result message.
	 * 
	 * @param result
	 *            message
	 */
	public void addResult(String result) {
		// System.out.println("add " + result);
		sb.append(result);
	}

	/**
	 * Returns collected results.
	 * 
	 * @return results
	 */
	public String getResult() {
		String s = sb.toString();
		clearResult();
		return s;
	}

	/**
	 * Clears result buffer.
	 */
	public void clearResult() {
		sb = new StringBuffer();
	}
}
