/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.SutEditorManager;
import jsystem.treeui.images.ImageCenter;

public class EditSutAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static EditSutAction action;

	private EditSutAction(){
		super();
		putValue(Action.NAME, "Edit Sut");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getEditSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_EDIT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_EDIT));
		putValue(Action.ACTION_COMMAND_KEY, "edit-sut");
	}
	
	public static EditSutAction getInstance(){
		if (action == null){
			action =  new EditSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SutEditorManager.getInstance().launchEditor();
	}

}
