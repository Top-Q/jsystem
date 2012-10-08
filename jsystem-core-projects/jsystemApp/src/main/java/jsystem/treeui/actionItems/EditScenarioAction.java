/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class EditScenarioAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static EditScenarioAction action;
	


	private EditScenarioAction(){
		super();
		putValue(Action.NAME, "Edit");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getEditScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_EDIT_SCENARIO));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_EDIT_SCENARIO));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "edit-scenario");
	}
	
	public static EditScenarioAction getInstance(){
		if (action == null){
			action =  new EditScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().editScenario();
	}

}
