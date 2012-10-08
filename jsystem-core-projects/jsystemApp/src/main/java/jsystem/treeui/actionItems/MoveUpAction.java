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

public class MoveUpAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static MoveUpAction action;
	
	private MoveUpAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getTestMoveUpMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getMoveTestUpButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_UP));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_UP));
		putValue(Action.ACTION_COMMAND_KEY, "move-test-up");
	}
	
	public static MoveUpAction getInstance(){
		if (action == null){
			action =  new MoveUpAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().handleUpDownAndDelete(ActionType.UP);
	}

}
