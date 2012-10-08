/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.ScenarioNavigationManager;

public class NextScenarioAction extends IgnisAction {

	private static final long serialVersionUID = 3266845025533733657L;
	private static NextScenarioAction action;

	private NextScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance()
				.getScenarioNavigateForward());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance()
				.getScenarioNavigateForward());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(
				ImageCenter.ICON_NAV_FORWARD));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(
				ImageCenter.ICON_NAV_FORWARD));
		putValue(Action.ACTION_COMMAND_KEY, "navigate-next-scenario");
	}

	public static NextScenarioAction getInstance() {
		if (action == null) {
			action = new NextScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ScenarioNavigationManager.getInstance().navigateForward();
	}

}
