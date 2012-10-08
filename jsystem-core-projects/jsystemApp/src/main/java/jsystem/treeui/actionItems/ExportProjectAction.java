/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.export.ExportImportWizard;
import jsystem.treeui.images.ImageCenter;

public class ExportProjectAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ExportProjectAction action = null;

	private ExportProjectAction(){
		super();
		putValue(Action.NAME, "Export Project");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getExportButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_EXPORT_WIZ));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_EXPORT_WIZ));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "export-project");
	}
	
	public static ExportProjectAction getInstance(){
		if (action == null){
			action =  new ExportProjectAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ExportImportWizard exportWizard = new ExportImportWizard(true);
		exportWizard.launch(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER),
				JSystemProperties.getCurrentTestsPath(), System.getProperty("user.dir"));

	}
}
