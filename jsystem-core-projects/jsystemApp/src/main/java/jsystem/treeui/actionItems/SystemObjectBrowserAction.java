/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.sobrows.SOProcess;

public class SystemObjectBrowserAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static SystemObjectBrowserAction action;
	
	private SystemObjectBrowserAction(){
		super();
		putValue(Action.NAME, "System Object Browser");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSystemObjectBrowserButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SO_GEN));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SO_GEN));
		putValue(Action.ACTION_COMMAND_KEY, "system-object-browser");
	}
	
	public static SystemObjectBrowserAction getInstance(){
		if (action == null){
			action =  new SystemObjectBrowserAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 // Launch the system objects tests generation
		(new Thread() {
			public void run() {
				SOProcess.processSOGenerator();
				WaitDialog.launchWaitDialog("update runner ...", null);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// ignored
				}
				try {
					TestRunner.treeView.getRunner().handleEvent(TestRunner.REFRESH_EVENT, null);
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		}).start();
	}

}
