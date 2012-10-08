/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngine.ConnectionState;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.SwingUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * All agents reporters panel. This panel holds multiple sub-panel, one per agent.
 */
public class ReportersPanel extends JTabbedPane {

	/**
	 */
	private static final long serialVersionUID = 1L;
	private static ReportersPanel allReportsPanel;
	private RunnerEngine[] agents;
	
	public ReportersPanel(RunnerEngine[] agents){
		super(JTabbedPane.RIGHT);
		this.agents = agents;
	}
	
	private void initPanel() {
		for (RunnerEngine agent:agents){
			addAgentReporter(agent);
		}
	}

	private void addAgentReporter(RunnerEngine agent) {
		ReporterPanel panel = new ReporterPanel(agent);
		panel.initPanel();
		ImageIcon icon = 
			agent.getConnectionState().equals(ConnectionState.connected) ? ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK):
				ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM);
		addTab(agent.getId(), icon, panel);
		agent.addListener(new JTabbedPaneListener(agent));
	}
	
	public static ReportersPanel initPanel(RunnerEngine[] agents) {
		allReportsPanel = new ReportersPanel(agents);
		allReportsPanel.initPanel();
		return allReportsPanel;
	}
	
	public static void removeAgent(String url){
		int index = allReportsPanel.indexOfTab(url);
		if (index != -1){
			allReportsPanel.removeTabAt(index);
		}
	}
	
	public static void addAgent(RunnerEngine agent){
		if (agent == null){
			return;
		}
		if (allReportsPanel.indexOfTab(agent.getId()) <0) {
			allReportsPanel.addAgentReporter(agent);
		}
	}

	/**
	 * @param status enable/disable
	 */
	public static void setInitReportsEnable(boolean status) {
		for (int i = 0 ; i < allReportsPanel.getTabCount(); i++) {
			((ReporterPanel)allReportsPanel.getComponent(i)).setInitReportsEnable(status);
		}
	}
	
	public class JTabbedPaneListener implements ExecutionListener,NotificationListener{
		private RunnerEngine engine;
		JTabbedPaneListener(RunnerEngine engine){
			this.engine =engine;
		}
		@Override
		public void errorOccured(String title, String message, ErrorLevel level) {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO_ERROR));
		}

		@Override
		public void executionEnded(String scenarioName) {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));			
		}

		@Override
		public void remoteExit() {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));			
			
		}

		@Override
		public void remotePause() {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_PAUSE));			
		}

		@Override
		public void addWarning(Test test) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endRun() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startTest(TestInfo testInfo) {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_RUN));						
		}

		@Override
		public void addError(Test arg0, Throwable arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addFailure(Test arg0, AssertionFailedError arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endTest(Test arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startTest(Test arg0) {
			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_RUN));									
		}

		@Override
		public void handleNotification(Notification notification, Object handback) {
			if (!(notification instanceof JMXConnectionNotification)){
				return;
			}
			ImageIcon icon;
			String notificationType = notification.getType();
			if (notificationType.equals(JMXConnectionNotification.CLOSED) ||
					notificationType.equals(JMXConnectionNotification.FAILED)){
				icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM);
			}else {
				icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK);
			}

			int index = allReportsPanel.indexOfTab(engine.getId());
			allReportsPanel.setIconAt(index, icon);			
		}
		@Override
		public void endContainer(JTestContainer container) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void endLoop(AntForLoop loop, int count) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void startContainer(JTestContainer container) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void startLoop(AntForLoop loop, int count) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

/**
 * Single, per agent, reporter panel.
 */
class ReporterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private RunnerEngine agent;
	private TestReporterTable reportsTable;
	private JButton initReport;
	
	public ReporterPanel(RunnerEngine agent){
		this.agent = agent;
	}
	
	public void initPanel(){
		
		setLayout(new BorderLayout());
	
		// Top panel
		JToolBar toolBar = SwingUtils.getJToolBarWithBgImage("My Toolbar", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		Insets margins = new Insets(4, 2, 4, 2);
		
		// Table
		reportsTable = new TestReporterTable();
		agent.addListener(reportsTable);
		JScrollPane tableScroll = new JScrollPane();
		tableScroll.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
		tableScroll.setViewportView(reportsTable);
		add(tableScroll,BorderLayout.CENTER);
		
		// Init reports button
		initReport = new JButton();
		initReport.setMargin(margins);
		initReport.setAction(new AgentInitReports(agent));
		setInitReportsEnable(true);
		
		// Open log file button
		JButton openBrowser = new JButton();
		openBrowser.setMargin(margins);
		openBrowser.setAction(new AgentViewLogAction(agent));
		
		// Agent name label
		JLabel label = new JLabel();
		Font f = new Font(label.getFont().getFontName(),Font.BOLD,label.getFont().getSize());
		label.setFont(f);
		label.setText("Agent: " + agent.getId()+ " ");
		
		// Add to top panel
		toolBar.add(label);
		toolBar.add(openBrowser);
		toolBar.add(initReport);
		add(toolBar,BorderLayout.NORTH);
		
	}

	/**
	 * @param status enable/disable
	 */
	public void setInitReportsEnable(boolean status) {
		initReport.setEnabled(status);		
	}
	
}
