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

public class NewIfConditionAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static NewIfConditionAction action;
	
	private NewIfConditionAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getIfButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getIfButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
		putValue(Action.ACTION_COMMAND_KEY, "flowcontrol-new-ifcondition");
	}
	
	public static NewIfConditionAction getInstance(){
		if (action == null){
			action =  new NewIfConditionAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().addFlowControlElement(ActionType.NEW_IF_CONDITION);
		} catch (Exception ex) {
		}
	}

}
