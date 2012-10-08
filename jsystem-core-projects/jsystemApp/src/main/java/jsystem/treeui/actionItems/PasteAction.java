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

public class PasteAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static PasteAction action;
	
	private PasteAction(){
		super();
		putValue(Action.NAME, "Paste");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getPasteButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_PASTE_TESTS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_PASTE_TESTS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "paste-tests");
		setEnabled(false);
	}
	
	public static PasteAction getInstance(){
		if (action == null){
			action =  new PasteAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().addClipboardTests(false);
	}

}
