package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
/**
 * This action delete the scenarios xml and properties files in the classes and test directory
 * 
 * @author Hadar Elbaz
 * 
 */
public class DeleteScenarioAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	private static DeleteScenarioAction action;

	private DeleteScenarioAction() {
		super();
		putValue(Action.NAME, "Delete Scenario");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getDeleteScenarioWindow());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_DELETE_SCENARIO));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_DELETE_SCENARIO));
		putValue(Action.ACTION_COMMAND_KEY, "delete-scenario");
	}

	public static DeleteScenarioAction getInstance() {
		if (action == null) {
			action = new DeleteScenarioAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean isOK = TestRunner.treeView.getTableController().deleteScenario();
		if(isOK){
			ScenariosManager.resetDirty();
		}
	}
}
