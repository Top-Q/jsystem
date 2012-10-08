/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

/**
 * When ever executing a {@link Runnable} in a thread,
 * there is a need to catch an exception if thrown and handle it internally as needed.
 * This class helps in this task
 * @author goland
 */
public abstract class BackgroundRunnable implements Runnable {

	private Throwable throwable;
	
	/**
	 * Invoked by the executing thread.
	 * Invokes the {@link #internalRun()} method,
	 * catches exception if thrown and holds it.
	 */
	public void run() {
		try {
			internalRun();
		}catch (Throwable t){
			setThrowable(t);
		}
	}
	
	/**
	 * Should be implemented by inheriting class.
	 */
	public abstract void internalRun() throws Exception;
	
	/**
	 */
	public synchronized void setThrowable(Throwable t) {
		throwable = t;		
	}
	
	/**
	 */
	public synchronized Throwable getThrowable() {
		return throwable;
	}
}
