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

public class CutAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static CutAction action;
	
	private CutAction(){
		super();
		putValue(Action.NAME, "Cut");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_CUT_TESTS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_CUT_TESTS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "cut-tests");
	}
	
	public static CutAction getInstance(){
		if (action == null){
			action =  new CutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().saveClipboardTests();
		RemoveItemAction.getInstance().actionPerformed(null);
//		PasteAction.getInstance().setEnabled(true);
//		PasteAfterAction.getInstance().setEnabled(true);
	}

}
