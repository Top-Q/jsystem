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

/**
 * @author Kobi Gana
 *
 */
public class MoveToTopAction extends IgnisAction {

	private static final long serialVersionUID = 42831365889855674L;
	private static MoveToTopAction action;
	
	public MoveToTopAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getTestMoveToTopMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getMoveTestToTopButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_TO_TOP));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_TO_TOP));
		putValue(Action.ACTION_COMMAND_KEY, "move-test-to-top");
	}
	
	public static MoveToTopAction getInstance(){
		if (action == null){
			action =  new MoveToTopAction();
		}
		return action;
	}
	/**
	 * @see jsystem.treeui.actionItems.IgnisAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().handleUpDownAndDelete(ActionType.TO_TOP);
	}

}
