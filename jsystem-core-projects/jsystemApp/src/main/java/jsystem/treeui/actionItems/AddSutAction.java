package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class AddSutAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static AddSutAction action;

	private AddSutAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getAddSutButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getAddSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_ADD));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_ADD));
		
		//TODO: What is this value for?
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-add-scenario");
	}

	public static AddSutAction getInstance() {
		if (action == null) {
			action = new AddSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().getSourceControlHandler().addCurrentSut();
		} catch (Exception ex) {
		}
	}

}
