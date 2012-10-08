/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.BrowserLauncher;

public class ViewLogAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ViewLogAction action;
	
	public ViewLogAction(){
		super();
		putValue(Action.NAME, "Log");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getLogButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_NEW));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_NEW));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "view-log");
	}
	
	public static ViewLogAction getInstance(){
		if (action == null){
			action =  new ViewLogAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			URL url = RunnerEngineManager.getRunnerEngine().getLogUrl();
			BrowserLauncher.openURL(url.toString());
		} catch (Exception e1) {
			ErrorPanel
					.showErrorDialog(
							"Fail to activate log browser."
									+ "Please define/update the 'browser' property in the jsystem.properties file.",
							e1, ErrorLevel.Warning);
		}
	}

}
