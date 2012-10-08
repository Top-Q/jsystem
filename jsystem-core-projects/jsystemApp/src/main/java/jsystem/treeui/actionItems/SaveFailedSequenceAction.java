/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;

public class SaveFailedSequenceAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static SaveFailedSequenceAction action;
	
	private SaveFailedSequenceAction(){
		super();
		putValue(Action.NAME, "Save Failed Sequence");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSaveFailedSequences());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_FILTER_SUCCESS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_FILTER_SUCCESS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "save-fail-sequence");
	}
	
	public static SaveFailedSequenceAction getInstance(){
		if (action == null){
			action =  new SaveFailedSequenceAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WaitDialog.launchWaitDialog("Filter success ...", null);
		(new Thread() {
			public void run() {
				try {
					TestRunner.treeView.getTableController().filterSuccess(ScenariosManager.getInstance().getCurrentScenario());
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		}).start();
	}

}
