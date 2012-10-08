/*
 * Created on Sep 17, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.monitor;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.framework.system.SystemObjectManager;

/**
 * @author guy.arieli
 * 
 */
public abstract class Monitor implements Runnable {
	protected String name;

	protected boolean isFail = false;

	public Monitor(String name) {
		this.name = name;
	}

	/**
	 * Use it to log positive events from your test.
	 */
	public static Reporter report = ListenerstManager.getInstance();

	/**
	 * Use it to get information about the setup/system you are testing.
	 */
	public static Sut sut = SutFactory.getInstance().getSutInstance();

	/**
	 * Use it to access the interfaces of your tested system.
	 */
	public SystemObjectManager system = SystemManagerImpl.getInstance();

	/**
	 * Go to sleep
	 * 
	 * @param time
	 *            sleep time
	 */
	public void sleep(long time) {
		report.report("Sleep " + (time / 1000) + " sec.");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void notifyTest() {
		synchronized (this) {
			notifyAll();
		}
	}

	public boolean isFail() {
		return isFail;
	}

	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}
}
