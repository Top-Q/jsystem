/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;

import org.jfree.util.Log;

public class RefreshAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static RefreshAction action = null;

	private RefreshAction(){
		super();
		putValue(Action.NAME, "Refresh");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getRefreshButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REFRESH));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REFRESH));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "refresh-tests");
	}
	
	public static RefreshAction getInstance(){
		if (action == null){
			action =  new RefreshAction();
		}
		return action;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		refresh(true);
	}
	
	public void refresh(boolean saveFirst) {
		int save_Ans = 0;
		if (saveFirst){
			try {
				save_Ans = SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
			} catch (Exception e1) {
				Log.error(e1.getMessage());
			}
		}
		if(save_Ans != JOptionPane.CANCEL_OPTION){
			WaitDialog.launchWaitDialog(JsystemMapping.getInstance().getRefreshRunnerDialog(), null);
			(new Thread() {
				public void run() {
					try {
						TestRunner.treeView.getRunner().handleEvent(TestRunner.REFRESH_EVENT, null);
					} finally {
						WaitDialog.endWaitDialog();
					}
				}
			}).start();
			if(save_Ans == JOptionPane.NO_OPTION){
				ScenariosManager.resetDirty();
			}
		}
	}

}
