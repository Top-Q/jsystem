/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import jsystem.framework.common.CommonResources;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.multiscenario.MultiScenarioDialog;

/**
 * @author Michael Oziransky
 */
@SuppressWarnings("serial")
public class ShowMultipleScenarioAction extends IgnisAction {

	private static Logger log = Logger.getLogger(ShowMultipleScenarioAction.class.getName());
	
	private static ShowMultipleScenarioAction showMultipleScenarioAction = null;
	
	private ShowMultipleScenarioAction() {
		super();
		putValue(Action.NAME, "Multiple Scenarios Suite Execution");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getShowMultiScenarioMenuItem());
		putValue(Action.ACTION_COMMAND_KEY, "show-multi-scenario");
	}
	
	public static ShowMultipleScenarioAction getInstance() {
		if (showMultipleScenarioAction == null) {
			showMultipleScenarioAction = new ShowMultipleScenarioAction();
		}
		return showMultipleScenarioAction;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {		
		File commandsFile = new File(System.getProperty("user.dir"), 
				CommonResources.JSYSTEM_COMMAND_LINE_FILE_NAME);
		
		if (!commandsFile.exists()) {
			try {
				commandsFile.createNewFile();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Fail to create run XML file", e);
			}
		}		
		
		MultiScenarioDialog multipleScenarioDialog = new MultiScenarioDialog();
		multipleScenarioDialog.loadConfiguration(commandsFile);
		multipleScenarioDialog.commandSelectionChanged(null);
		multipleScenarioDialog.setVisible(true);
	}
}
