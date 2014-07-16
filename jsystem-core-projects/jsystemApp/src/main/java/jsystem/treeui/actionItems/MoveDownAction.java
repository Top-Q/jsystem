/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.TestsTableController.ActionType;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MoveDownAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static MoveDownAction action;
	
	private MoveDownAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getTestMoveDownMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getMoveTestDownButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_DOWN));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_DOWN));
		putValue(Action.ACTION_COMMAND_KEY, "move-test-down");
	}
	
	public static MoveDownAction getInstance(){
		if (action == null){
			action =  new MoveDownAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().handleUpDownAndDelete(ActionType.DOWN);
        firePropertyChange(action.getClass().getSimpleName(),null,null);
	}
}
