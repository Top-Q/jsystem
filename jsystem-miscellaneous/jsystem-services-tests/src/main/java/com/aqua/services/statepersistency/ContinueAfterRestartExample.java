/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.statepersistency;

import jsystem.framework.report.ListenerstManager;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class ContinueAfterRestartExample extends SystemTestCase4 {
	
	private long sleep;

	@Test
	public void someTestOrOperation() throws Exception{
		report.step("activating some test or operation");
		sleep(getSleep());
	}

	@Test
	public void restartAgent() throws Exception{
		report.report("----------------------------------------");
		report.report("-									  -");
		report.report("-     Restart Agent Now!!              -");
		report.report("-                                      -");
		report.report("-                                      -");
		report.report("----------------------------------------");
		report.report("----------------------------------------");
		ListenerstManager.getInstance().saveState(this);
		sleep(getSleep());
		
	}

	@Test
	public void anotherTestOrOperation() throws Exception{
		report.step("anotherTestOrOperation");
		sleep(getSleep());

	}

	@Test
	public void shouldContinue() throws Exception{
		report.report("shouldContinue");
		sleep(getSleep());

	}

	@Test
	public void shouldFail() throws Exception{
		report.report("shouldFail");
		report.report("Error log",false);

	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

}
