package org.jsystemtest.jsystem_services_tests;

import org.junit.Test;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.LinuxDefaultCliConnection;

import junit.framework.SystemTestCase4;

public class Ssh25ExampleTests extends SystemTestCase4 {

	@Test
	public void ssh25Test() throws Exception {
		
		// To run this example, use the following SUT: ssh25_example.xml
		
		LinuxDefaultCliConnection cliConnection = (LinuxDefaultCliConnection) system.getSystemObject("wsl_ubuntu");
//		LinuxDefaultCliConnection cliConnection = (LinuxDefaultCliConnection) system.getSystemObject("docker_container");
		
		CliCommand command = new CliCommand("uptime");
		cliConnection.command(command);
		
		String result = command.getResult();
		report.report(result);
		
		command = new CliCommand("ls -la");
		cliConnection.command(command);
		
		result = command.getResult();
		report.report(result);
	}
}
