/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
import org.jfree.util.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class OpenScenarioAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static OpenScenarioAction action = null;

	private OpenScenarioAction(){
		super();
		putValue(Action.NAME, "Open Scenario");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getOpenScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "open-scenario");
	}
	
	public static OpenScenarioAction getInstance(){
		if (action == null){
			action =  new OpenScenarioAction();
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
			boolean isOK = TestRunner.treeView.getTableController().switchScenario();
			if(save_Ans == JOptionPane.NO_OPTION && isOK){
				ScenariosManager.resetDirty();
			}
		}
        firePropertyChange(action.getClass().getSimpleName(),null,null);
	}

}
