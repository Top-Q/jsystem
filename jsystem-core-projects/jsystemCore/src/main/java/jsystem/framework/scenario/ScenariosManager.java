/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.SystemTest;

/**
 * This class is used to manage all the scenarios. It access using static
 * singleton method called getInstance. You can use it to getScenario with the
 * scenario name as a parameter or getCurrentScenario to get the current active
 * scenario. Use the static method init to drop the current manager. When
 * getInstance will be called a new scenarios manager will be created.
 * 
 * @author guy.arieli
 * 
 */
public class ScenariosManager {

	public static final int RUN_MODE_CONTINUE = 0;

	public static final int RUN_MODE_FREEZE = 1;

	public static final int RUN_MODE_STOP = 2;

	private static Logger log = Logger.getLogger(ScenariosManager.class.getName());

	private static ScenariosManager manager = null;
	
	private Exception lastException;
	
	private static volatile boolean isDirty;
	
	private static volatile boolean silent;
	
	private Scenario currentScenario;

	private File scenariosDirectoryFiles;

	private int runMode = RUN_MODE_CONTINUE;
	
	public static ScenariosManager getInstance() {
		if (manager == null) {
			ScenarioHelpers.resetCache();
			manager = new ScenariosManager();
		}
		return manager;
	}

	/**
	 * drop the current scenarios manager, When getInstance will be called a new
	 * scenarios manager will be created.
	 */
	public static void init() {
		manager = null;
	}

	public static void setDirty(){
		isDirty = true;
		if (!silent){
			ListenerstManager.getInstance().scenarioDirtyStateChanged(ScenariosManager.getInstance().currentScenario, isDirty);
		}
	}

	
	public static void resetDirty(){
		isDirty = false;
		if (!silent){
			ListenerstManager.getInstance().scenarioDirtyStateChanged(ScenariosManager.getInstance().currentScenario, isDirty);
		}
	}

	public static void setDirtyStateEventsSilent(boolean silent){
		ScenariosManager.silent = silent;
	}

	public static boolean isDirty(){
		return isDirty;
	}

	/***************************************************************************
	 * returns back a Scenario object The Scenario is set to be saved at the
	 * test classes dir. Scenario name convention is "<scenariosDirectory>/.../<scenarionName>"
	 * 
	 * @param name
	 * @return Scenario object
	 * @throws Exception
	 */
	public Scenario getScenario(String name) throws Exception {
		setDirtyStateEventsSilent(true);
		try {
			Scenario scenario = null;
			synchronized (this) {
				if (currentScenario == null) {
					setScenariosDirectoryFiles(new File(JSystemProperties.getCurrentTestsPath()));
				}
				
				if (StringUtils.isEmpty(name)){
					name = JSystemProperties.getInstance().getPreference(FrameworkOptions.CURRENT_SCENARIO);
				}
		
				if (StringUtils.isEmpty(name)) {
					name = "scenarios" + File.separator + "default";
				}
				scenario = new Scenario(scenariosDirectoryFiles, name);
			}
			try {
				ScenarioHelpers.cleanScenarioPropertiesFromRedundantEntries(scenario);
			}catch (Exception e){
				throw new RuntimeException(e);
			}
			return scenario;
		}finally {
			setDirtyStateEventsSilent(false);
		}
	}

	/**
	 * Collect a list of all the scenarios that a given test is found in. The
	 * list is of string in the format: <scenarioName>;<indexInTheScenario>
	 * 
	 * @param test
	 *            the test to look for
	 * @return the list of all that were found
	 * @throws Exception
	 */
	public ArrayList<String> collectTestsOfType(SystemTest test) throws Exception {
		/*
		 * Init the scenario manager
		 */
		getCurrentScenario();
		ArrayList<String> collectTo = new ArrayList<String>();
		/*
		 * collect all the scenarios
		 */
		ScenarioCollector sc = new ScenarioCollector();
		Vector<String> scenarios = sc.collectTestsVector();
		/*
		 * Run on all the scenario and on all the tests in every scenario If the
		 * class name and the method name is the save as of the input test add
		 * to the collectTo
		 */
		for (int i = 0; i < scenarios.size(); i++) {
			String scenarioName = (String) scenarios.elementAt(i);
			Scenario s = new Scenario(scenariosDirectoryFiles, scenarioName, null);
			Vector<JTest> rootTests = s.getRootTests();
			for (int j = 0; j < rootTests.size(); j++) {
				RunnerTest t = (RunnerTest) rootTests.elementAt(j);
				if (t.getClassName().equals(test.getClass().getName()) && t.getMethodName().equals(test.getName())) {
					collectTo.add(scenarioName + ";" + j);
				}
			}
		}
		return collectTo;
	}

	/**
	 * @return Returns the currentScenario.
	 */
	public synchronized Scenario getCurrentScenario() {
		if (currentScenario == null) {
			try {
				currentScenario = getScenario(null);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unable to init scenario", e);
				lastException = e;
			}
		}
		return currentScenario;
	}
	
	/**
	 * Try to create the current scenario<br>
	 * if it fails, try loading the default scenario<br>
	 * if this also fails, backup default scenario and create a new default scenario
	 * 
	 * @return	the Message String if there were errors, null otherwise
	 */
	public synchronized String checkScenario(){
		if (currentScenario == null) {
			try {
				currentScenario = getScenario(null);
				return null;
			} catch (Exception e) { // current scenario file is damaged
				log.log(Level.SEVERE, "Unable to init scenario", e);
				lastException = e;
				// remove current Scenario entry from jsystem properties file
				JSystemProperties.getInstance().removePreference(FrameworkOptions.CURRENT_SCENARIO);
				// try default
				String name = "scenarios" + File.separator + "default";
				try{
					currentScenario = new Scenario(scenariosDirectoryFiles, name);
					return "Problem loading Scenario - Default scenario was loaded instead\n\n"
					     + StringUtils.getStackTrace(lastException);
				}catch (Exception e1) { // default scenario file is damaged 
					lastException = e1;
					try {
						String backup = ScenarioHelpers.backupDefaultScenarioFile();
						currentScenario = new Scenario(scenariosDirectoryFiles, name);
						return "Problem loading Default Scenario!\n"
						      +"Default scenario was copied to " + backup + "\n"
						      +"new Default scenario was loaded\n\n"
						      +StringUtils.getStackTrace(lastException);
					} catch (Exception e2) {
						log.log(Level.SEVERE, "Unable to init scenario", e2);
						lastException = e2;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param currentScenario
	 *            The currentScenario to set.
	 */
	public void setCurrentScenario(Scenario currentScenario) {
		Scenario original = this.currentScenario;
		this.currentScenario = currentScenario;
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, currentScenario.getName());
		if(original == null || !original.getName().equals(currentScenario.getName())){
			ListenerstManager.getInstance().scenarioChanged(currentScenario, ScenarioChangeType.CURRENT);
		}
		original = null;
	}
	
	
    public boolean isScenarioExists(String name) throws Exception{
    	String windowsStyle = FileUtils.convertToWindowsPath(name);
    	String unixStyle = FileUtils.replaceSeparator(name);
        Vector<String> v = getAvailableLists();
        for (int m = 0; m < v.size(); m++) {
        	
            if (v.get(m).equals(windowsStyle) || v.get(m).equals(unixStyle))
                return true;
        }
        return false;
    }
    
	/**
	 * Using the ScenarioCollector in order to bring all the exists scenarios.
	 * 
	 * @return Vector of all scenarios exists
	 */
	public Vector<String> getAvailableLists() throws Exception  {
		ScenarioCollector sc;
		sc = new ScenarioCollector();
		return sc.collectTestsVector();
	}

	public File getScenariosDirectoryFiles() {
		return scenariosDirectoryFiles;
	}

	public void setScenariosDirectoryFiles(File scenariosDirectoryFiles) {
		this.scenariosDirectoryFiles = scenariosDirectoryFiles;
		if (!scenariosDirectoryFiles.exists()) {
			scenariosDirectoryFiles.mkdirs();
		}
		ListenerstManager.getInstance().scenarioDirectoryChanged(scenariosDirectoryFiles);
		currentScenario = null;
	}

	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}

	public int getRunMode() {
		return runMode;
	}

	/**
	 * for jsystemobject
	 * 
	 * @return Vector
	 */
	public Vector<String> getScenariosNames() throws Exception  {
		Vector<String> names = new Vector<String>();
		Vector<String> exsistsScenarios = getAvailableLists();

		for (int i = 0; i < exsistsScenarios.size(); i++) {
			names.add((String) exsistsScenarios.get(i));

		}

		return names;
	}
	
	public Exception getLastException() {
		return lastException;
	}
}
