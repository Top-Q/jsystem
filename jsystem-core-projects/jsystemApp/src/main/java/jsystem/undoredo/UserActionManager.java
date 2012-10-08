/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.undoredo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import jsystem.framework.TestRunnerFrame;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.treeui.actionItems.ScenarioRedoAction;
import jsystem.treeui.actionItems.ScenarioUndoAction;
import jsystem.utils.SwingUtils;

public class UserActionManager implements ScenarioListener {
	
	private static List<UserAction> userActions;
	private static volatile int currentLocation;
	private static volatile boolean inRedoUndoAction = false;
	static {
		reset();
		AddUndoRedoListener();
	}
	public static void addAction(UserAction action) {
		if (inRedoUndoAction){
			return;
		}
		synchronized(UserActionManager.class) {
			userActions.add(currentLocation,action);
			currentLocation++;
			userActions = userActions.subList(0, currentLocation);
			refresh();
		}
	}
	
	public static void onUndo() throws Exception {
		if (inRedoUndoAction){
			return;
		}
		synchronized(UserActionManager.class){
			final UserAction action = userActions.get(currentLocation-1);
			SwingUtils.setBusyCursor(TestRunnerFrame.guiMainFrame, true);
			SwingWorker<String, Object> worker = new SwingWorker<String, Object>(){
		        public String doInBackground() {
		        	try {
		        		inRedoUndoAction = true;
		        		action.undo();
		        		SwingUtils.setBusyCursor(TestRunnerFrame.guiMainFrame, false);
		        	}catch (Exception e){
		        		throw new RuntimeException("Failed in redo operation",e);
		        	}finally {
		        		inRedoUndoAction = false;
		        	}
		        	return "";
		        } 
			};
			worker.execute();
			currentLocation--;
			refresh();
		}
	}

	public static void onRedo() throws Exception {
		if (inRedoUndoAction){
			return;
		}
		synchronized (UserActionManager.class) {		
			currentLocation++;
			final UserAction action = userActions.get(currentLocation-1);
			SwingUtils.setBusyCursor(TestRunnerFrame.guiMainFrame, true);
			SwingWorker<String, Object> worker = new SwingWorker<String, Object>(){
		        public String doInBackground() {
		        	try {
		        		inRedoUndoAction = true;
		        		action.redo();
		        		SwingUtils.setBusyCursor(TestRunnerFrame.guiMainFrame, false);
		        	}catch (Exception e){
		        		throw new RuntimeException("Failed in redo operation",e);
		        	}finally {
		        		inRedoUndoAction = false;
		        	}		        	
		        	return "";
		        } 
			};
			worker.execute();
			refresh();
		}
	}

	public synchronized static void reset(){
		userActions = 
			(List<UserAction>)Collections.synchronizedList(new ArrayList<UserAction>());
		currentLocation = 0;
	}

	
	public synchronized static boolean isUndoEnabled(){
		return userActions.size() > 0 && currentLocation >= 1;
	}

	
	public synchronized static boolean isRedoEnabled(){
		return currentLocation < userActions.size();
	}

	public static void refresh() {
		ScenarioRedoAction.getInstance().setEnabled(isRedoEnabled());
		ScenarioUndoAction.getInstance().setEnabled(isUndoEnabled());
	}

	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
	}

	@Override
	public void scenarioDirectoryChanged(File directory) {
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {

	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,Parameter[] newValues) {
		addAction(new ParameterChangeAction(testIIUUD,oldValues,newValues));		
	}

	private static void AddUndoRedoListener() {
		RunnerListenersManager.getInstance().addListener(new UserActionManager());
	}

}
