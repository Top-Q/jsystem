package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;

public class ToggleSourceControlToolbarAction extends IgnisAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ToggleSourceControlToolbarAction action;
	
	private ToggleSourceControlToolbarAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getSourceControlToolbar());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSourceControlToolbar());
		putValue(Action.ACTION_COMMAND_KEY, "toggle source control toolbar");
	}
	
	public static ToggleSourceControlToolbarAction getInstance(){
		if (action == null){
			action =  new ToggleSourceControlToolbarAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().toggleSourceControlToolBarVisability();
		
	}

}
