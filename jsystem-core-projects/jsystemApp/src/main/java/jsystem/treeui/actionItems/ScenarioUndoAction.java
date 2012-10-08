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

public class ScenarioUndoAction extends IgnisAction {
	private static final long serialVersionUID = 1L;
	
	private static ScenarioUndoAction action;
	
	private ScenarioUndoAction(){
		super();
		putValue(Action.NAME, "Scenario Undo");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getScenarioUndoButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_UNDO_EDIT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_UNDO_EDIT));
		putValue(Action.ACTION_COMMAND_KEY, "scenario-undo");
	}
	
	public static ScenarioUndoAction getInstance(){
		if (action == null){
			action =  new ScenarioUndoAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			UserActionManager.onUndo();
		}catch (Exception e1){
			ErrorPanel.showErrorDialog("Undo error", e1, ErrorLevel.Error);
		}
	}
}
