/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JDialog;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;

public class ScenarioUIUtils {
	
	/**
	 * Tries to create an instance of current scenario and shows an error dialog in case of errors<br>
	 * after this method is run the current scenario should be valid
	 *  
	 * @see ScenariosManager - checkScenario()
	 */
	public static void checkCurrentScenario(){
		String result = ScenariosManager.getInstance().checkScenario();
		if (result != null){
			ErrorPanel.showErrorDialog("Problem loading Scenario", result, ErrorLevel.Error);
		}
	}

	/**
	 */
	public static void showErrorDialog(Vector<String[]> errors) {
		if (errors != null) {
			String textMessage = "Fail to load part of the tests. Following are posible actions/causes:\n"
					+ " 1. Rebuild your tests project.\n" 
					+ " 2. Check that you are using updated jars.\n"
					+ " 3. Check that you are not missing any external jars\n"
					+ " 4. Check your test class for problematic static members or problematic constructors.\n"
					+ " 5. If the problematic tests are old tests remove them from the scenario.";
			final JDialog d = ErrorPanel.getErrorDialog(errors, textMessage);
			d.setTitle("Tests Errors:");
			d.pack();
			d.setVisible(true);
			d.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					d.dispose();
				}
			});
		}
	}

}
