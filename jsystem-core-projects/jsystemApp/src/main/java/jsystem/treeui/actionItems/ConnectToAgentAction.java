/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.RemoteAgentUIComponents;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;

/**
 * Ignis action for connecting to engine.
 * @author goland
 */
public class ConnectToAgentAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	private static ConnectToAgentAction action;

	private ConnectToAgentAction(){
		super();
		putValue(Action.NAME, "Connect To Agent");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getConnectToAgentButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_CONNECT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_CONNECT));
		putValue(Action.ACTION_COMMAND_KEY, "connect-to-agent");
	}
	public static ConnectToAgentAction getInstance(){
		if (action == null){
			action =  new ConnectToAgentAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		connectToAgent(TestRunner.treeView.getRunner());
	}
	private static void connectToAgent(final TestRunner runner) {
		WaitDialog.launchWaitDialog("Connecting to " + RemoteAgentUIComponents.getSelectedAgent(), null);
		Thread t = new Thread(){
			public void run(){
				try {
					runner.handleEvent(TestRunner.CONNECT_TO_AGENT, RemoteAgentUIComponents.getSelectedAgent());
				}finally {
					WaitDialog.endWaitDialog();
				}
			}
		};
		t.start();
	}	
}
