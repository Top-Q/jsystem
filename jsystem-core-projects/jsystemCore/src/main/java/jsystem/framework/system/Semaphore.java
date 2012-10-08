/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

public class Semaphore {

	private int count;

	public Semaphore(int n) {
		this.count = n;
	}

	public synchronized void acquire() {
		while (count == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// keep trying
			}
		}
		count--;
	}

	public synchronized void release() {
		count++;
		notify(); // alert a thread that's blocking on this semaphore
	}
}
