/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class ViewTestCodeAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ViewTestCodeAction action = null;

	private ViewTestCodeAction(){
		super();
		putValue(Action.NAME, "View Test Code");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getViewTestCodeButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_CODE));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_CODE));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "view-test-code");
	}
	
	public static ViewTestCodeAction getInstance(){
		if (action == null){
			action =  new ViewTestCodeAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().getTestCode();
	}

}
