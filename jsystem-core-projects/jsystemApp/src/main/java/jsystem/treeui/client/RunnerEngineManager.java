/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.io.File;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngineExecutionState;
import jsystem.runner.agent.server.RunnerEngineImpl;
import jsystem.treeui.ExecutionWorker;
import jsystem.treeui.TestRunner;
import jsystem.treeui.TestTreeView;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.StringUtils;

/**
 * The runner is composed of two modules:<br>
 * 1. Scenario studio - the module with which the user builds a scenario, sets
 * its parameters etc'<br>
 * 2. Execution engine - the engine that runs the scenario.<br>
 * <br>
 * The runner application can run with the engine embedded in it ({@link RunnerEngineImpl}
 * or it can connect to a remote agent {@link RemoteAgentClient}. <br>
 * In both cases the interface that the runner application works with is
 * identical - {@link RunnerEngine}.<br>
 * This factory class creates and manages the engine implementation with which
 * the runner works with.<br>
 * 
 * This class also activates a {@link ConnectionListener} which monitors the
 * connection to the agent in case connection is lost.
 * 
 * @author goland
 */
public class RunnerEngineManager {
	private static Logger log = Logger.getLogger(RunnerEngineManager.class.getName());
	
	private static RunnerEngine agent;
	private static ConnectionListener connectionListener;
	final public static String LOCAL_AGNET = "local";
	
	/**
	 * Returns an implementation of {@link RunnerEngine}.
	 * {@link #initRunnerEngine(String))} must be activated before getting
	 * runner engine implementation.
	 */
	public static RunnerEngine getRunnerEngine(){
		if(agent == null){
			throw new IllegalStateException("Agent was not initialized");
		}
		return agent;
	}
	/**
	 * Initializes runner's {@link RunnerEngine}.<br>
	 * This method should be invoked before the first time someone calls the
	 * {@link #getRunnerEngine()}.<br>
	 * If the runner is already connected to an engine, the connection to the
	 * engine is closed and a new connection is opened.<br>
	 * 
	 * @param agentUrl -
	 *            url of remote agent should be in the format host:port.<br>
	 *            If the value of this parameter is {@value #LOCAL_AGNET}, the
	 *            runner will work with an embedded engine in it ({@link RunnerEngineImpl})
	 */
	public static void initRunnerEngine(String agentUrl) throws Exception {
		if (agent != null){
			agent.close();
			log.fine("Agent closed ");
		}
		agent = new ApplicationRunnerEngineImpl();
		if (!StringUtils.isEmpty(agentUrl) && !LOCAL_AGNET.equals(agentUrl.trim())){
			agent =  new RemoteAgentClient(agentUrl);
			JSystemAgentClientsPool.addClient(agentUrl, false);
		}
		resetConnectionListener();
		updateUI();
		AgentList.addToAgentList(agentUrl);
	}
		
	private static void resetConnectionListener() throws Exception{
		if (connectionListener == null) {
			connectionListener = new ConnectionListener();
		}
		connectionListener.stopMonitor();
		log.fine("Monitor stopped ");
		connectionListener.setEngine(agent);
		log.fine("Engine set ");
		connectionListener.connect();
		log.fine("Connected ");
	}
	
	
	private static void updateUI() throws Exception{
		/**
		 * Set GUI to reflect agent state.
		 */
		RunnerEngineExecutionState executionState = agent.getEngineExecutionState();
		setExecutionView(executionState);
		
		if (!(RunnerEngineExecutionState.idle.equals(executionState) || 
			  RunnerEngineExecutionState.idleError.equals(executionState))) {
			ExecutionWorker runWorker = new ExecutionWorker(TestRunner.treeView);
			agent.addListener(runWorker);
			String engineProject = agent.getCurrentProjectName();
			boolean isAligned = validateProject(engineProject);
			if (isAligned) {
				String currentScenario = agent.getActiveScenario();
				syncActiveScenario(currentScenario);
			}
			((RunnerListenersManager) RunnerListenersManager.getInstance()).initReporters();
			Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
			scenario.setStatusNotRunning();
		}
	}


	private static boolean validateProject(String projectName){
		File projectFolder = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER)).getParentFile();
		if (!projectFolder.getName().equals(projectName) && !projectFolder.getPath().equals(projectName)){
			ErrorPanel.showErrorDialog("Projects Synchronization"
					, "Engine project and senario studio projects are not aligned. \nPlease note that the JRunner UI will not reflect execution events correctly",
					ErrorLevel.Warning);
			return false;
		}
		return true;
	}

	private static void syncActiveScenario(String currentScenario){
		String studioScenario = ScenariosManager.getInstance().getCurrentScenario().getName();
		studioScenario = ScenarioHelpers.removeScenarioHeader(studioScenario);
		currentScenario = ScenarioHelpers.removeScenarioHeader(currentScenario);
		if (!currentScenario.equals(studioScenario)){
			TestRunner.treeView.getTableController().loadScenario(currentScenario, false);
		}
	}

	private static void setExecutionView(RunnerEngineExecutionState executionState){
		if (TestRunner.treeView == null){
			return;
		}
		if (RunnerEngineExecutionState.idle.equals(executionState) || RunnerEngineExecutionState.idleError.equals(executionState)){
			TestRunner.treeView.configureView(TestTreeView.VIEW_IDLE);
		}else
		if (RunnerEngineExecutionState.running.equals(executionState) || RunnerEngineExecutionState.stopping.equals(executionState)){
			TestRunner.treeView.configureView(TestTreeView.VIEW_RUNNING);
		}else
		if (RunnerEngineExecutionState.paused.equals(executionState)){
			TestRunner.treeView.configureView(TestTreeView.VIEW_PAUSED);
		}else {
			throw new IllegalStateException("Illigal engine state");
		}
	}

}
