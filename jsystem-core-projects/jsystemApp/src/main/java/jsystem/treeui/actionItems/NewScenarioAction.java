/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.jfree.util.Log;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class NewScenarioAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static NewScenarioAction action = null;

	private NewScenarioAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getNewScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getNewScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO_NEW));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO_NEW));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "new-scenario");
	}
	
	public static NewScenarioAction getInstance(){
		if (action == null){
			action =  new NewScenarioAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int save_Ans = 0;
		try {
			save_Ans = SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
		} catch (Exception e1) {
			Log.error(e1.getMessage());
		}
		if(save_Ans != JOptionPane.CANCEL_OPTION){
			boolean isOK = TestRunner.treeView.getTableController().createNewScenario();
			if(save_Ans == JOptionPane.NO_OPTION && isOK){
				ScenariosManager.resetDirty();
			}
		}
	}

}
