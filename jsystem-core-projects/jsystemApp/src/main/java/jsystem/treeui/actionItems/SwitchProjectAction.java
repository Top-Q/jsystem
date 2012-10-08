/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.JsystemAppTitle;
import jsystem.treeui.ScenarioUIUtils;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.fixtureui.FixturePanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.ScenarioNavigationManager;
import jsystem.treeui.utilities.ApplicationUtilities;
import jsystem.upgrade.UpgradeManager;

import org.jfree.util.Log;

public class SwitchProjectAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static SwitchProjectAction action = null;

	private SwitchProjectAction(){
		super();
		putValue(Action.NAME, "Switch Project");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getSwitchProjectButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_CHANGE_TESTS_DIR));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_CHANGE_TESTS_DIR));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "switch-project");
	}
	
	public static SwitchProjectAction getInstance(){
		if (action == null){
			action =  new SwitchProjectAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int save_Ans = 0;
		try {
			save_Ans = SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
		} catch (Exception e1) {
			Log.error(e1.getMessage());
		}
		if (save_Ans != JOptionPane.CANCEL_OPTION){
			String newDir = ApplicationUtilities.chooseClassesDirectory(null, false);
			changeTestDir(newDir,null,false);
			if (save_Ans == JOptionPane.NO_OPTION && newDir!=null){
				ScenariosManager.resetDirty();
			}
		}

	}
	
	/**
	 * change test directory to new directory, change sut to given sut
	 * 
	 * @param newDir	the new Classes directory full path
	 * @param sut		the sut name to change to 
	 * @param wait		wait for init scenario and refresh tree to end (Wait for the thread)
	 */
	public void changeTestDir(String newDir,String sut,boolean wait) {
		if (newDir == null) {
			return;
		}
		try {
			RunnerEngineManager.getRunnerEngine().changeProject(newDir);
		}catch (Exception e){
			ErrorPanel.showErrorDialog("Failed switching project.", e,ErrorLevel.Error);
		}
		
		
		//resetting navigation manager.
		ScenarioNavigationManager.getInstance().init();

		try {
			UpgradeManager.upgrade(false);
		}catch (Exception e){
			ErrorPanel.showErrorDialog("Failed converting old scenarios.", e,ErrorLevel.Error);
		}
		final File sutFile = SutFactory.getInstance().getSutFile();
		// Popping wait dialog while scenario is loading.
		WaitDialog.launchWaitDialog("Opening project ", null);
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if (sutFile!=null){
						SutComboAction.getInstance().changeSut(sutFile.getPath());
					}
					initScenarioAndRefreshTree();
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		});
		t.start();
		
		if (wait){
			try {
				t.join();
			}catch (Exception e){
				ErrorPanel.showErrorDialog("Failed waiting for project to load.", e,ErrorLevel.Error);
			}
		}	
	}
	
	private void initScenarioAndRefreshTree() {
		ScenarioUIUtils.checkCurrentScenario();
		TestRunner.treeView.getSutCombo().setModel(new DefaultComboBoxModel(SutFactory.getInstance().getOptionalSuts()));
		// fix the empty sut list bug
		int sutIndex = SutFactory.getInstance().getCurrentSutIndex();
		if (sutIndex >= 0) {
			TestRunner.treeView.getSutCombo().setSelectedIndex(sutIndex);
		}
		TestRunner.treeView.getRunner().handleEvent(TestRunner.REFRESH_EVENT, null);
		/**
		 * update fixture panel
		 */
		((FixturePanel) TestRunner.treeView.getFixture().getComponent(0)).showFixtureTree();
		TestRunner.treeView.setTitle(JsystemAppTitle.getInstance().generateTitle());
	}
	


}
