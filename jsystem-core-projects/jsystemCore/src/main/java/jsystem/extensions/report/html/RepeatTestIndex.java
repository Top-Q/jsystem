/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.Serializable;
import java.util.HashMap;

public class RepeatTestIndex  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8802856660378107140L;
	HashMap<String, Integer> tests = new HashMap<String, Integer>();

	/**
	 * get and test index and increase it.
	 * 
	 * @param fullName
	 * @return the test index
	 */
	public int getTestIndex(String fullName) {
		Integer index = tests.get(fullName);
		if (index == null) {
			tests.put(fullName, Integer.valueOf(1));
			return 1;
		}
		int tindex = index.intValue() + 1;
		tests.put(fullName, Integer.valueOf(tindex));
		return tindex;
	}
}
