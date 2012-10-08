/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.undoredo.UserActionManager;

public class ScenarioRedoAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ScenarioRedoAction action;
	
	private ScenarioRedoAction(){
		super();
		putValue(Action.NAME, "Scenario Redo");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getScenarioRedoButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REDO_EDIT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REDO_EDIT));
		putValue(Action.ACTION_COMMAND_KEY, "scenario-redo");
	}
	
	public static ScenarioRedoAction getInstance(){
		if (action == null){
			action =  new ScenarioRedoAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			UserActionManager.onRedo();
		}catch (Exception e1){
			ErrorPanel.showErrorDialog("Redo error", e1, ErrorLevel.Error);
		}
	}
}
