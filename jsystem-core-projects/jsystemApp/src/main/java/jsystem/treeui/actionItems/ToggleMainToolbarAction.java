/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;

/**
 * Toggles main runner Tool bar visibility.
 * @author goland
 *
 */
public class ToggleMainToolbarAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ToggleMainToolbarAction action;
	private ToggleMainToolbarAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getMainToolbar());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getMainToolbar());
		putValue(Action.ACTION_COMMAND_KEY, "toggle main toolbar");
	}
	
	public static ToggleMainToolbarAction getInstance(){
		if (action == null){
			action =  new ToggleMainToolbarAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.toggleMainToolBarVisability();
	}

}
