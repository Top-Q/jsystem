package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.BrowserLauncher;

public class ViewDocumentationAction extends IgnisAction {

	private static final long serialVersionUID = 1L;

	private static ViewDocumentationAction action;

	public ViewDocumentationAction() {
		super();
		putValue(Action.NAME, "Documentation");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getLogButton());
	}

	public static ViewDocumentationAction getInstance() {
		if (action == null) {
			action = new ViewDocumentationAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String url = "https://github.com/Top-Q/jsystem-docs/wiki";
		try {
			BrowserLauncher.openURL(url);
		} catch (Exception e1) {
			ErrorPanel.showErrorDialog("Fail to activate documentation in browser from " + url, e1, ErrorLevel.Warning);
		}
	}

}
