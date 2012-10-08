/*
 * Created on Sep 17, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.monitor;

import java.util.HashMap;
import java.util.Iterator;

import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * @author guy.arieli
 * 
 */
public class MonitorsManager implements TestListener {
	private static MonitorsManager monitor;

	public static MonitorsManager getInstance() {
		if (monitor == null) {
			if (JSystemProperties.getInstance().isReporterVm() && JSystemProperties.getInstance().isJsystemRunner()) {
				return null;
			}
			monitor = new MonitorsManager();
		}
		return monitor;
	}

	private Reporter report = ListenerstManager.getInstance();

	HashMap<String, Thread> runningMonitors = new HashMap<String, Thread>();

	private MonitorsManager() {
		ListenerstManager.getInstance().addListener(this);
	}

	/**
	 * Start running the monitor in seperated thread
	 * 
	 * @param monitor
	 */
	public synchronized void startMonitor(Monitor monitor) {
		report.report("start monitor: " + monitor.getName());
		Thread thread = new Thread(monitor);
		thread.setName(monitor.getName());
		thread.start();
		runningMonitors.put(monitor.getName(), thread);
	}

	@SuppressWarnings("deprecation")
	public synchronized void stopMontior(Monitor monitor) {
		report.report("stop monitor: " + monitor.getName());
		Thread t = (Thread) runningMonitors.remove(monitor.getName());
		if (t == null) {
			return;
		}
		t.interrupt();
		long startTime = System.currentTimeMillis();
		while (t.isAlive() && (System.currentTimeMillis() - startTime) < 3000) {
			Thread.yield();
		}
		if (t.isAlive()) {
			t.stop();
		}

	}

	@SuppressWarnings("deprecation")
	public synchronized void closeAllMonitors() {
		Iterator<Thread> iter = runningMonitors.values().iterator();
		while (iter.hasNext()) {
			Thread t = (Thread) iter.next();
			t.interrupt();
		}
		long startTime = System.currentTimeMillis();
		boolean oneRunning = false;
		while ((System.currentTimeMillis() - startTime) < 5000) {
			iter = runningMonitors.values().iterator();
			oneRunning = false;
			while (iter.hasNext()) {
				Thread t = (Thread) iter.next();
				if (t.isAlive()) {
					break;
				}
			}
			Thread.yield();
			if (!oneRunning) {
				break;
			}
		}
		iter = runningMonitors.values().iterator();
		while (iter.hasNext()) {
			Thread t = (Thread) iter.next();
			if (t.isAlive()) {
				/*
				 * If the tread is alive stop it. mask any exception that can be
				 * caused by the stop process
				 */
				try {
					t.stop();
				} catch (Throwable tt) {
				}
			}
		}
		runningMonitors = new HashMap<String, Thread>();
	}

	/**
	 * Enable the test to wait for a monitor notification The monitor should
	 * call method notifyTest.
	 * 
	 * @param monitor
	 *            the monitor to wait for.
	 * @param timeout
	 *            wait timeout
	 * @throws Exception
	 */
	public void waitForMonitor(Monitor monitor, long timeout) throws Exception {
		report.report("wait for monitor: " + monitor.getName());
		Thread t = (Thread) runningMonitors.remove(monitor.getName());
		if (t == null) {
			return;
		}
		synchronized (monitor) {
			t.wait(timeout);
		}
		if (monitor.isFail) {
			throw new Exception("Monitor: " + monitor.getName() + " failed");
		}
	}

	public synchronized void waitForAllMonitorsToEnd(long timeout) throws Exception {
		Iterator<Thread> iter = runningMonitors.values().iterator();
		long startTime = System.currentTimeMillis();
		while (iter.hasNext()) {
			Thread t = (Thread) iter.next();
			long left = timeout - (System.currentTimeMillis() - startTime);
			if (left <= 0) {
				left = 1;
			}
			if (timeout == 0) {
				left = 0;
			}
			t.join(left);
		}
		runningMonitors = new HashMap<String, Thread>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 *      java.lang.Throwable)
	 */
	public void addError(Test arg0, Throwable arg1) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 *      junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test arg0, AssertionFailedError arg1) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test arg0) {
		closeAllMonitors();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test arg0) {

	}

}