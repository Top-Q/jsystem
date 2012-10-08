/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.util.Map;
import java.util.Properties;

import jsystem.runner.agent.ProjectComponent;

/**
 * For interface documentation please see {@link RunnerAgent}
 * @author goland
 */
public interface RunnerAgentMBean {
	
	/**
	 */
	public Properties getAgentProperties() throws Exception;

	/**
	 */
	public String getAgentVersion() throws Exception;

	/**
	 */
	public String getEngineExecutionState() throws Exception;

	/**
	 */
	public void extractProjectZip(String projectFileRelativePath, ProjectComponent[] components) throws Exception;
	
	
	/**
	 */
	public void changeProject(String projectPath) throws Exception;

	
	/**
	 */
	public String getCurrentProjectName() throws Exception;

	/**
	 */	
	public Map<ProjectComponent, String> getProjectMD5(ProjectComponent[] componet) throws Exception;

	/**
	 */
	public void setActiveScenario(String scenarioName) throws Exception;

	/**
	 */
	public String getActiveScenario() throws Exception;

	/**
	 */
	public void run() throws Exception;
	
	
	/**
	 */
	public void stop() throws Exception;
	
	/**
	 */
	public void pause() throws Exception;

	/**
	 */
	public void gracefulStop() throws Exception;
	
	/**
	 */
	public void resume() throws Exception;
	
	
	/**
	 */
	public void setRepeat(int number) throws Exception;

	/**
	 */
	public void enableRepeat(boolean enable) throws Exception;
		
	/**
	 */
	public void setSutFile(String sutFile) throws Exception;
	
	/**
	 */
	public void initReporters();
	
	/**
	 */
	public void returnMessageConfirmationResult(int result,long sequenceNumber) throws Exception;
	
	/**
	 */
	public void setJsystemProperties(Properties props) throws Exception;
	
	/**
	 */
	public void refresh() throws Exception;
	
	/**
	 * 
	 */
	public void run(String rootScenarioName,String uuid) throws Exception;
	
	/**
	 * 
	 */
	public String getCurrentProjectMD5() throws Exception;
	
	/**
	 * 
	 */
	public Properties getRunProperties() throws Exception;
	
	/**
	 * 
	 */
	public void setEnabledTests(int[] selectedTests) throws Exception;
}
