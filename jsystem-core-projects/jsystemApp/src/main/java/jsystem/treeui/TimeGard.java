/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class TimeGard implements TestListener {
	GardThread gard = null;

	public void addError(Test test, Throwable t) {
		// TODO Auto-generated method stub

	}

	public void addFailure(Test test, AssertionFailedError t) {
		// TODO Auto-generated method stub

	}

	public synchronized void endTest(Test test) {
		if (gard != null) {
			gard.setStop(true);
			gard = null;
		}

	}

	public synchronized void startTest(Test test) {
		if (gard != null) {
			endTest(test);
		}
	}

}

class GardThread extends Thread {
	boolean stop = false;

	long maxTime = -1;

	public GardThread(long maxTime) {
		this.maxTime = maxTime;
	}

	public void run() {
		try {
			Thread.sleep(maxTime);
		} catch (Exception e) {

		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
		interrupt();
	}
}