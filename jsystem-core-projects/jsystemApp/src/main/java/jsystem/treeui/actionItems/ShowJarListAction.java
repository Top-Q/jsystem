/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.JarListDialog;

public class ShowJarListAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static ShowJarListAction action;
	
	private ShowJarListAction(){
		super();
		putValue(Action.NAME, "Show Jar List");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getShowJarListMenuItem());
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "show-jar-list");
	}
	
	public static ShowJarListAction getInstance(){
		if (action == null){
			action =  new ShowJarListAction();
		}
		return action;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		JarListDialog jarList = JarListDialog.getInstance();
		jarList.showWindow();
	}

}
