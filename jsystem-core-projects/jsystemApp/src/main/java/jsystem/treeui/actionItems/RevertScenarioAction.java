package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class RevertScenarioAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RevertScenarioAction action;

	private RevertScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getRevertScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getRevertScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_REVERT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_REVERT));
		
		//TODO: What is this value for?
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-revert-scneario");
	}

	public static RevertScenarioAction getInstance() {
		if (action == null) {
			action = new RevertScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().revertCurrentScenario();
		} catch (Exception ex) {
		}
	}

}
