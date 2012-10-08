package com.aqua.sanity;

import org.junit.Test;

import com.aqua.general.JSysTestCase4UseExistingServer;

public class ExitThrewMenuTest extends JSysTestCase4UseExistingServer {
	public ExitThrewMenuTest() {
		super();
	}
	@Test
	public void testExitThrewMenu() throws Exception{
		applicationClient.exitThroughMenu(true);
		report.report("passed the exit through menu");
	}
}
