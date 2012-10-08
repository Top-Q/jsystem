/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.agent.client;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.projectsync.ProjectZip;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

import com.aqua.services.AgentConnection;
import com.aqua.services.analyzers.AnalyzerFileFinder;
import com.aqua.services.fixtures.AgentDefaultProjectFixture;

/**
 * This class tests all actions on the client. On Linux: 1. Verify the host name
 * under /etc/hosts is configured to the machine external IP 2. Upload Runner
 * and eclipse from root user without an environment settings ( su root)
 * 
 * @author Guy Chen
 * 
 */
public class LocalAgentTest extends SystemTestCase {

	private AgentConnection agentSysObj;
	public static String sWorkspace = "";
	public static String sScenario = "";
	String agentDir = "";

	public LocalAgentTest() {
		super();
		setFixture(AgentDefaultProjectFixture.class);
		setTearDownFixture(RootFixture.class);
	}

	public void setUp() throws Exception {
		agentDir = RunProperties.getInstance().getRunProperty("agentDir");
		sWorkspace = JSystemProperties.getCurrentTestsPath();
		sScenario = "scenarios/default";
		agentSysObj = (AgentConnection)system.getSystemObject("AgentConnection");
		// Delete any leftover from previous Tests
		agentSysObj.deleteFile(agentDir + "/MyFile.txt");
		agentSysObj.deleteFile(agentDir + "/MyFile2.txt");
	}

	public void	testThatDoNothing() throws Exception {
	
	}

	/**
	 * Test running a scenario with the agent Simple test, checking the .run()
	 * method
	 */
	public void testRunAgent() throws Exception {
		report.report("Set the scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		agentSysObj.client.run();
		sleep(5000);
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt");
		agentSysObj.analyze(ff, false, false, false);
	}

	/**
	 * Test switch a scenario on the agent Phase 1 : runs the current scenario
	 * that create the MyFile.txt Phase 2 : switch scenario Phase 3 : runs the
	 * new scenario that create the MyFile2.txt
	 */
	public void testSwitchAScenario() throws Exception {

		// Start Phase 1
		report.report("Set the scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
				
		report.report("Start scenario...");
		agentSysObj.client.run();
		sleep(5000);
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt","Scenario default runs properly");
		agentSysObj.analyze(ff, false, false, false);
		// End of Phase 1

		sleep(2);
		agentSysObj.client.stop();

		// Start Phase 2
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault2");
		// End Phase 2

		// Start Phase 3
		agentSysObj.client.run();
		sleep(20000);
		AnalyzerFileFinder ff2 = new AnalyzerFileFinder("MyFile2.txt","Scenario successfully switched");
		agentSysObj.analyze(ff2, false, false);
		// End of Phase 3
	}

	/**
	 * Test that the agent resumes after restart at the specific point he has
	 * stopped.
	 */
	public void testResumeAfterRestartAgent() throws Exception {

		// Local Variable
		AgentThread agentThread;
		// End Local Variable
        report.report("Delete files");
		agentSysObj.deleteFile(agentDir + "/MyFile.txt");
		agentSysObj.deleteFile(agentDir + "/MyFile2.txt");

		agentSysObj.client.setActiveScenario("scenarios/agentScenarioRestart");

		// Start Phase 1

		agentThread = new AgentThread(agentSysObj.client);
		agentThread.start();
		sleep(5000);
		agentSysObj.client.shutAgentDown();
		sleep(5000);
		AnalyzerFileFinder ff2 = new AnalyzerFileFinder("MyFile.txt", false, "The engine stoped before the file create - as expected");
		agentSysObj.analyze(ff2);

		// End of Phase 1
		// restart the engine

		agentSysObj = (AgentConnection) system.getSystemObject("AgentConnection");
		agentSysObj.startAgent();
		sleep(5000);
		agentSysObj.client = new JSystemAgentClient(agentSysObj.getAgentHost());
		agentSysObj.client.init();

		agentSysObj.client.run();

		// check the test started running from the middle of the scenario,

		AnalyzerFileFinder ff3 = new AnalyzerFileFinder("MyFile.txt",false,"The file: 'MyFile.txt' doesnt created - as expected");
		agentSysObj.analyze(ff3);

		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile2.txt","The test started from the middle of the scenario - as expected");
		agentSysObj.analyze(ff);
		
		report.step("Set back Active scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
	}

	/**
	 * Test that the agent resumes after restart at the specific point he has
	 * stopped.
	 */
	public void testLogAfterRestartAgent() throws Exception {

		report.step("Synchonize before the scenario start");
		synchoronize();
		
		//need to init the repoter cause we check the log folders.
		report.report("init the reporter ");
		agentSysObj.client.initReporters();
		
		// Local Variable
		AgentThread agentThread;
		// End Local Variable
        report.report("Set active scenario to : agentScenarioRestart"); 
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioRestart"); //do create file1 and file2

		// Start Phase 1
		agentThread = new AgentThread(agentSysObj.client);
		report.report("start the agent");
		agentThread.start();
		sleep(8000);
		
		report.report("Shut down the agent");
		agentSysObj.client.shutAgentDown();
		sleep(10000);
		// End of Phase 1

		//check that the engine stopped at the correct point.
		report.step("check that the engine stopped at the correct point");
		AnalyzerFileFinder ff1 = new AnalyzerFileFinder("/log/current/test_2/",false,"The log stopped at the right point when stopped the engine - as expected");
		agentSysObj.analyze(ff1, false, false, false);

		// restart the engine
		report.step("restart the agent");
		agentSysObj = (AgentConnection) system.getSystemObject("AgentConnection");
		agentSysObj.startAgent();
		report.report("Wait for test to finish...");
		sleep(35000);

		report.report("print list of files from: " + agentDir + "/log/current/");
		File f2 = new File(agentDir + "/log/current/");
		String[] dirs = FileUtils.listDirs(f2);
		for (String dir : dirs) {
			report.report(dir.toString());
		}
		
		AnalyzerFileFinder ff2 = new AnalyzerFileFinder("log/current/test_2/","The log continue after restart the engine - as expected");
		agentSysObj.analyze(ff2, false, false, false);
		
		report.step("Synchonize after the scenario end");
		synchoronize();
	}

	/**
	 * Testing the .run() method with UUID parameter the UUID is the specific
	 * test from the scenario
	 * 
	 * 
	 * @throws Exception
	 */
	public void testRunWithUUID() throws Exception {

		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, agentDir + "/projects/jsystemAgentProject/classes");
		ScenariosManager.init();
		Scenario s = ScenariosManager.getInstance().getScenario("scenarios/agentScenarioDefault");
		String uuid = s.getTest(0).getFullUUID();

		agentSysObj.client.setActiveScenario(s);
		report.report("The Current Active Scenario "+ agentSysObj.client.getActiveScenario());
		agentSysObj.client.run(uuid);

		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt","Agent Perform the 1st test in Current scenario");
		agentSysObj.analyze(ff, false, false, false);

		sleep(5000);
		report.report("The Current Active Scenario "+ agentSysObj.client.getActiveScenario());
	}
	
	/**
	 * Testing the .run() method with scenario name and the UUID parameters
	 * The UUID is the specific test from the requested scenario
	 * 
	 * This operation performing scenario switching and then running the test
	 * from the scenario by UUID
	 * 
	 * @throws Exception
	 */
	public void testRunWithScenarioAndUUID() throws Exception {
		
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, agentDir + "/projects/jsystemAgentProject/classes");
		ScenariosManager.init();
		Scenario s = ScenariosManager.getInstance().getScenario("scenarios/agentScenarioDefault3");
		String uuid = s.getTest(1).getFullUUID();

		agentSysObj.client.run("scenarios/agentScenarioDefault3", uuid);
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile2.txt","Agent Perform the 3rd test in agentScenarioDefault3.xml scenario");
		agentSysObj.analyze(ff, false, false, false);
	}

	/**
	 * Printing the current Project name
	 * 
	 * @throws Exception
	 */
	public void testGetCurrentProjectName() throws Exception {
		agentSysObj.workspaceSettings(agentDir+ "jsystemApp/resources/jsystemAgentPorject/classes",	"scenarios/agentScenarioDefault");
		report.report(agentSysObj.client.getCurrentProjectName(), 0);
		sleep(5000);
	}

	/**
	 * Test enable repeat on the agent
	 */
	public void testEnableRepeat() throws Exception {
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.report("Set the scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		agentSysObj.client.enableRepeat(true);
		agentSysObj.client.setRepeat(3);
		agentThread = new AgentThread(agentSysObj.client);

		//delete the summary file.
		String summaryFileName = agentDir + File.separator + "summary.properties";
		FileUtils.deleteFile(summaryFileName);
		
		//check that the summary file delete successfully
		File file1 = new File(agentDir + File.separator + "summary.properties");
		if(!file1.exists()){
			report.report("The summary file delete successfully - as expected",true);
		} else{
			report.report("The summary file doesnt delete successfully - not as expected",false);
		}

		//start the agent
		agentThread.start();
		sleep(60000);

		//read from the summary file.
		Properties p = FileUtils.loadPropertiesFromFile(summaryFileName);
		String value = p.getProperty("Number");

		if (value.equals("3")) {
			report.report("Test was repeated", true);

		} else {
			report.report("Value is: "+Integer.parseInt(value)+ " and should be 3");
			report.report("Failed: Test was not repeated", false);
		}
		agentSysObj.client.enableRepeat(false);

	}

	/**
	 * Test disable a repeat on the agent
	 */
	public void testDisableRepeat() throws Exception {
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		agentSysObj.client.setRepeat(5);
		agentSysObj.client.enableRepeat(false);
		agentThread = new AgentThread(agentSysObj.client);

		//delete the summary file.
		String summaryFileName = agentDir + File.separator + "summary.properties";
		FileUtils.deleteFile(summaryFileName);

		//start the agent
		agentThread.start();
		sleep(30000);

		//read from the summary file.
		Properties p = FileUtils.loadPropertiesFromFile(summaryFileName);
		String value = p.getProperty("Number");

		if (value.equals("5")) {
			report.report("Fail: Test was repeated - not as expected", false);

		} else {
			report.report("Test was not repeated - as expected", true);
		}

	}

	/**
	 * Test enable endless repeat on the agent
	 */
	public void testSetRepeatToEndLessLoop() throws Exception {
		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		// make sure to disable it in the end!!
		// must stop it in the middle

		agentSysObj.client.setRepeat(0);
		agentSysObj.client.enableRepeat(true);
		agentThread = new AgentThread(agentSysObj.client);

		//delete the summary file.
		String summaryFileName = agentDir + File.separator + "summary.properties";
		FileUtils.deleteFile(summaryFileName);

		//start the agent
		agentThread.start();
		sleep(60000);


		//read from the summary file.
		Properties p = FileUtils.loadPropertiesFromFile(summaryFileName);
		String value = p.getProperty("Number");

		if (Integer.parseInt(value) > 3) {
			report.report("Endless loop works correctly", true);

		} else {
			report.report("Fail: Endless loop doesnt work correctly", false);
		}

		// In the end
		agentSysObj.client.enableRepeat(false);

	}

	/**
	 * Test getting a log URL from the agent
	 */
	public void testGetLogUrl() throws Exception {
		URL url = agentSysObj.client.getLogUrl();
		report.report("URL is: " + url.toString());

		if (url.toString().compareTo("http://" + agentSysObj.getAgentHost().split(":")[0]+ ":8383/log\\current\\index.html") == 0
				|| url.toString().compareTo("http://" + agentSysObj.getAgentHost().split(":")[0]+ ":8383/log/current/index.html") == 0) {
			report.report("Log URL is valid", 0);
		} else {
			report.report("Log URL is invalid", 1);
		}
	}

	/**
	 * Test initializing reports on the agent
	 */
	public void testInitReporters() throws Exception {
		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		report.report("Start running scenario");
		agentSysObj.client.run();

		//check that the folder 'test_1' is exist.
		String folderLocation=(agentDir + File.separator+"log"+File.separator+"current"+File.separator+"test_1"+File.separator);
		File file1 = new File(folderLocation);
		if(file1.exists()){
			report.report("The folder 'test_1' exists before init the repoter - as expected",true);
		} else{
			report.report("The folder 'test_1' doesnt exist before init the repoter - not as expected",false);
		}

		//init reporter
		agentSysObj.client.initReporters();

		
		//check that the folder 'test_1' doesn't exist cause we init the reporter.
		if(file1.exists()){
			report.report("Init reporter is not working fine",false);
		} else{
			report.report("Init reporter is woring fine",true);
		}
	}

	/**
	 * Test pause the agent actions
	 */
	public void testPauseAgent() throws Exception {
		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.report("set the scenario to run in endless loop");
		agentSysObj.client.setRepeat(0);
		agentSysObj.client.enableRepeat(true);
		agentThread = new AgentThread( agentSysObj.client);
		
		//delete the summary file.
		report.report("delete summary.properties file");
		String summaryFileName = agentDir + File.separator + "summary.properties";
		FileUtils.deleteFile(summaryFileName);
		
		report.report("check that the summary.properties file deleted successfully");
		if(FileUtils.exists(summaryFileName)){
			report.report("summary.properties was not deleted - not as excepted",false);
		}else{
			report.report("summary.properties was deleted - as excepted",true);
		}
		
		report.report("start the agent");
		agentThread.start();
				
		report.report("pause the scenario after 10 seconds...");
		sleep(10000);
		agentSysObj.client.pause();
		report.report("wait 1 min to see if the scenrio still running although we pause it...");
		sleep(60000);

		
		//read from the summary file.
		report.report("read value from the file: "+summaryFileName);
		Properties p = FileUtils.loadPropertiesFromFile(summaryFileName);
		String value = p.getProperty("Number");

		report.report("Check if the scenario paused by checking the value from the property file...");
		if (Integer.parseInt(value) > 3) {
			report.report("Fail: The agent was not paused(value = "+value+")  - not as expected", false);

		} else {
			report.report("The agent was paused  - as expected", true);
		}
		
		// In the end
		agentSysObj.client.enableRepeat(false);
		agentSysObj.client.stop();
		
		

	}

	/**
	 * Test graceful stop on the agent
	 */
	public void testGracefulStopAgent() throws Exception {
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.step("Set Active scenario to be agentScenarioDefault4");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault4");
		
		report.step("Delete files");
		agentSysObj.deleteFile(agentDir + "/MyFile.txt");
		
		report.step("verify that the file: 'myfile.txt' doesnt exist.");
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt",false,"The file 'MyFile.txt' deleted successfully");
		agentSysObj.analyze(ff, false, false, false);
		
		report.report("delete the summary file");
		String summaryFileName = agentDir + File.separator + "summary.properties";
		FileUtils.deleteFile(summaryFileName);
		
		report.step("verify that the file: 'summary.properties' doesnt exist.");
		AnalyzerFileFinder ff2 = new AnalyzerFileFinder("summary.properties",false,"The file 'summary.properties' deleted successfully");
		agentSysObj.analyze(ff2, false, false, false);
		
		report.step("Empty 'jsystem0.log' file");
		agentSysObj.emptyFile(agentDir + "/jsystem0.log");

		report.step("Start running the scenario");
		agentThread = new AgentThread(agentSysObj.client);
		agentThread.start(); //run
		
		report.step("Graceful stop the agent after 15 seconds...");
		sleep(15000);
		agentSysObj.client.gracefulStop();
		report.report("Graceful stop has activated");
		
		report.step("Wait 60 sec for the first test to finish");
		sleep(60000);

		report.report("read from the summary file");
		Properties p = FileUtils.loadPropertiesFromFile(summaryFileName);
		String value = p.getProperty("Number");
		
		report.step("Check if the scenario stopped by checking the value from the property file...");
		if ((Integer.parseInt(value) < 50)) {
			report.report("The agent was stoped gracefuly(vlaue="+Integer.parseInt(value)+")  - as expectd", true);
		} else {
			report.report("Fail: The agent was not stoped gracefuly(vlaue="+Integer.parseInt(value)+")  - not as expected", false);
		}
	    
		report.step("Check that the message: 'sent graceful stop message' apear in the file 'jsystem0.log'");
		GrepLog grepLog = new GrepLog();
		if (grepLog.grep(new File(agentDir + "/jsystem0.log"),"sent graceful stop message")) {
			report.report("The message: 'sent graceful stop message'  apear in the file 'jsystem0.log'", true);
		} else {
			report.report("The message: 'sent graceful stop message' doesnt apear in the file 'jsystem0.log'", false);
		}
		
		report.report("check that the second test(the one that create the file: 'MyFile.txt') has not been started");
		sleep(20000);
		AnalyzerFileFinder ff3 = new AnalyzerFileFinder("MyFile.txt",false,"Agent stop gracefully");
		agentSysObj.analyze(ff3, false, false, false);
	}

	/**
	 * Test stopping the agent
	 */
	public void testStopAgent() throws Exception {
		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.report("Start running the scenario...");
		agentThread = new AgentThread(agentSysObj.client);
		agentThread.start();
		sleep(7000);
		agentSysObj.client.stop();

		report.report("Check that the test doesnt create the file 'MyFile.txt'");
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt",false,"Agent has stopped successfully");
		agentSysObj.analyze(ff, false, false, false);
	}

	/**
	 * Test resuming the agent
	 */
	public void testResumeAgent() throws Exception {
		// Local Variable
		AgentThread agentThread;
		// End Local Variable

		report.report("init the reporters");
		agentSysObj.client.initReporters();
		
		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		report.report("Start running the test");
		agentThread = new AgentThread( agentSysObj.client);
		agentThread.start();

		report.report("Wait 8 seconds and pause the test");
		sleep(8000);
		agentSysObj.client.pause();

		report.report("Wait 10 seconds and check that the test realy stopped and didnt create the file: 'MyFile.txt'");
		sleep(10000);
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt",false,"The test paused - as expected");
		agentSysObj.analyze(ff, false, false, false);
		
		report.report("Resume running the test");
		agentSysObj.client.resume();
		sleep(13000);

		report.report("Check that the test resumed and create the file: 'MyFile.txt'");
		AnalyzerFileFinder ff1 = new AnalyzerFileFinder("MyFile.txt","The agent resume running the test");
		agentSysObj.analyze(ff1, false, false, false);
		
		agentSysObj.client.stop();
	}

	/**
	 * Test setting an active scenario on the agent
	 */
	public void testSetActiveScenario() throws Exception {

		report.step("Set Active scenario");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");

		report.report("Start running the scenario: 'agentScenarioDefault'");
		agentSysObj.client.run();
		sleep(15000);

		report.report("Check that the agent runs the right scenario");
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile.txt","The 'setting active scenario is woring fine");
		agentSysObj.analyze(ff, false, false, false);
		agentSysObj.client.stop();
		
		report.report("Init reporter and change scenario to: agentScenarioDefault2");
		agentSysObj.client.initReporters();
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault2");
		
		report.report("Start running the scenario: 'agentScenarioDefault2'");
		agentSysObj.client.run();
		sleep(15000);
		
		report.report("Check that the agent runs the right scenario");
		AnalyzerFileFinder ff1 = new AnalyzerFileFinder("MyFile2.txt","The 'setting active scenario is woring fine");
		agentSysObj.analyze(ff1, false, false, false);
		agentSysObj.client.stop();
		
		report.step("Set Active scenario back to: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
	}

	/**
	 * Printing the Agent ID.
	 * 
	 * @throws Exception
	 */
	public void testGetID() throws Exception {

		report.step("Testing The JSystemAgent getId() Method");
	
		String input = agentSysObj.client.getId();
		Pattern p1 = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
		Pattern p2 = Pattern.compile("\\w+:\\d+");
		Matcher m1  = p1.matcher(input);
		Matcher m2 = p2.matcher(input);
		if (m1.find() || m2.find()) {
			report.report("getId() Method works fine",true);
		}else{
			report.report("getId() Method doesnt work fine",true);
		}
	}

	/**
	 * Printing the JRunner version at the agent
	 * 
	 * @throws Exception
	 */
	public void testGetEngineVersion() throws Exception {

		report.step("Testing The JSystemAgent getEngineVersion() Method");
		try {
			report.report("Agent " + agentSysObj.client.getId()	+ " using Jrunner version "	+ agentSysObj.client.getEngineVersion(), 0);
		} catch (Exception e) {
			report.report("Unable to get " + agentSysObj.client.getId()	+ "JRunner version", 1);
		}
	}

	/**
	 * Getting & Setting the Active Scenario
	 * 
	 * @throws Exception
	 */
	public void testSetGetActiveScenario() throws Exception {

		report.step("Set the scenario to be: 'agentScenarioDefault2'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault2");
		
		report.step("Check that the 'getActiveScenario' is working fine");
		if(agentSysObj.client.getActiveScenario().equals("agentScenarioDefault2")){
			report.report("The 'getActiveScenario' method works fine",true);
		}else{
			report.report("The 'getActiveScenario' method doesnt work fine",false);
		}
		
		report.step("Start the agent");
		agentSysObj.client.run();

		report.step("Check that the 'setActiveScenario' method is working fine");
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile2.txt","'setActiveScenario' method works fine");
		agentSysObj.analyze(ff);
		
		report.step("Set back the scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
	}

	/**
	 * Testing The .GetProjectName() method
	 * 
	 * @throws Exception
	 */
	public void testGetProjectName() throws Exception {
		report.step("Check that 'getCurrentProjectName' method is working fine");

		if (agentSysObj.client.getCurrentProjectName().equals("jsystemAgentProject")) {
			report.report("The current project name is : " + agentSysObj.client.getCurrentProjectName()+" - as expected", true);
		}else{
			report.report("Error while executing 'getCurrentProjectName' Method", false);
		}
	}
	
	/**
	 * This Test changing the project and performing the default scenario
	 * 
	 * @throws Exception
	 */
	public void testChangeProject() throws Exception {

		String projectDir = RunProperties.getInstance().getRunProperty("projectDir");
		report.step("Changing Project");
		try {

			agentSysObj.client.changeProject(projectDir + "/resources/MyProject/tests");
		} catch (Exception e) {
			report.report("Unable to find the new project", false);
		}

		report.step("Set the scenario to be: 'agentScenarioDefault'");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioDefault");
		
		report.step("Start the agent");
		agentSysObj.client.run();

		report.step("Check that the agent run the test from the other project(MyProject)");
		AnalyzerFileFinder ff = new AnalyzerFileFinder("MyFile3.txt","The project was successfully switched");
		agentSysObj.analyze(ff);
	}
	
	public void tearDown() throws Exception {
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, sWorkspace);
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, sScenario);
		ScenariosManager.init();
//		agentSysObj.client.shutAgentDown();
		
	}
	
	
	public void testcheckListener() throws Exception{
		agentSysObj.workspaceSettings(RunProperties.getInstance().getRunProperty("projectDir")+"/resources/jsystemAgentProject/classes","scenarios/agentScenarioEvents");
		ScenariosManager.init();
		
		//the following scenario contains 12 tests: 3 that pass , 3 with failure, 3 with warning and 3 with error in random order!!
		report.step("change the active scenario to be: 'agentScenarioEvents' ");
		agentSysObj.client.setActiveScenario("scenarios/agentScenarioEvents");
		report.step("add listener");
		MyListener myListener = new MyListener();
		agentSysObj.client.addListener(myListener);
		report.step("start the scenario");
		agentSysObj.client.run();
		sleep(3000);
		
		report.report("check that the listener works");
		
		if(myListener.getTotalTests() != 12){
			report.report("Not all tests run. missing: "+(12-myListener.getTotalTests()),false);
		}else{
			report.report("All the 12 tests run",true);
		}
		
		if(myListener.getTestsThatFailed() != 3){
			report.report("problem with 'Failure Event' - counter= "+myListener.getTestsThatFailed(),false);
		}else{
			report.report("'Failure Event' - counter = 3",true);
		}
		
		if(myListener.getTestsWithWarning() != 3){
			report.report("problem with 'Warning Event' - counter ="+myListener.getTestsWithWarning(),false);
		}else{
			report.report("'Warning Event' - counter = 3",true);
		}
		
		if(myListener.getTestsThatThrowException() != 3){
			report.report("problem with 'Error Event' - counter= "+myListener.getTestsThatThrowException(),false);
		}else{
			report.report("'Error Event' - counter = 3",true);
		}
	}
	
    /**
     * failed test.
     *  
     * @throws Exception
     */
	public void testFailed() throws Exception{
		report.report("throw run time exception");
		throw new RuntimeException();
	}

	private void printListOfDir(){
		report.report("print list of folders");
		File f2 = new File(agentDir + "/log/current/");
		report.report("log directory : " + f2.getAbsolutePath());
		String[] dirs = FileUtils.listDirs(f2);
		for (String dir : dirs) {
			report.report(dir.toString());
		}
	}
	
	private void printListOfFiles(){
		report.report("print list of files from : " + agentDir);
		File f2 = new File(agentDir);
		String[] files = FileUtils.listFiles(f2);
		for (String file : files) {
			report.report(file.toString());
		}
	}
	
	private void synchoronize() throws Exception{
		// New Local Variable
		AgentConnection agentConnection = (AgentConnection)system.getSystemObject("AgentConnection");
		String projectClasses = 
			RunProperties.getInstance().getRunProperty("projectDir")+"/resources/jsystemAgentProject/classes";
		String sutFile = "AgentConnection.xml";
		String currentScenario = "scenarios/agentScenarioDefault4";
		File zippedProject = null;
		// End New Local Variable
		// Zipping
		report.step("Creating zip file from " + projectClasses);
		ProjectZip zipper = new ProjectZip(new File(projectClasses));
		zippedProject = zipper.zipProject(ProjectComponent.values());
		String projectName = ProjectZip.getProjectNameFromClassesPath(new File(	projectClasses));
		report.step("Sending the project " + projectName+ " in zip file");
		agentConnection.client.synchronizeProject(zippedProject,projectName, currentScenario, sutFile,null,null);
//		agentSysObj.client.synchronizeProject(zippedProject,projectName, currentScenario, sutFile);
		
		// End Zipping
	report.step("End of Synchronize - zipping and etc.");
	}
}
