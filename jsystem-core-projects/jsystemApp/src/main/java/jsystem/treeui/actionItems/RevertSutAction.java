package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class RevertSutAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static RevertSutAction action;

	private RevertSutAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getRevertSutButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getRevertSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_REVERT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_REVERT));
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-revert-sut");
	}

	public static RevertSutAction getInstance() {
		if (action == null) {
			action = new RevertSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().revertCurrentSut();
		} catch (Exception ex) {
		}
	}

}
