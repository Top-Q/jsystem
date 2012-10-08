/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent;

import javax.management.ObjectName;

/**
 * Static file which holds runner agent MBean names.
 * @author goland
 */
public class MBeanNames {

	public static ObjectName FTP_SERVER;
	
	public static ObjectName WEB_SERVER;
	
	public static ObjectName AGENT_MAIN;
	
	public static ObjectName RUNNER_AGENT;

	static {
		try {
			AGENT_MAIN = new ObjectName("jsystem.runner.agent:type=RunnerAgentMain");
			RUNNER_AGENT = new ObjectName("jsystem.runner.agent:type=RunnerAgent");
			FTP_SERVER = new ObjectName("jsystem.runner.agent:type=FtpServer");
			WEB_SERVER = new ObjectName("jsystem.runner.agent:type=WebServer");
		}catch (Exception e){
			throw new RuntimeException("Failed initializing mbean names " + e.getMessage(),e);
		}
	}
}
