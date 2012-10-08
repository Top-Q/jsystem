/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filter use to filtering ends of file names.
 */
public class ExtentionFilter implements FilenameFilter {

	String endWith = null;

	/**
	 * Create instance of ExtentionFilter
	 * 
	 * @param endWith
	 *            file names end
	 */
	public ExtentionFilter(String endWith) {
		this.endWith = endWith;
	}

	/**
	 * Filter files.
	 * 
	 * @param name
	 *            file name
	 * @return true if name ends same as variable set in constructor
	 */
	public boolean accept(File dir, String name) {
		if (endWith == null) {
			return true;
		}
		return (name.toLowerCase().endsWith(endWith.toLowerCase()));
	}
}
