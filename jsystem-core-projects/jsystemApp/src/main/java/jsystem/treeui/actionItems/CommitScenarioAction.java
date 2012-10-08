package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class CommitScenarioAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CommitScenarioAction action;

	private CommitScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getCommitScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCommitScenarioButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_COMMIT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_COMMIT));
		
		//TODO: What is this value for?
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-commit-scenario");
	}

	public static CommitScenarioAction getInstance() {
		if (action == null) {
			action = new CommitScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().commitCurrentScenario();
		} catch (Exception ex) {
		}
	}

}
