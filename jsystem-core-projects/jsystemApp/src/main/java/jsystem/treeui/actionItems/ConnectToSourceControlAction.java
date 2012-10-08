package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class ConnectToSourceControlAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	private static ConnectToSourceControlAction action;

	private ConnectToSourceControlAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getConnectToSourceContorl());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getConnectToSourceContorl());

		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_NOTCONNECTED));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_NOTCONNECTED));

		putValue(Action.ACTION_COMMAND_KEY, "sourcecontrol-update-scenario");
	}

	public static ConnectToSourceControlAction getInstance() {
		if (action == null) {
			action = new ConnectToSourceControlAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (TestRunner.treeView.getTableController().getSourceControlHandler().isEnabled()) {
			return;
		}
		TestRunner.treeView.getTableController().getSourceControlHandler().init();
		if (TestRunner.treeView.getTableController().getSourceControlHandler().isEnabled()) {
			putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
			putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
		} else {
			putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM));
			putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM));
		}
	}

}
