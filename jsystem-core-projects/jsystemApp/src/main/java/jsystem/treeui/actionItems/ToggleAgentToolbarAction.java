/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;

/**
 * Toggles agent connectivity tool bar visibility.
 * @author goland
 */
public class ToggleAgentToolbarAction extends IgnisAction {
	private static final long serialVersionUID = 1L;
	private static ToggleAgentToolbarAction action;
	private ToggleAgentToolbarAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getAgentToolbar());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getAgentToolbar());
		putValue(Action.ACTION_COMMAND_KEY, "toggle main toolbar");
	}
	public static ToggleAgentToolbarAction getInstance(){
		if (action == null){
			action =  new ToggleAgentToolbarAction();
		}
		return action;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.toggleAgentToolBarVisability();
	}
}
