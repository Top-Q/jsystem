/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.io.Externalizable;
import java.io.File;
import java.util.Properties;

import jsystem.extensions.report.html.LevelHtmlTestReporter;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 *  Manages runner engine state persistence.<br>
 *  This class was written in order to implement continuous execution after engine
 *  restart.<br>
 *  Currently the state of the engine includes the following:<br>
 *  1. Last test that was activated<br>
 *  2. Last internal scenario that was activated<br>
 *  3. Serialization of HTML reporter.<br>
 *  <br>
 *  In addition infrastructure was implemented to add more reporters to the state.<br>
 *  <br>
 *  Tests that cause the machine on which they are running to restart should
 *  activate the method {@link RunnerListenersManager#saveState(Test).<br>
 *  When method is activated, a message is sent to the engine to save it's reporters state.
 *  state is save by {@link RunnerListenersManager#saveState(junit.framework.Test)}.
 *  In addition active test index is saved by {@link ActiveTestStateListener} listener 
 *  (active test index is saved any way, all the time).<br>
 *  Internal scenario id is saved by {@link RunnerEngineImpl#run(String scenario id).<br>
 *  <br>
 *  After restart, when the agent starts running the following happens:<br>
 *  1. The {@link RunnerListenersManager} is started normally, when the {@link LevelHtmlTestReporter is initiated, 
 *     it identifies that the {@link #LOAD_REPORTERS_STATE} is turned on and normal initiation is skipped.<br>
 *  2. {@link RunnerListenersManager} checks whether the {@link #LOAD_REPORTERS_STATE} is true. if so
 *     it signals all the reporters that implement the {@link Externalizable} interface
 *     to de-serializes themselves. (currently there is one reporter that implements that interface: 
 *     {@link LevelHtmlTestReporter}.<br>
 *  3. The results of steps 1-2 is that after restart the HTML report shows exactly what it showed before restart.<br>
 *  4. When the agent starts, is signals the engine to check whether to run on start.<br> 
 *  5. if {@link RunnerStatePersistencyManager#getRunOnStart()}returns true, 
 *     the engine takes {@link RunnerStatePersistencyManager#getActiveTestIndex()} and 
 *     {@link RunnerStatePersistencyManager#getInternalScenarioUUID()}
 *     and continues scenario run from active test index.<br>
 *  <br>
 *  Please note that for agent to start to run automatically after restart, is has to be 
 *  registered as an operating system service.
 *  
 * @see RunnerEngineImpl#checkAndRunOnStart()
 * @see RunnerAgentMain#initAgent()
 * @see ListenerstManager#saveState(junit.framework.Test)
 * @see RunnerListenersManager#saveState(junit.framework.Test)
 *  
 * @author goland
 */
public class RunnerStatePersistencyManager {
	public static final File STATE_FILE = new File("runnerState.properties");
	private final String RUN_ON_START = "runOnStart";
	private final String LOAD_REPORTERS_STATE = "loadReportersState";
	private final String TEST_INDEX = "testIndex";
	private final String INTERNAL_SCENARIO_ID = "internalScenarioUUID";
	private final String SELECTED_TESTS = "selectedTests";
	private static RunnerStatePersistencyManager manager; 
	
	public static synchronized RunnerStatePersistencyManager getInstance() {
			if (manager == null){
				manager = new RunnerStatePersistencyManager();
			}
			return manager;
	}

	private RunnerStatePersistencyManager(){
		if (!STATE_FILE.exists()){
			//if state file doesn't exist, create it
			Properties p = new Properties();
			saveProps(p);
		}
	}
	
	public void setRunOnStart(boolean runOnStart){
		Properties p = loadProps();
		p.setProperty(RUN_ON_START,Boolean.toString(runOnStart));
		saveProps(p);
	}
	
	public boolean getRunOnStart(){
		Properties p = loadProps();
		String val = p.getProperty(RUN_ON_START);
		if (StringUtils.isEmpty(val)){
			return false;
		}
		return Boolean.parseBoolean(val);
	}

	public void setLoadReporters(boolean runOnStart) {
		Properties p = loadProps();
		p.setProperty(LOAD_REPORTERS_STATE,Boolean.toString(runOnStart));
		saveProps(p);
	}
	
	public boolean getLoadReporters() {
		Properties p = loadProps();
		String val = p.getProperty(LOAD_REPORTERS_STATE);
		if (StringUtils.isEmpty(val)){
			return false;
		}
		return Boolean.parseBoolean(val);
	}

	public void setActiveTestIndex(int index) {
		Properties p = loadProps();
		p.setProperty(TEST_INDEX, Integer.toString(index));
		saveProps(p);
		
	}

	public int getActiveTestIndex() {
		Properties p = loadProps();
		String val = p.getProperty(TEST_INDEX);
		if (StringUtils.isEmpty(val)){
			return -1;
		}
		return Integer.parseInt(val);
	}

	public void setEnabledTests(int[] selectedTests) {
		Properties p = loadProps();
		if (selectedTests == null){
			p.remove(SELECTED_TESTS);
		}else {
			String[] res = new String[selectedTests.length];
			for (int i = 0; i < selectedTests.length;i++){
				res[i] = Integer.toString(selectedTests[i]);
			}

			String selectedTestsString = StringUtils.objectArrayToString(",",(Object[])res);
			p.setProperty(SELECTED_TESTS, selectedTestsString);
		}
		saveProps(p);
	}
	
	public int[] getEnabledTests(boolean reset) {
		Properties p = loadProps();
		String val = p.getProperty(SELECTED_TESTS);
		if (reset){
			setEnabledTests(null);
		}
		if (val == null){
			return null;
		}
		String[] resTmp =  StringUtils.split(val,",");
		int[] res = new int[resTmp.length];
		for (int i = 0; i < resTmp.length;i++){
			res[i] = Integer.parseInt(resTmp[i]);
		}
		return res;
	}
	
	public void setInternalScenarioUUID(String uuid) {
		Properties p = loadProps();
		p.setProperty(INTERNAL_SCENARIO_ID, uuid);
		saveProps(p);
		
	}

	public String getInternalScenarioUUID()  {
		Properties p = loadProps();
		String val = p.getProperty(INTERNAL_SCENARIO_ID);
		return val;
	}
	
	private Properties loadProps(){
		try {
			Properties p = FileUtils.loadPropertiesFromFile(STATE_FILE.getPath());
			return p;
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private void saveProps(Properties p){
		try {
			FileUtils.savePropertiesToFile(p,STATE_FILE.getPath());
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
