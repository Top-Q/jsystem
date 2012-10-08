package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class UpdateScenarioAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static UpdateScenarioAction action;

	private UpdateScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getUpdateScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getUpdateScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_UPDATE));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_UPDATE));
		
		//TODO: What is this value for?
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-update-scenario");
	}

	public static UpdateScenarioAction getInstance() {
		if (action == null) {
			action = new UpdateScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().updateCurrentScenario();
		} catch (Exception ex) {
		}
	}

}
