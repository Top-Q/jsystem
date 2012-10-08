/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase;

public class ThreadExampleTest extends SystemTestCase {
	
	public void testWithWaitForThread() throws Exception {
		IxiaWork work = new IxiaWork();
		Thread t = new Thread(work);
		t.start();
		
		report.report("After thread work start ...");		
		//
		work.waitForWorktoFinish();
		
		report.report("Thread work is done ... ");
		//
		//
		//
		
	}
	
	public void testWithBusyWaitForThread() throws Exception {
		IxiaWork work = new IxiaWork();
		Thread t = new Thread(work);
		t.start();
		
		report.report("After thread work start ...");

		while (!work.isWorkDone()){
			Thread.sleep(750);
			report.report("monitoring code here ...");
		}
		
		report.report("Thread work is done ... ");
		//
		//
		//

	}
	
	
}

class IxiaWork implements Runnable {
	
	private static Reporter report = ListenerstManager.getInstance();
	private Exception e;
	private boolean workDone;
	
	public void run() {
		try {
			internalRun();
		}catch (Throwable t){
			setThrowable(t);
		}finally{
			setWorkDone();
			synchronized (this) {
				this.notifyAll();
			}
			
		}
	}

	private void internalRun() throws Exception {
		for (int  i = 0 ;i < 5 ;i++){
			Thread.sleep(1000);
			report.report("After loop number " + i);
		}		
	}
	
	public synchronized void waitForWorktoFinish() throws Exception{
		while (!isWorkDone()){
			this.wait();
		}
	}
	
	private synchronized void setThrowable(Throwable t) {
		e = new Exception(t);
	}
	
	public synchronized Exception getException(){
		return e;
	}
	
	private synchronized void setWorkDone() {
		workDone = true;
	}
	
	public synchronized boolean isWorkDone(){
		return workDone;
	}

}
