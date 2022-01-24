/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class StopAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static StopAction action = null;

	private StopAction(){
		super();
		putValue(Action.NAME, "Stop");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getStopButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_STOP));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_STOP));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "stop-tests");
	}
	
	public static StopAction getInstance(){
		if (action == null){
			action =  new StopAction();
		}
		return action;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (TestRunner.treeView.isPaused()) {
			TestRunner.treeView.setPaused(false);
			TestRunner.treeView.getRunner().handleEvent(TestRunner.CONTINUE_EVENT, null);
		}
		TestRunner.treeView.setStopped(true);
		TestRunner.treeView.getRunner().handleEvent(TestRunner.STOP_EVENT, null);
	}
	
	@Override
	public void setEnabled(boolean arg0) {
		// enable only if global flag enable stop
		if ("true".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_DISABE_STOP)))
		{
			super.setEnabled(false);
		}
		else
			super.setEnabled(arg0);
	}
}
