/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;

public class StatusAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static StatusAction action;
	
	private StatusAction(){
		super();
		putValue(Action.NAME, "Agent Status");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getAgentStatusButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
		putValue(Action.ACTION_COMMAND_KEY, "agent-status");
	}
	
	public static StatusAction getInstance(){
		if (action == null){
			action =  new StatusAction();
		}
		return action;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

}
