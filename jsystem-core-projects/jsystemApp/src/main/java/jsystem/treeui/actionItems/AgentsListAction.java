/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.agents.AgentsDialog;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.StringUtils;

public class AgentsListAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static AgentsListAction action;
	
	private AgentsListAction(){
		super();
		putValue(Action.NAME, "Agents List");
		putValue(Action.SHORT_DESCRIPTION, "Manage agents");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "manage-agents");
	}
	
	public static AgentsListAction getInstance(){
		if (action == null){
			action =  new AgentsListAction();
		}
		return action;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			AgentsDialog.showAgentsDialog();
		}catch (Exception ex){
			ErrorPanel.showErrorDialog("Failed opening agents dialog", StringUtils.getStackTrace(ex), ErrorLevel.Error);
		}
	}
}
