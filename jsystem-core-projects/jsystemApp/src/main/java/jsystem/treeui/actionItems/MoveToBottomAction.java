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

/**
 * @author Kobi Gana
 *
 */
public class MoveToBottomAction extends IgnisAction {

	private static final long serialVersionUID = 6645847050708559419L;
	
	private static MoveToBottomAction action;
	
	public MoveToBottomAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getTestMoveToBottomMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getMoveTestToBottomButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_TO_BOTTOM));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_TO_BOTTOM));
		putValue(Action.ACTION_COMMAND_KEY, "move-test-to-bottom");
	}
	
	public static MoveToBottomAction getInstance(){
		if (action == null){
			action =  new MoveToBottomAction();
		}
		return action;
	}
	
	/**
	 * @see jsystem.treeui.actionItems.IgnisAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().handleUpDownAndDelete(ActionType.TO_BOTTOM);
        firePropertyChange(action.getClass().getSimpleName(),null,null);
	}

}
