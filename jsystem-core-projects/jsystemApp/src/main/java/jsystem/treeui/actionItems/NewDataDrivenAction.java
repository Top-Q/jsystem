package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.TestsTableController.ActionType;

/**
 * 
 * Adds data driven container to the scenario.
 * @author Itai_Agmon
 *
 */
public class NewDataDrivenAction extends IgnisAction {
	
	private static final long serialVersionUID = 1L;
	
	private static NewDataDrivenAction action;

	
	private NewDataDrivenAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getDataDrivenButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getDataDrivenButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_DATA_DRIVEN));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_DATA_DRIVEN));
		putValue(Action.ACTION_COMMAND_KEY, "flowcontrol-new-datadriven");
	}
	

	public static NewDataDrivenAction getInstance(){
		if (action == null){
			action =  new NewDataDrivenAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestRunner.treeView.getTableController().addFlowControlElement(ActionType.NEW_DATA_DRIVEN);
		} catch (Exception ex) {
		}
	}

}
