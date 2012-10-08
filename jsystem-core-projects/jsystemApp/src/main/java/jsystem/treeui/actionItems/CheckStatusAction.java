/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.SystemObjectCheckWindow;
import jsystem.treeui.images.ImageCenter;

public class CheckStatusAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static CheckStatusAction action;
	
	private CheckStatusAction(){
		super();
		putValue(Action.NAME, "Check Status");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getCheckSystemObjectStatus());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_GREEN));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_GREEN));
		putValue(Action.ACTION_COMMAND_KEY, "check Status");
	}
	
	public static CheckStatusAction getInstance(){
		if (action == null){
			action =  new CheckStatusAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// running new Thread of SysObj check status window.
		SystemObjectCheckWindow sobWindow = SystemObjectCheckWindow.getInstance();
		sobWindow.showWindow();
	}

}
