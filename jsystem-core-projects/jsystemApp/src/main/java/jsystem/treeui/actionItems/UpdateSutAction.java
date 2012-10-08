package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class UpdateSutAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static UpdateSutAction action;

	private UpdateSutAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getUpdateSutButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getUpdateSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_UPDATE));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_UPDATE));
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-update-sut");
	}

	public static UpdateSutAction getInstance() {
		if (action == null) {
			action = new UpdateSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().updateCurrentSut();
		} catch (Exception ex) {
		}
	}

}
