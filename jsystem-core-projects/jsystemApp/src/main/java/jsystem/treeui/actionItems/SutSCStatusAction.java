package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class SutSCStatusAction extends IgnisAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SutSCStatusAction action;

	private SutSCStatusAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getSutSCStatusButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSutSCStatusButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT));
		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-sut-status");
	}

	public static SutSCStatusAction getInstance() {
		if (action == null) {
			action = new SutSCStatusAction();
		}
		return action;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			switch (TestRunner.treeView.getTableController().getSourceControlHandler().getSutStatus()) {
			case UNVERSIONED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_NOT_VERSIONED));
				putValue(Action.LARGE_ICON_KEY,
						ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_NOT_VERSIONED));
				break;
			case NORMAL:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_COMMITED));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_COMMITED));
				break;
			case MODIFIED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_NOT_SYNC));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_NOT_SYNC));
				break;
			case ADDED:
				putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_ADDED));
				putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SC_SUT_ADDED));
				break;
			
			}
		} catch (Exception ex) {
		}
	}
}
