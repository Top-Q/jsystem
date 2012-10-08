/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.Color;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import jsystem.extensions.scenarionamehook.ScenarioNameHookManager;
import jsystem.treeui.actionItems.AgentsListAction;
import jsystem.treeui.actionItems.ClearScenarioAction;
import jsystem.treeui.actionItems.CopyAction;
import jsystem.treeui.actionItems.CopyScenarioAction;
import jsystem.treeui.actionItems.CutAction;
import jsystem.treeui.actionItems.DeleteScenarioAction;
import jsystem.treeui.actionItems.EditScenarioAction;
import jsystem.treeui.actionItems.ExitAction;
import jsystem.treeui.actionItems.ExportProjectAction;
import jsystem.treeui.actionItems.InitReportersAction;
import jsystem.treeui.actionItems.JSystemPropertiesAction;
import jsystem.treeui.actionItems.NewScenarioAction;
import jsystem.treeui.actionItems.OpenReportsApplicationAction;
import jsystem.treeui.actionItems.OpenScenarioAction;
import jsystem.treeui.actionItems.PasteAction;
import jsystem.treeui.actionItems.PasteAfterAction;
import jsystem.treeui.actionItems.PauseAction;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.ProjectNameAction;
import jsystem.treeui.actionItems.PublishXmlResultAction;
import jsystem.treeui.actionItems.RefreshAction;
import jsystem.treeui.actionItems.SaveFailedSequenceAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.actionItems.ScenarioRedoAction;
import jsystem.treeui.actionItems.ScenarioUndoAction;
import jsystem.treeui.actionItems.ShowJarListAction;
import jsystem.treeui.actionItems.ShowMultipleScenarioAction;
import jsystem.treeui.actionItems.StopAction;
import jsystem.treeui.actionItems.SwitchProjectAction;
import jsystem.treeui.actionItems.ToggleAgentToolbarAction;
import jsystem.treeui.actionItems.ToggleFlowControlToolbarAction;
import jsystem.treeui.actionItems.ToggleMainToolbarAction;
import jsystem.treeui.actionItems.ToggleSourceControlToolbarAction;
import jsystem.treeui.actionItems.ViewLogAction;
import jsystem.treeui.actionItems.ViewTestCodeAction;

import org.jfree.util.Log;

/**
 * This class creates the runner Menu Bar with all the necessary menus and items.
 * After creating an instance of it, use the getMenuBar method to get your menu tool bar.
 * 
 * @author uri.koaz
 * 
 */
public class MenuBuilder {

	static MenuBuilder mb = null;

	public static MenuBuilder getInstance(ActionListener actionListner) {
		if (mb == null) {
			mb = new MenuBuilder(actionListner);
		}

		return mb;
	}

	/**
	 * Help Menu
	 */
	protected JMenuItem menuItemAboutAQUA;

	protected JMenuItem menuItemHelp;

	protected JMenuItem menuItemAboutVersion;

	/**
	 * end of menu item
	 */

	/**
	 * Menus for Tray Icon
	 */
	protected MenuItem trayIconMenuItemExit;

	private JMenuBar menuBar;

	private MenuBuilder(ActionListener actionListenr) {
		menuBar = new JMenuBar();

		menuBar.setBackground(new Color(0xf6, 0xf6, 0xf6));

		// Create a file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		fileMenu.add(RefreshAction.getInstance());
		fileMenu.add(NewScenarioAction.getInstance());
		fileMenu.add(OpenScenarioAction.getInstance());
		fileMenu.add(DeleteScenarioAction.getInstance());
		fileMenu.add(SaveScenarioAction.getInstance());
		fileMenu.add(CopyScenarioAction.getInstance());
		fileMenu.add(SaveFailedSequenceAction.getInstance());
		fileMenu.addSeparator();
		fileMenu.add(ExportProjectAction.getInstance());
		fileMenu.add(SwitchProjectAction.getInstance());
		fileMenu.addSeparator();
		fileMenu.add(ExitAction.getInstance());
		
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.add(ScenarioUndoAction.getInstance());
		editMenu.add(ScenarioRedoAction.getInstance());
		editMenu.add(ClearScenarioAction.getInstance());
		editMenu.add(EditScenarioAction.getInstance());
		editMenu.addSeparator();
		editMenu.add(CopyAction.getInstance());
		editMenu.add(CutAction.getInstance());
		editMenu.add(PasteAction.getInstance());
		editMenu.add(PasteAfterAction.getInstance());
				
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu);
		viewMenu.add(ViewLogAction.getInstance());
		viewMenu.add(ViewTestCodeAction.getInstance());
		JMenu toolbars = new JMenu("Toolbars");
		
		JCheckBoxMenuItem mainToolbar = new JCheckBoxMenuItem(ToggleMainToolbarAction.getInstance());
		toolbars.add(mainToolbar);
		mainToolbar.setSelected(true);

		toolbars.add(new JCheckBoxMenuItem(ToggleAgentToolbarAction.getInstance()));
		toolbars.add(new JCheckBoxMenuItem(ToggleFlowControlToolbarAction.getInstance())).setSelected(true);
		toolbars.add(new JCheckBoxMenuItem(ToggleSourceControlToolbarAction.getInstance())).setSelected(false);
		viewMenu.add(toolbars);
		
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(toolsMenu);
		toolsMenu.add(InitReportersAction.getInstance());
		toolsMenu.add(PublishXmlResultAction.getInstance());
		PublishXmlResultAction.getInstance().setEnabled(false);
		toolsMenu.add(ShowJarListAction.getInstance());
		toolsMenu.add(ShowMultipleScenarioAction.getInstance());
		toolsMenu.add(OpenReportsApplicationAction.getInstance());
		//toolsMenu.add(DbPropertiesAction.getInstance());
		toolsMenu.add(JSystemPropertiesAction.getInstance());
		toolsMenu.add(AgentsListAction.getInstance());
		try {
			if (ScenarioNameHookManager.getHookClass()!= null){
				toolsMenu.add(ProjectNameAction.getInstance());
			}
		}catch (Exception e) {
			Log.error("Failed loading scenario hook.");
		}
		JMenu executionMenu = new JMenu("Execution");
		executionMenu.setMnemonic(KeyEvent.VK_X);
		menuBar.add(executionMenu);
		executionMenu.add(PlayAction.getInstance());
		executionMenu.add(PauseAction.getInstance());
		executionMenu.add(StopAction.getInstance());

		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		menuItemAboutVersion = new JMenuItem("About Version");
		menuItemAboutVersion.addActionListener(actionListenr);
		helpMenu.add(menuItemAboutVersion);
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public PopupMenu getTrayIconPopupMenu(ActionListener actionListenr) {
		PopupMenu popup = new PopupMenu();
		trayIconMenuItemExit = new MenuItem("Exit");
		trayIconMenuItemExit.addActionListener(actionListenr);
		popup.add(trayIconMenuItemExit);

		return popup;
	}

	/**
	 * Enable/Disable all menu bar items based on Runner view.
	 * 
	 * @param mode
	 */
	public void setView(int view) {

		boolean enable = true;
		if (view == TestTreeView.VIEW_RUNNING) {
			enable = false;
		}
		
		RefreshAction.getInstance().setEnabled(enable);
		NewScenarioAction.getInstance().setEnabled(enable);
		OpenScenarioAction.getInstance().setEnabled(enable);
		CopyScenarioAction.getInstance().setEnabled(enable);
		SwitchProjectAction.getInstance().setEnabled(enable);
		SaveFailedSequenceAction.getInstance().setEnabled(enable);
		InitReportersAction.getInstance().setEnabled(enable);
	
	}
	
}
