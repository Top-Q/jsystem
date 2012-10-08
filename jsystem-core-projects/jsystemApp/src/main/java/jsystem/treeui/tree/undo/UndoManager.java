/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree.undo;

import java.io.File;
import java.util.HashMap;

import org.jfree.util.Log;

import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.treeui.TestRunner;
import jsystem.utils.FileUtils;

public class UndoManager implements ScenarioListener {
	
	private static UndoManager manager = null;
	public static UndoManager getInstance() {
		if (manager == null) {
			manager = new UndoManager();
		}
		return manager;
	}
	
	
	private File scenariosUndoDir;
	private HashMap<String, ScenarioUndoRedo> scenarios = new HashMap<String, ScenarioUndoRedo>();
	private boolean blockAllEvents = false;
	
	private UndoManager() {
		//RunnerListenersManager.getInstance().addListener(this);
	}
	
	public void init() throws Exception {
		File classDir = new File(JSystemProperties.getCurrentTestsPath());
		scenariosUndoDir = new File(classDir.getParent() + File.separator
				+ ".temp", "scenario.undo");
		if (scenariosUndoDir.exists()) {
			FileUtils.deltree(scenariosUndoDir);
		}
		scenariosUndoDir.mkdirs();
		// do to scenarioChange miss behaver fire the fist event
		Scenario current = ScenariosManager.getInstance().getCurrentScenario();
		ScenarioUndoRedo scenarioUndoRedo = new ScenarioUndoRedo(current, scenariosUndoDir);
		scenarios.put(current.getName(), scenarioUndoRedo);
	}
	public boolean isUndoEnabled(String scenarioName) {
		ScenarioUndoRedo scenarioUndoRedo = scenarios.get(scenarioName);
		if (scenarioUndoRedo != null) {
			return scenarioUndoRedo.canUndo();
		}
		return false;
	}
	public boolean isRedoEnabled(String scenarioName) {
		ScenarioUndoRedo scenarioUndoRedo = scenarios.get(scenarioName);
		if (scenarioUndoRedo != null) {
			return scenarioUndoRedo.canRedo();
		}
		return false;
	}
	public void undo(Scenario scenario) throws Exception {
		if(blockAllEvents){
			return;
		}
		blockAllEvents = true;
		try {
			ScenarioUndoRedo scenarioUndoRedo = scenarios.get(scenario.getName());
			if (scenarioUndoRedo == null) {
				Log.error("No scenario undo\redo component!");
				return;
			}
			scenarioUndoRedo.undo(scenario);
		} finally {
			blockAllEvents = false;
		}
	}
	public void redo(Scenario scenario) throws Exception {
		if(blockAllEvents){
			return;
		}
		blockAllEvents = true;
		try {
			ScenarioUndoRedo scenarioUndoRedo = scenarios.get(scenario.getName());
			if (scenarioUndoRedo == null) {
				Log.error("No scenario undo\redo component!");
				return;
			}
			scenarioUndoRedo.redo(scenario);
		} finally {
			blockAllEvents = false;
		}
	}
	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		if(blockAllEvents){
			return;
		}
		blockAllEvents = true;
		try {
			if(changeType.equals(ScenarioChangeType.MODIFY)){
				ScenarioUndoRedo scenarioUndoRedo = scenarios.get(current.getName());
				if (scenarioUndoRedo == null) {
					scenarioUndoRedo = new ScenarioUndoRedo(current, scenariosUndoDir);
					scenarios.put(current.getName(), scenarioUndoRedo);
				} else {
					scenarioUndoRedo.scenarioChanged(current);
				}
			} else if(changeType.equals(ScenarioChangeType.DELETE)){
				scenarios.remove(current.getName());
			} else if(changeType.equals(ScenarioChangeType.NEW)){
				ScenarioUndoRedo scenarioUndoRedo = new ScenarioUndoRedo(current, scenariosUndoDir);
				scenarios.put(current.getName(), scenarioUndoRedo);
			} else if(changeType.equals(ScenarioChangeType.CURRENT)){
				ScenarioUndoRedo scenarioUndoRedo = scenarios.get(current.getName());
				if(scenarioUndoRedo == null){
					scenarioUndoRedo = new ScenarioUndoRedo(current, scenariosUndoDir);
					scenarios.put(current.getName(), scenarioUndoRedo);
				}
			}
			TestRunner.treeView.getTableController().updateEnabledAndDisabledActions(null);
		} finally {
			blockAllEvents = false;
		}
	}
	@Override
	public void scenarioDirectoryChanged(File directory) {
		scenarios = new HashMap<String, ScenarioUndoRedo>();
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {
		// TODO Auto-generated method stub
		
	}
}