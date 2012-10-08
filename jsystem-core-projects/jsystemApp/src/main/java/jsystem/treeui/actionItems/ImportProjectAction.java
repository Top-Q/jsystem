/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.export.ExportImportWizard;
import jsystem.treeui.images.ImageCenter;

public class ImportProjectAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ImportProjectAction action = null;

	private ImportProjectAction(){
		super();
		putValue(Action.NAME, "Import Project");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getImportButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_IMPORT_WIZ));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_IMPORT_WIZ));
		//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "import-project");
	}
	
	public static ImportProjectAction getInstance(){
		if (action == null){
			action =  new ImportProjectAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			ExportImportWizard exportWizard = new ExportImportWizard(false);
			exportWizard.launch(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_SOURCE_FOLDER),
					JSystemProperties.getCurrentTestsPath(), System.getProperty("user.dir"));
	}
}
