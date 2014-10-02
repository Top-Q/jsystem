/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.TestsTableController.ActionType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

public class RemoveItemAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static RemoveItemAction action;
	
	private RemoveItemAction(){
		super();
		putValue(Action.NAME, "Remove Item");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getRemoveTestsButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_DELETE));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_DELETE));
		putValue(Action.ACTION_COMMAND_KEY, "remove-item");
	}
	
	public static RemoveItemAction getInstance(){
		if (action == null){
			action =  new RemoveItemAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WaitDialog.launchWaitDialog("Delete tests ...", null);
		(new Thread() {
			public void run() {
				try {
					try {
						TestRunner.treeView.getTableController().handleUpDownAndDelete(ActionType.DELETE);
					} catch (Exception e1) {
						TestRunner.getLog().log(Level.SEVERE, "Fail to delete tests", e1);
					}
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		}).start();
        firePropertyChange(action.getClass().getSimpleName(),null,null);
	}

}
