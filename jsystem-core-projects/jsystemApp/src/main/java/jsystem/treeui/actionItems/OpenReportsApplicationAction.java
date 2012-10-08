/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.BrowserLauncher;
import jsystem.utils.UploadRunner;

public class OpenReportsApplicationAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static OpenReportsApplicationAction action;
	
	private OpenReportsApplicationAction(){
		super();
		putValue(Action.NAME, "Open Reports Application");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getOpenReportApplicationButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REPORTER));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REPORTER));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "open-reports-application");
	}
	
	public static OpenReportsApplicationAction getInstance(){
		if (action == null){
			action =  new OpenReportsApplicationAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			BrowserLauncher.openURL(UploadRunner.getReportsApplicationUrl());
		} catch (Exception exception) {
			ErrorPanel.showErrorDialog("Fail to activate reports browser."
					+ "Please define/update the 'browser' property in the jsystem.properties file.", exception,
					ErrorLevel.Warning);

		}
	}

}
