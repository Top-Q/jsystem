/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.extensions.scenarionamehook.ProjectNameDialog;

public class ProjectNameAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ProjectNameAction action;
	
	private ProjectNameAction(){
		super();
		putValue(Action.NAME, "Project Name");
		putValue(Action.SHORT_DESCRIPTION, "Set project name");
		putValue(Action.ACTION_COMMAND_KEY, "set-project-name");
	}
	
	public static ProjectNameAction getInstance(){
		if (action == null){
			action =  new ProjectNameAction();
		}
		return action;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectNameDialog.showProjectNameDialog();
	}
}
