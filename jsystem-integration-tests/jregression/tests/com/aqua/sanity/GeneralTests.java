package com.aqua.sanity;

import junit.framework.SystemTestCase;

public class GeneralTests extends SystemTestCase {

	private int sleepSeconds;
	
	public void testSleep(){
		sleep(sleepSeconds * 1000);
	}

	public int getSleepSeconds() {
		return sleepSeconds;
	}

	public void setSleepSeconds(int sleepSeconds) {
		this.sleepSeconds = sleepSeconds;
	}
	
}
