/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

import com.aqua.sysobj.conn.CliConnectionImpl;

/**
 * This class is a system object that contain all SUT data and implements the
 * basic operations on the agent.
 * 
 * @author Guy Chen
 * 
 */

public class AgentConnection extends SystemObjectImpl {
	public JSystemAgentClient client;

	private String agentHost;

	private String runAgent;

	private String runnerDir;
	
	public CliConnectionImpl cli; 
	
	private Process agentProcess;

	public AgentConnection() {
		super();
	}

	public void init() throws Exception {
	
		report.step("In agent init method");
		super.init();

		File f2 = new File(System.getProperty("user.dir"));
		File f3 = new File(f2.getParent());
		File f = new File(FileUtils.replaceSeparator(f3 + "/runnerAgent"));
		File f5 = new File(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER));
		File f6 = new File(f5.getParent());
		String projectDir = FileUtils.replaceSeparator(f6.getAbsolutePath());
		String agentDir = FileUtils.replaceSeparator(f.getAbsolutePath());
		String runnerDir = FileUtils.replaceSeparator(System.getProperty("user.dir"));
		String runnerDirFromSut = sut.getValue("/sut/AgentConnection/runnerDir/text()");
		RunProperties.getInstance().setRunProperty("agentDir", agentDir);
		if (! runnerDirFromSut.equals(" ")) {
			RunProperties.getInstance().setRunProperty("runnerDir", runnerDirFromSut);
		}else{
			RunProperties.getInstance().setRunProperty("runnerDir", runnerDir);
		}
		RunProperties.getInstance().setRunProperty("projectDir", projectDir);

		setTestAgainstObject(RunProperties.getInstance().getRunProperty("agentDir"));
	}
	

	/**
	 * Starts the agent
	 */

	public void startAgent() throws Exception {
		String agentDir = RunProperties.getInstance().getRunProperty("agentDir");
		File f = new File(agentDir + runAgent);
		if (!f.exists()) {
			throw new Exception("exec file not found");
		}
		Command command = new Command();
		report.report("Start Agent " + agentDir + runAgent);
		command.setCmd(new String[] { agentDir + runAgent});
		command.setDir(new File(agentDir));
		Exception t = null;
		
		for (int i = 0 ; i < 4;i++){ //try 4 times to run agent 
			try {
				t = null;
				Execute.execute(command, false, true, true, true);
				break;
			}catch (IOException ioe){
				t= ioe;
				Thread.sleep(3000);
				report.report("Failed starting agent attempt " + i + "  error is" + ioe.getMessage());
			}
		}
		if (t != null){
			throw t;
		}
		agentProcess = command.getProcess();
		
		report.report("Waiting for agent to start ...");
		Thread.sleep(10000);
		initClient();
	}
	
	public boolean stopAgent() throws Exception {
		if (agentProcess != null){
			agentProcess.destroy();
			agentProcess = null;
			Thread.sleep(30000);
		}
		for (int i = 0; i < 4 ; i++ ){
			try {
				initClient();
				client.shutAgentDown();
				Thread.sleep(30000);
			}catch (Exception e) {
				report.report("The agent is down  - as expected",true);
				return true;
			}
		}
		return false;
	}
	public void initClient() throws Exception {
		client = new JSystemAgentClient(getAgentHost());
		client.init();		
	}
	/**
	 * Create a directory
	 */

	public boolean createDirectory(File dstDir) throws IOException {
		if (!dstDir.exists()) {
			dstDir.mkdir();
			return true;
		} else {
			report.report("the Directory " + dstDir + " is already exists", 1);
			return false;
		}
	}

	/**
	 * Setting the workspace for the agent to run
	 * 
	 * @param systemAgentClient
	 * 
	 */
	public void workspaceSettings(String sWorkspace, String sScenario)
	throws Exception {
		JSystemProperties jsystem = JSystemProperties.getInstance();
		jsystem.setPreference(FrameworkOptions.CURRENT_SCENARIO, sScenario);
		jsystem.setPreference(FrameworkOptions.TESTS_CLASS_FOLDER, sWorkspace);
	}

	public boolean emptyFile(String fileFullPath) throws Exception {
		File checkFile = new File(fileFullPath);
		Writer output = null;

		if (checkFile.exists()) {
			output = new BufferedWriter(new FileWriter(checkFile));
			output.write("");
			return true;
		} else {
			report.report("the File " + fileFullPath + " was emptied");
		}
		return false;
	}

	public boolean deleteFile(String fileFullPath) {
		File checkFile = new File(fileFullPath);
		if (checkFile.exists()) {
			if (!checkFile.delete()) {
				throw new RuntimeException("Failed deleting " + checkFile.getPath());
			}
		}
		return true;
	}

	public boolean deleteDirectory(File path) {
        if (path.exists()) {
        	if (path.isDirectory()){
        		File[] files = path.listFiles();
        		for (int i = 0; i < files.length; i++) {
                	deleteDirectory(files[i]);
                }
            }
            if (!path.delete()) {
                throw new RuntimeException("Failed deleting "+path.getPath());
            }else {
				report.report(path.getPath()+" deleted successfuly");
			}
        }
        return true;
    }

	public void createAgentDir() throws Exception {

		// setup of agentDir and runnerDir

		String projectDir = RunProperties.getInstance().getRunProperty(
		"projectDir");
		String agentDir = RunProperties.getInstance()
		.getRunProperty("agentDir");
		String runnerDir = RunProperties.getInstance().getRunProperty(
		"runnerDir");

		report.report("agentDir " + agentDir);
		report.report("runnerDir " + runnerDir);
		report.report("projectDir " + projectDir);

		String[] dirs = new String[] { "thirdparty", "lib" };

		report.step("Deleting folder " + agentDir);

		FileUtils.deltree(agentDir);

		report.step("Copying from " + runnerDir + " to " + agentDir);

		for (String dir : dirs) {
			FileUtils.copyDirectory(new File(runnerDir, dir), new File(
					agentDir, dir));
		}
		String[] filesInRoot = FileUtils.getFileNameStartingWith(runnerDir, "");
		for (String file : filesInRoot) {
			try {
				File toCopy = new File(runnerDir, file);
				if (!toCopy.isDirectory()) {
					FileUtils.copyFile(toCopy, new File(agentDir, file));
				}
			} catch (Exception e) {
				report.report("Failed Copying file " + file, StringUtils
						.getStackTrace(e), true);
			}
		}

		updateAgentJsystemFile(agentDir);

		if ("Linux".equalsIgnoreCase(System.getProperty("os.name"))) {

			Runtime.getRuntime().exec("chmod 777 -R " + agentDir);

		}
		
		report.step("delete unnecessary files from agentDir");
		deleteUnecessaryFiles(agentDir);	
		
		report.report("create 'jsystem.properties' file with one property:'stdout.file.name'");
		Properties p = new Properties();
		p.setProperty("stdout.file.name", "stdoutFile.txt");
		p.setProperty("agent.server.ftp.port", "2122");
		FileUtils.savePropertiesToFile(p, agentDir+"/jsystem.properties");
	}

	private void updateAgentJsystemFile(String agentDir) throws IOException {
		String agentJsystemFile = agentDir + File.separator
		+ CommonResources.JSYSTEM_PROPERTIES_FILE_NAME;
		Properties p = FileUtils.loadPropertiesFromFile(agentJsystemFile);

		p.setProperty(FrameworkOptions.AGENT_FTP_PORT.toString(), "2122");

		FileUtils.savePropertiesToFile(p, agentJsystemFile);
	}

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public String getRunAgent() {
		return runAgent;
	}

	public void setRunAgent(String runAgent) {
		this.runAgent = runAgent;
	}

	public String getRunnerDir() {
		return runnerDir;
	}

	public void setRunnerDir(String runnerDir) {
		this.runnerDir = runnerDir;
	}

	private void deleteUnecessaryFiles(String dirName){
		File dir = new File(dirName);
		File[] files = dir.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				if(name.startsWith(".") || name.endsWith("log")|| name.endsWith("bin")|| name.startsWith("runnerState") || name.equals("jsystem.properties")){
					return true;
				}
				return false;
			}
		});
		
		
		for (int i = 0; i < files.length; i++) {
			report.report("delete file:  "+files[i]);
			files[i].delete();
		}
	}
}
