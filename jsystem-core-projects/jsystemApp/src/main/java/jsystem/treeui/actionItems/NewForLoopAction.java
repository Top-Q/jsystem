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

public class NewForLoopAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static NewForLoopAction action;
	
	private NewForLoopAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getLoopButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getLoopButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_FOR_LOOP));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_FOR_LOOP));
		putValue(Action.ACTION_COMMAND_KEY, "flowcontrol-new-forloop");
	}
	
	public static NewForLoopAction getInstance(){
		if (action == null){
			action =  new NewForLoopAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().addFlowControlElement(ActionType.NEW_FOR_LOOP);
		} catch (Exception ex) {
		}
	}

}
