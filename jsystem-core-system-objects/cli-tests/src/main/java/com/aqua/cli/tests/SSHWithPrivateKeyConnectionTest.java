/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.cli.tests;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.analyzer.AnalyzerImpl;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aqua.cli.objects.LinuxStation;
import com.aqua.sysobj.conn.CliCommand;

public class SSHWithPrivateKeyConnectionTest extends SystemTestCase4 {

	private LinuxStation linuxStation;

	@Before
	public void setUp() throws Exception {
		linuxStation = (LinuxStation) system
				.getSystemObject("amazonLinuxStation");
		linuxStation.init();
	}

	@After
	public void tearDown() throws Exception {
		linuxStation.close();
	}

	@Test
	public void sShWithKeyConnetiontest() throws Exception {
		CliCommand command = new CliCommand("pwd");
		linuxStation.handleCliCommand("check if in the right dir", command);
		linuxStation.analyze(new FindText("/home/ec2-user"));

	}

}
