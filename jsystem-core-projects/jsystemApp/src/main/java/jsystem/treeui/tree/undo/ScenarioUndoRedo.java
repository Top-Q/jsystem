/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree.undo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.treeui.TestRunner;
import jsystem.utils.FileUtils;

public class ScenarioUndoRedo {


	private File scenariosUndoRedoDirectory;

	/**
	 * 
	 * A list of all the scenarios redo undo backups
	 */

	private List<String> scenarios = new ArrayList<String>();

	/**
	 * 
	 * Index to the current scenario in the scenarios list
	 */

	int currentScenarioIndex;

	public ScenarioUndoRedo(Scenario scenario, File scenariosUndoRedoDirectory) {

		this.scenariosUndoRedoDirectory = scenariosUndoRedoDirectory;

		currentScenarioIndex = -1;
		scenarioChanged(scenario);

	}

	/**
	 * 
	 * Check if Redo is supported
	 * 
	 * @return true if supported
	 */

	public boolean canRedo() {

		return scenarios.size() > (currentScenarioIndex + 1);

	}

	/**
	 * 
	 * Check if undo is supported
	 * 
	 * @return true if supported
	 */

	public boolean canUndo() {

		return currentScenarioIndex > 0;

	}

	/**
	 * 
	 * Redo
	 */

	public synchronized void redo(Scenario scenario) throws Exception {
		if (canRedo()) {
			currentScenarioIndex++;
			String scenarioName = scenarios.get(currentScenarioIndex);
			copyFromUndoDir(scenarioName,scenarioName.replace(".xml.", ".properties."), scenario);
			scenario.load();
			TestRunner.treeView.tableController.loadScenario(scenario.getName(), false);
		}
	}
	/**
	 * 
	 * Undo
	 */

	public synchronized void undo(Scenario scenario) throws Exception {

		if (canUndo()) {
			currentScenarioIndex--;
			String scenarioName = scenarios.get(currentScenarioIndex);
			copyFromUndoDir(scenarioName,scenarioName.replace(".xml.", ".properties."), scenario);
			scenario.load();
			TestRunner.treeView.tableController.loadScenario(scenario.getName(), false);
		}

	}

	public void scenarioChanged(Scenario scenario) {
		// not the last
		if(canRedo()){
			scenarios = scenarios.subList(0, currentScenarioIndex + 1);
		}
		currentScenarioIndex++;
		String backupName = getBackupFileName(scenario);
		String propertiesBackup = getPropertiesBackupFileName(scenario);
		scenarios.add(backupName);
		try {
			saveScenarioToUndoDir(scenario, backupName, propertiesBackup);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	private String getBackupFileName(Scenario scenario){
		return 	scenario.getName().replace('\\', '.').replace('/', '.') + ".xml." + currentScenarioIndex;
	}
	private String getPropertiesBackupFileName(Scenario scenario){
		return 	scenario.getName().replace('\\', '.').replace('/', '.') + ".properties." + currentScenarioIndex;
	}


	private void saveScenarioToUndoDir(Scenario scenario, String newName, String propertiesBackupName) throws Exception {
		FileUtils.copyFile(scenario.getScenarioFile(), new File(scenariosUndoRedoDirectory, newName));
		File propertiesFile = new File(ScenarioHelpers.getScenarioPropertiesFile(scenario.getName()));
		if(propertiesFile.exists()){
			FileUtils.copyFile(propertiesFile, new File(scenariosUndoRedoDirectory, propertiesBackupName));
		}
	}

	private void copyFromUndoDir(String name,String propertiesBackup, Scenario scenario) throws Exception{
		FileUtils.copyFile(new File(scenariosUndoRedoDirectory, name), scenario.getScenarioFile());
		File propertiesFile = new File(ScenarioHelpers.getScenarioPropertiesFile(scenario.getName()));
		File backPropertiesFile = new File(scenariosUndoRedoDirectory, propertiesBackup);
		if(backPropertiesFile.exists()){
			FileUtils.copyFile(backPropertiesFile, propertiesFile);
		}
	}

}