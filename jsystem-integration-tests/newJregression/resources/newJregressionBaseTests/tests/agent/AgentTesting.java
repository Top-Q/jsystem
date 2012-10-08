package agent;

import java.util.Random;

import jsystem.framework.TestProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.RunningProperties;
import jsystem.runner.agent.clients.JSystemAgentClient;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class AgentTesting extends SystemTestCase4 {
	
	private int numberOfFiles;
	private int expectedNumberOfFiles;
	private String agentUrl;
	private long sleep = 1;
	
	
	@Test
	public void activateTestedApplication() throws Exception{
		report.report("activating tested application");
	}

	@Test
	public void addUserToApplication() throws Exception{
		report.report("adding user ----------------------------------",getAgentUrl(),true);
	}

	@Test
	@TestProperties(paramsInclude={"agentUrl","sleep"})
	public void automaticAgentRestart() throws Exception{
		ListenerstManager.getInstance().saveState(this);
		JSystemAgentClient client = new JSystemAgentClient(getAgentUrl());
		client.init();
		report.report(" ------------------ Connected to agent");
		client.restartAgent();
		report.report(" ------------------ Sent the agent signal to restart");
		report.report(" ------------------ Entering a sleep of " + getSleep());
		sleep(getSleep());
		report.report(" ------------------ After sleep. Exiting");
		System.exit(0);
	}

	@Test
	@TestProperties(paramsInclude={"sleep"})
	public void manualAgentRestart() throws Exception{
		ListenerstManager.getInstance().saveState(this);
		report.report(" ------------------ Entering a sleep of " + getSleep());
		sleep(getSleep());
		report.report(" ------------------ After sleep. Exiting");		
	}
	
	@Test
	public void makeAWarning() throws Exception{
		report.report("stam warning",Reporter.WARNING);
	}

	@Test
	public void testThatMightFail() throws Exception{
		report.step("Tossing a boolean value");
		boolean exception = new Random(System.currentTimeMillis()).nextBoolean();
		if (exception){
			throw new Exception("example fail");
		}
		report.report("Did not throw an exception");
	}

	@Test
	@TestProperties(returnParam={"numberOfFiles","expectedNumberOfFiles"})
	public void checkResultsFolder() throws Exception{
		report.step("Checking results folder");
		if (getNumberOfFiles() == -1){
			setNumberOfFiles(new Random(System.currentTimeMillis()).nextInt(4));
		}
		report.step("---------------   " + getNumberOfFiles() + "   ----------------------");
	}

	@Test
	@TestProperties(returnParam={"value"})
	public void getValueTest() throws Exception{
		report.report("AgentUrl  is" + getAgentUrl());
		report.report(System.getProperty(RunningProperties.PARENT_NAME));
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public int getExpectedNumberOfFiles() {
		return expectedNumberOfFiles;
	}

	public void setExpectedNumberOfFiles(int expectedNumberOfFiles) {
		this.expectedNumberOfFiles = expectedNumberOfFiles;
	}

	public String getAgentUrl() {
		return agentUrl;
	}

	public void setAgentUrl(String value) {
		this.agentUrl = value;
	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

}
