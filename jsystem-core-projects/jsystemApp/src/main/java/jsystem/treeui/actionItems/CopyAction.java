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

public class CopyAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static CopyAction action;
	
	private CopyAction(){
		super();
		putValue(Action.NAME, "Copy");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCopyButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_COPY_TESTS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_COPY_TESTS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "copy-tests");
	}
	
	public static CopyAction getInstance(){
		if (action == null){
			action =  new CopyAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().saveClipboardTests();
//		PasteAction.getInstance().setEnabled(true);
//		PasteAfterAction.getInstance().setEnabled(true);
	}

}
