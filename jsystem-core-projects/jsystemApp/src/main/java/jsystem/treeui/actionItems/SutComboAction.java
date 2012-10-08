/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import jsystem.framework.sut.SutFactory;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.TestTreeView;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.images.ImageCenter;

public class SutComboAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static SutComboAction action = null;
	
	private static Logger log = Logger.getLogger(TestTreeView.class.getName());

	private SutComboAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getSUTSelectButton());
		putValue(Action.SHORT_DESCRIPTION, "Sut Combo");
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_CHANGE_SUT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_CHANGE_SUT));
		putValue(Action.ACTION_COMMAND_KEY, "sut-combo");
	}
	
	public static SutComboAction getInstance(){
		if (action == null){
			action =  new SutComboAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String sutName = (String) TestRunner.treeView.getSutCombo().getSelectedItem();
		//if (sutName.equals("Create a new SUT file...")){
		if(sutName.equals(SutFactory.CREATE_A_NEW_SUT_FILE)){
			sutName = TestRunner.treeView.createSUTFile();
		}
		changeSut(sutName);
	}
	
	public void changeSut(String sutName) {
		try {
			RunnerEngineManager.getRunnerEngine().changeSut(sutName);
		} catch (Exception e1) {
			log.log(Level.SEVERE, "Fail to load sut", e1);
		}
		refreshSUTTooltip();
	}
	
	public void refreshSUTTooltip() {
		String tooltip = SutFactory.CREATE_A_NEW_SUT_FILE;
		if (TestRunner.treeView.getSutCombo().getSelectedItem() != null) {
			tooltip = TestRunner.treeView.getSutCombo().getSelectedItem().toString();
		}else{
			String sutName = (String) TestRunner.treeView.getSutCombo().getSelectedItem();
			changeSut(sutName);
		}
		TestRunner.treeView.getSutCombo().setToolTipText(tooltip);
	}

}
