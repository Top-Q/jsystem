/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;

public class InstallAgentClientAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static InstallAgentClientAction action;
	
	private InstallAgentClientAction(){
		super();
		putValue(Action.NAME, "Install New Agent");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getInstallNewAgentButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_INSTALL));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_INSTALL));
		putValue(Action.ACTION_COMMAND_KEY, "install-new-agent");
	}
	
	public static InstallAgentClientAction getInstance(){
		if (action == null){
			action =  new InstallAgentClientAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
