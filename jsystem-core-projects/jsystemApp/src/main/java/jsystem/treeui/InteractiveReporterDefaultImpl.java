/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import javax.swing.JOptionPane;

import jsystem.framework.TestRunnerFrame;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.InteractiveReporter;
import jsystem.treeui.actionItems.StopAction;

/**
 * Handles showConfirm dialog event.
 * If the message is internal jsystem freeze message, runner execution is stopped.
 * @author goland
 */
public class InteractiveReporterDefaultImpl implements InteractiveReporter{

	public int showConfirmDialog(String title, String message, int optionType, int messageType) {
		int res = JOptionPane.showConfirmDialog(
				TestRunnerFrame.guiMainFrame, 
				message, 
				title, 
				optionType, 
				messageType);
		
		if (CommonResources.FREEZE_ON_FAIL_TITLE.equals(title) && res == 2){
			StopAction.getInstance().actionPerformed(null);
		}
		
		return res;
	}

}
