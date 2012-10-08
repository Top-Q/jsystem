/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.TestsTableController.ActionType;

public class NewSwitchCaseAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static NewSwitchCaseAction action;
	
	private NewSwitchCaseAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getCaseButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCaseButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SWITCH));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SWITCH));
		putValue(Action.ACTION_COMMAND_KEY, "flowcontrol-new-switchcase");
	}
	
	public static NewSwitchCaseAction getInstance(){
		if (action == null){
			action =  new NewSwitchCaseAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().addFlowControlElement(ActionType.NEW_SWITH_CASE);
		} catch (Exception ex) {
		}
	}

}
