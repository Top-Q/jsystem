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

public class ClearScenarioAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ClearScenarioAction action;
	
	private ClearScenarioAction(){
		super();
		putValue(Action.NAME, "Clear");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getClearScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_CLEAR));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_CLEAR));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "cleare-scenario");
	}
	
	public static ClearScenarioAction getInstance(){
		if (action == null){
			action =  new ClearScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().clearScenario(true);
	}

}
