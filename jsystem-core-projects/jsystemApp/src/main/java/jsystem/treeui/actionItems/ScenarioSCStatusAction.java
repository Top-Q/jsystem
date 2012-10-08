package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class ScenarioSCStatusAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ScenarioSCStatusAction action;

	private ScenarioSCStatusAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getScenarioSCStatusButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getScenarioSCStatusButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SELECT_SCENARIO));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SELECT_SCENARIO));

		// TODO: What is this value for?
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-scneario-status");
	}

	public static ScenarioSCStatusAction getInstance() {
		if (action == null) {
			action = new ScenarioSCStatusAction();
		}
		return action;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			switch (TestRunner.treeView.getTableController().getSourceControlHandler().getScenarioStatus()) {
			case UNVERSIONED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_NOT_VERSIONED));
				putValue(Action.LARGE_ICON_KEY,
						ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_NOT_VERSIONED));
				break;
			case NORMAL:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_COMMITED));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_COMMITED));
				break;
			case MODIFIED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_NOT_SYNC));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_NOT_SYNC));
				break;
			case ADDED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_ADDED));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SCENARIO_ADDED));
				break;
			
			}
		} catch (Exception ex) {
		}
	}
}
