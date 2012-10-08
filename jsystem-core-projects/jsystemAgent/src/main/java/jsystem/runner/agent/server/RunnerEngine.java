/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.net.URL;
import java.util.Properties;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.system.SystemObject;

/**
 * Interface of Jsystem tests runner.<br>
 * 
 * Jsystem runner includes two modules:<br> 
 * 1. execution engine - engine that executes jsystem scenarios and manages execution events. <br> 
 * 2. Scenario studio - studio for creating and manipulating jsystem scenarios.<br>
 * <br>
 * In JSystem 5.0 a major refactor that separates between the scenario studio and the execution engine was done.<br>
 * The trigger of the refactor was to enable remote execution of JSystem engine.<br>
 * <br>
 * This interface currently has two implementations: {@link RunnerEngineImpl} and <br>
 * {@link RemoteAgentClient} int jsystemApp project.
 *   
 * @author goland
 */
public interface RunnerEngine {
	
	public enum ConnectionState {
		na,
		connected,
		disconnected,
		notificationLost;
	}
	
	final public static String LOCAL_ENGINE = "local";
	/**
	 * Sets <code>testsPath</code> to be the active project on which the engine works.<br>
	 * @param testsPath - full path to tests compilation output.<br>
	 */
	public void changeProject(String testsPath) throws Exception;
	
	/**
	 * Sets <code>sutName</code> to be active SUT file.<br>
	 * @param sutName -  sut file path relative to tests classes folder (i.e. scenarios/default.xml)
	 */
	public void changeSut(String sutName) throws Exception;
	
	/**
	 *Sets <code>scenario</code> to be the active scenario.<br> 
	 */
	public void setActiveScenario(Scenario scenario) throws Exception;
	
	/**
	 * Turns on/off engine's repeat flag.<br>
	 * Can be activated while engine is running a scenario.
	 */
	public void enableRepeat(boolean enable) throws Exception;
	/**
	 * Sets repeat number.<br>
	 * Can be activated while engine is running a scenario.
	 */
	public void setRepeat(int numOfRepeats)throws Exception;
	/**
	 * Starts execution of active scenario on active project with selected sut.
	 * @see #setActiveScenario(Scenario)
	 * @see #changeProject(String)
	 * @see #changeSut(String)
	 */
	public void run()throws Exception;

	/**
	 * Executes test/scenario with UUID <code>uuid</code> in the context of the current scenario.<br>
	 * In case that <code>uuid</code> points to a scenario, the scenario is executed in run.mode 1.
     */
	public void run(String uuid) throws Exception;
	
	/**
	 * Immediate stop of scenario execution.<br>
	 * Execution is stopped brutally, the jvm which executes the<br>
	 * is disposed immediately, tear down process is not executed and<br>
	 * the close method of the SystemObjects are not called.     
	 */
	public void stop()throws Exception;
	
	/**
	 * Signals the tests jvm to stop execution.<br>
	 * In the next call to one of the report methods of the {@link ListenerstManager}<br>
	 * the system identifies that the flag was raised and it starts a graceful stop process.<br>
	 * It graceful stop, tear down process is performed and all system objects are disposed properly.
	 */
	public void gracefulStop() throws Exception;
	
	/**
	 * Signals the tests jvm to pause execution.<br>
	 * In the next call to one of the report methods of the {@link ListenerstManager}<br>
	 * the system identifies that the flag was raised and it pauses execution.<br>
	 * Pause process involves calling the {@link SystemObject#pause()} method <br>
	 * of all the system objects in the system .
	 */	
	public void pause()throws Exception;

	/**
	 * Signals the tests jvm to resume execution.<br>
	 * If execution is not paused resume signal is ignored.<br>
	 * Resume process involves calling the {@link SystemObject#resume()} method <br>
	 * of all the system objects in the system .
	 */	
	public void resume()throws Exception;
	
	/**
	 * Signals all reporters in the system to reset themselves.
	 */
	public void initReporters();
	
	/**
	 * Returns the {@link URL} of the index.html of the HTML reporter.
	 */
	public URL getLogUrl() throws Exception;
	
	/**
	 * Resets agent configuration.<br>
	 * Reset includes:<br>
	 * 1. Resetting class loader<br>
	 * 2. Reloading jsystem.properties<br>
	 * 3. Resetting fixtures model<br>
	 * 4. Reloading SUT file.
	 * 5. Reloading scenario
	 */
	public void refresh() throws Exception;
	
	/**
	 * Adds a listener to tests/engine events.
	 * Please read {@linkplain http://docs.google.com/Doc?id=dcsht6g7_152ckgnpzf3 } to understand jsystem events.
	 */
	public void addListener(Object eventListsner);

	/**
	 */
	public void removeListener(Object eventListsner);
	
	/**
	 * Performs agent termination process. 
	 */
	public void close();
	
	/**
	 * Performs agent initialization process.
	 */
	public void init() throws Exception;	
	
	/**
	 * Returns the current execution state of the engine.
	 */
	public RunnerEngineExecutionState getEngineExecutionState() throws Exception;
	
	/**
	 * Returns the active project name
	 */
	public String getCurrentProjectName() throws Exception;
	
	/**
	 * Returns the name of the active scenario name.
	 */
	public String getActiveScenario() throws Exception;
	
	/**
	 * Returns engine's unique id.
	 * Id is in the format of host:port
	 */
	public String getId();
	
	/**
	 * Returns the state of the connection to the engine
	 */
	public ConnectionState getConnectionState();
	
	/**
	 * Returns engine version.
	 */
	public String getEngineVersion() throws Exception;
	
	/**
	 * Returns engine run properties.
	 */
	public Properties getRunProperties() throws Exception;
}
