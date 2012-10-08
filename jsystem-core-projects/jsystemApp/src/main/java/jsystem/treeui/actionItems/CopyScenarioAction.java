/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class CopyScenarioAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static CopyScenarioAction action = null;

	private CopyScenarioAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getCopyScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCopyScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SAVE_AS));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SAVE_AS));
//		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "Copy-scenario");
	}
	
	public static CopyScenarioAction getInstance(){
		if (action == null){
			action =  new CopyScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (TestRunner.treeView.getTableController().copyScenario()){
			if (ScenariosManager.isDirty()) {
				ScenariosManager.resetDirty();
			}
		}
	}


}
