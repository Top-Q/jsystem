/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.ScenarioNavigationManager;

public class PreviosScenarioAction  extends IgnisAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5082819280951972708L;
	private static PreviosScenarioAction action;

	private PreviosScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance()
				.getScenarioNavigateBackword());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance()
				.getScenarioNavigateBackword());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(
				ImageCenter.ICON_NAV_BACKWARD));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(
				ImageCenter.ICON_NAV_BACKWARD));
		putValue(Action.ACTION_COMMAND_KEY, "navigate-privios-scenario");
	}

	public static PreviosScenarioAction getInstance() {
		if (action == null) {
			action = new PreviosScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ScenarioNavigationManager.getInstance().navigateBackward();
	}

}
