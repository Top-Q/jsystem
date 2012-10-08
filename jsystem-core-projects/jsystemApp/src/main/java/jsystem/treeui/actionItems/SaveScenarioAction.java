/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.dialog.DialogWithCheckBox;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.utilities.UnmodifiableFileHandler;

public class SaveScenarioAction extends IgnisAction implements ScenarioListener {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(SaveScenarioAction.class.getName());

	private static SaveScenarioAction action;

	public SaveScenarioAction() {
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getSaveScenarioButton());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSaveScenarioButton());
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "save-scenario");
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_SAVE_SCENARIO));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_SAVE_SCENARIO));
		setEnabled(false);
	}

	public static SaveScenarioAction getInstance() {
		if (action == null) {
			action = new SaveScenarioAction();
			ListenerstManager.getInstance().addListener(action);
		}
		return action;
	}

	public int saveCurrentScenarioWithConfirmation() throws Exception {
		int ans = JOptionPane.YES_OPTION;
		if (ScenariosManager.isDirty()) {
			if (ScenariosManager.getInstance().getCurrentScenario().canWrite()) {
				ans = DialogWithCheckBox.showConfirmDialog("You have unsaved changes",
						"Do you want to save the changes?", "In the future, don't show this dialog and Auto-save",
						FrameworkOptions.AUTO_SAVE_NO_CONFIRMATION);
				if (ans != JOptionPane.YES_OPTION) {
					return ans;
				}
			}
		} else {
			return ans;
		}
		actionPerformed(null);
		return ans;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!UnmodifiableFileHandler.getInstance().makeWritable(
				ScenariosManager.getInstance().getCurrentScenario().getScenarioFiles())) {
			// Failed to set file permissions to writable or user canceled
			// operation.
			return;
		}
		WaitDialog.launchWaitDialog("Saving Scenario", null);
		try {
			saveCurrentScenario();
		} catch (Exception ex) {
			ErrorPanel.showErrorDialog("Save Scenario", ex, ErrorLevel.Error);
		}
		log.fine("Save Scenario swing worker - closing waitDialog");
		WaitDialog.endWaitDialog();
	}

	public void saveCurrentScenario() throws Exception {

		ScenariosManager.setDirtyStateEventsSilent(true);
		Scenario s = ScenariosManager.getInstance().getCurrentScenario();
		s.save();
		ScenariosManager.setDirtyStateEventsSilent(false);
		ScenariosManager.resetDirty();
	}

	class SaveScenario extends SwingWorker<String, Object> {
		public String doInBackground() {
			try {
				saveCurrentScenario();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Save Scenario", e, ErrorLevel.Error);
			}
			return "";
		}

		protected void done() {
			log.fine("Save Scenario swing worker - closing waitDialog");
			WaitDialog.endWaitDialog();
		}
	}

	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirectoryChanged(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		setEnabled(isDirty);
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues, Parameter[] newValues) {
		// TODO Auto-generated method stub

	}
}
