/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import junit.framework.SystemTestCase;

public class ParamLevel1 extends SystemTestCase {

	public int rate = 100;
	public boolean macLearning = true;

	public boolean getMacLearning() {
		return macLearning;
	}

	/**
	 * @section Device
	 * @param macLearning
	 */
	public void setMacLearning(boolean macLearning) {
		this.macLearning = macLearning;
	}
	public int getRate() {
		return rate;
	}
	/**
	 * @section Generator
	 * @param rate
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}


}
