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
public class ToggleFlowControlToolbarAction extends IgnisAction {
	private static final long serialVersionUID = 1L;
	private static ToggleFlowControlToolbarAction action;
	private ToggleFlowControlToolbarAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getFlowControlToolbar());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getFlowControlToolbar());
		putValue(Action.ACTION_COMMAND_KEY, "toggle flow control toolbar");
	}
	public static ToggleFlowControlToolbarAction getInstance(){
		if (action == null){
			action =  new ToggleFlowControlToolbarAction();
		}
		return action;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().toggleFlowControlToolBarVisability();
	}
}
