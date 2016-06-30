/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Date;

import org.junit.Ignore;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.SystemTestCase;

import com.aqua.services.AgentConnection;
import com.aqua.services.fixtures.AgentFixture;

/**
 * This class tests all actions on the client. On Linux: 
 * 		1. Verify the host name under /etc/hosts is configured to the machine external IP 
 * 		2. Upload Runner and eclipse from root user without an environment settings ( su root)
 * 
 * @author Guy Chen
 */
@Ignore("Not supported anymore")
public class LocalAgentTest extends SystemTestCase {

	public AgentConnection agentSysObj;
	public static RemoteAgentClient client;
	private String destinationFolder = "";
	private String sourceFolder = "";
	public static String sWorkspace = "";
	public static String sScenario = "";

	public LocalAgentTest() {
		super();
		setFixture(AgentFixture.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp() throws Exception {
	
		
		sWorkspace  = JSystemProperties.getCurrentTestsPath();
		sScenario = "scenarios/default";
		agentSysObj = (AgentConnection) system.getSystemObject("AgentConnection");

	}

	/**
	 * Test running a scenario with the agent
	 */
	
	public void testRunAgent() throws Exception {

		// delete the file before running
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		
		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		client.run(RemoteAgentClient.SyncOptions.no);

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
			report.report("Agent was run and verified",0);
		} else {
			report.report("Error: Agent was not run and verified",1);
		}
		
	}

	/**
	 * Test switch a scenario on the agent
	 */
	
	public void testSwitchAScenario() throws Exception {

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenario", client);

		client.run(RemoteAgentClient.SyncOptions.no);
		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile2.txt")) {
			report.report("Agent was refreshed", 0);
		} else {
			report.report("Agent was not refreshed", 1);
		}
	}
	
	/**
	 * Test enable repeat on the agent
	 */
	
	public void testEnableRepeat() throws Exception {
		AgentThread agentThread;

		// delete the file before running
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		client.enableRepeat(true);
		client.setRepeat(5);
		agentThread = new AgentThread(10, client);
		agentThread.start();
		String fileDate = "";
		sleep(14000);
		int j = 0;
		for (int i = 0; i < 5 ; i++) {
			// set timer to check the file each 2 sec
			sleep(10000);
			if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
				File f;
				f = new File(agentSysObj.getAgentDir() + "MyFile.txt");
				Date date = new Date(f.lastModified());
				if (fileDate != date.toString()){
					j++;
					fileDate = date.toString();
				}
			} 
		}
			
		if (j==5){
			report.report("Test was repeat and verified", 0);
		}
		else {
			report.report("Error: Test was not repeat and verified", 1);
		}

		
		// after 65 sec
		client.enableRepeat(false);

	}

	/**
	 * Test disable a repeat on the agent
	 */
	
	public void testDisableRepeat() throws Exception {
		AgentThread agentThread;

		// delete the file before running
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		client.setRepeat(5);
		client.enableRepeat(false);
		agentThread = new AgentThread(10, client);
		agentThread.start();
		String[] fileDate = new String[2];
		sleep(15000);

		for (int i = 0; i < 2; i++) {
			// set timer to check the file each 11 sec
			sleep(10000);
			if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
				File f;
				f = new File(agentSysObj.getAgentDir() + "MyFile.txt");
				Date date = new Date(f.lastModified());
				fileDate[i] = date.toString();
			} else {
				report.report("Error: Test was not repeat and verified", 1);
			}

		}

		if (fileDate[1].compareTo(fileDate[0]) == 0) {
			report.report("Test was repeated", 0);

		} else {
			report.report("Fail: Test was not repeat", 1);
		}

	}

	/**
	 * Test enable endless repeat on the agent
	 */
	
	public void testSetRepeatToEndLessLoop() throws Exception {
		AgentThread agentThread;
		// make sure to disable it in the end!!
		// must stop it in the middle
		// delete the file before running
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		client.setRepeat(0);
		client.enableRepeat(true);
		agentThread = new AgentThread(10, client);
		agentThread.start();
		String[] fileDate = new String[5];
		sleep(15000);

		for (int i = 0; i < 5; i++) {
			// set timer to check the file each 11 sec
			sleep(15000);
			if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
				File f;
				f = new File(agentSysObj.getAgentDir() + "MyFile.txt");
				Date date = new Date(f.lastModified());
				fileDate[i] = date.toString();
			} else {
				report.report("Error: Endless Loop doesn't work");
			}

		}

		boolean bRepeat = true;

		for (int i = 1; i < 5; i++) {
			if (fileDate[i].compareTo(fileDate[i - 1]) == 0) {
				report.report("Error: Endless Loop doesn't work", 1);
				client.enableRepeat(false);
				bRepeat = false;
				break;
			}
		}

		if (bRepeat) {
			report.report("Endless loop works correctly", 0);
		}
		// In the end
		client.enableRepeat(false);

	}

	/**
	 * Test getting a log URL from the agent
	 */
	
	public void testGetLogUrl() throws Exception {
		
		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);
		URL url = client.getLogUrl();
		System.out.println("URL is: " + url.toString());

		if (url.toString().compareTo(
				"http://" + agentSysObj.getAgentHost().split(":")[0]
						+ ":8383/log\\current\\index.html") == 0
				|| url.toString().compareTo(
						"http://" + agentSysObj.getAgentHost().split(":")[0]
								+ ":8383/log/current/index.html") == 0) {
			report.report("Log URL is ok", 0);
		} else {
			report.report("Log URL is bad", 1);
		}

	}

	/**
	 * Test initializing reports on the agent
	 */
	
	public void testInitReporters() throws Exception {

		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		client.run(RemoteAgentClient.SyncOptions.no);
		client.initReporters();
		sleep(2);
		if (checkIfFileExists(agentSysObj.getAgentDir()
				+ "jsystemApp/log/current/test_1") == false) {
			report.report("InitReporters was verified", 0);
		} else {
			report.report("Error: InitReporters was not verified", 1);
		}
	}

	/**
	 * Test pause the agent actions
	 */

	public void testPauseAgent() throws Exception {

		AgentThread agentThread;

		emptyFile(agentSysObj.getAgentDir() + "jsystem0.log");
		
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		agentThread = new AgentThread(10, client);
		agentThread.start();
		sleep(3000);
		
		client.pause();

		sleep(13000);

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == false) {
			report.report("Agent was paused and verified", 0);
		} else {
			report.report("Error: Agent was not paused and verified", 1);
		}

		client.stop();

	}

	/**
	 * Test graceful stop on the agent
	 */

	public void testGracefulStopAgent() throws Exception {
		AgentThread agentThread;

		emptyFile(agentSysObj.getAgentDir() + "jsystem0.log");
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		agentThread = new AgentThread(10, client);
		agentThread.start();
		sleep(7000);
		client.gracefulStop();

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == false) {
			report.report("Agent was stopped and verified", 0);
		} else {
			report.report("Error: Agent was not stopped", 1);
		}

		GrepLog grepLog = new GrepLog();
		if (grepLog.grep(new File(agentSysObj.getAgentDir() + "jsystem0.log"),
				"sent graceful stop message")) {
			report.report("Graceful stop works correctly", 0);
		} else {
			report.report("Graceful stop doesn't work", 1);
		}

	}
	
	/**
	 * Test stopping the agent
	 */

	public void testStopAgent() throws Exception {
		AgentThread agentThread;

		emptyFile(agentSysObj.getAgentDir() + "jsystem0.log");
		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		agentThread = new AgentThread(10, client);
		agentThread.start();
		sleep(7000);
		client.stop();

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == false) {
			report.report("Agent was stopped and verified", 0);
		} else {
			report.report("Error: Agent was not stopped", 1);
		}
	}

	/**
	 * Test resuming the agent
	 */
	
	public void testResumeAgent() throws Exception {
		AgentThread agentThread;

		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);

		agentThread = new AgentThread(10, client);
		agentThread.start();
		sleep(2000);
		client.pause();

		client.resume();

		sleep(13000);

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
			report.report("Agent was resume and verified", 0);
		} else {
			report.report("Error: Agent was not resume and verified", 1);
		}

		client.stop();
	}
	
	/**
	 * Test setting an active scenario on the agent
	 */

	public void testSetActiveScenario() throws Exception {

		deleteFile(agentSysObj.getAgentDir() + "MyFile.txt");

		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/classes", "scenarios/agentScenario", client);

		System.out.println("Set Active scenario");
		agentSysObj.workspaceSettings(agentSysObj.getAgentAutomationRemoteDir()
				+ "jsystemApp/resources/jsystemAgentPorject/classes",
				"scenarios/agentScenarioDefault", client);
		client.setActiveScenario(ScenariosManager.getInstance()
				.getCurrentScenario());
		client.run(RemoteAgentClient.SyncOptions.no);

		sleep(11000);

		if (checkIfFileExists(agentSysObj.getAgentDir() + "MyFile.txt") == true) {
			report.report("Set Active Scenario verified", 0);
		} else {
			report.report("Error: Set Active Scenario was not verified", 1);
		}
	}

	public boolean checkIfFileExists(String fileFullPath) {
		File checkFile;

		checkFile = new File(fileFullPath);

		if (checkFile.exists() == true) {
			report.report("File " + fileFullPath + " exists");
			return true;
		} else {
			report.report("File " + fileFullPath + " doesn't exists");
			return false;
		}

	}

	public boolean emptyFile(String fileFullPath) throws Exception {
		File file;
		Writer output = null;

		if (checkIfFileExists(fileFullPath) == true) {
			file = new File(fileFullPath);
			output = new BufferedWriter(new FileWriter(file));
			output.write("");
			return true;
		}
		report.report("File was emptied");
		return false;
	}

	public boolean deleteFile(String fileFullPath) {
		File file;

		if (checkIfFileExists(fileFullPath) == true) {
			file = new File(fileFullPath);
			file.delete();

			if (checkIfFileExists(fileFullPath) == false) {
				report.report("File deleted successfully");
				return true;
			} else {
				report.report("File wasn't deleted");
				return false;
			}
		}
		report.report("File wasn't found");
		return false;
	}

	public void tearDown() throws Exception {
		
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, sWorkspace);
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, sScenario);

		//AgentFixture agentFixture = new AgentFixture();
		//agentFixture.tearDown();
		deleteFile("MyFile.txt");
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

}
