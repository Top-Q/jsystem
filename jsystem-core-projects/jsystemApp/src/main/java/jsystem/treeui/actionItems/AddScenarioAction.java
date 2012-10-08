package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class AddScenarioAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static AddScenarioAction action;

	private AddScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getAddScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getAddScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_ADD));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_ADD));
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-add-scenario");
	}

	public static AddScenarioAction getInstance() {
		if (action == null) {
			action = new AddScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().addCurrentScenario();
		} catch (Exception ex) {
		}
	}

}
