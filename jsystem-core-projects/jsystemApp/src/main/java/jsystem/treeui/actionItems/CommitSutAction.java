package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class CommitSutAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static CommitSutAction action;

	private CommitSutAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getCommitSutButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCommitSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_COMMIT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_COMMIT));
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-commit-scenario");
	}

	public static CommitSutAction getInstance() {
		if (action == null) {
			action = new CommitSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().commitCurrentSut();
		} catch (Exception ex) {
		}
	}

}
