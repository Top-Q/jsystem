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

public class NewElseIfAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static NewElseIfAction action;
	
	private NewElseIfAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getElseIfButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getElseIfButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
		putValue(Action.ACTION_COMMAND_KEY, "flowcontrol-new-elseif");
	}
	
	public static NewElseIfAction getInstance(){
		if (action == null){
			action =  new NewElseIfAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().addFlowControlElement(ActionType.NEW_ELSE_IF);
		} catch (Exception ex) {
		}
	}

}
