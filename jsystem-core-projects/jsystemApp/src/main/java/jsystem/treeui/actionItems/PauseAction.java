/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class PauseAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static PauseAction action;

	private PauseAction(){
		super();
		putValue(Action.NAME, "Pause");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getPauseButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SUSPEND));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SUSPEND));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "pause-tests");
	}
	
	public static PauseAction getInstance(){
		if (action == null){
			action =  new PauseAction();
		}
		return action;
	}
	
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.setPaused(true);
		TestRunner.treeView.getRunner().handleEvent(TestRunner.PAUSE_EVENT, null);
	}


}
