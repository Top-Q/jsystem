/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.agents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngine.ConnectionState;
import jsystem.treeui.actionItems.AgentsListAction;
import jsystem.treeui.actionItems.IgnisAction;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.images.ImageCenter;

/**
 * Confirmation dialog shown when user press on the play button.
 * Shown if the system identifies that one or more tests are configured
 * to run in a distributed manner.
 * 
 * @author goland
 */
public class DistributedExecutionConfirmation extends JPanel {

	private static final String DISTRIBUTED_EXECUTION_DISABLED = "Some of the tests/scenarios are configured" +
	                                      	" to run on a remote JRunner agent, the 'ignore.distributed.execution' was set to true.\n"+
	                                      	"All tests will run on the local machine.";
	                                      

	private static final String CONNECTED_TO_REMOTE_AGENT = "Some of the tests/scenarios are configured" +
											" to run in a distributed manner, the JRunner is conected to a remote agent.\n"+
											"Distributed execution will be ignored, all tests will run on the remote agent.";
	                                     
	private static final String MESSAGE = "The system has identified that some of the tests/scenarios are configured" +
										" to run on a remote JRunner agent.\n" +
	                                    "In order for the execution to succeed and to get correct indications,"+
	                                    "the agents must be synchronized with the local automation project.\n" +
	                                    "If you suspect that the agents might not be synchronized, open the agent management " +
	                                    "dialog (by pressing on the 'Agents List' button, and synchronize the agents";
	
	private static final String AGNET_NOT_IN_LIST_MESSAGE = 
    "Some of the tests/scenarios are associated with agents that are not in the JRunner list.\n" +
    "Add agents to JRunner before runing the scenario.";

	private static final String CONNECTION_MESSAGE = 
	                                                 "The system has identified that there is no connection to one or more agents.\n" +
	                                                 "Restore connection to agents before running the scenario";
	
	private static final long serialVersionUID = 1L;
	private RunnerEngine[] agents;
	private JDialog parentWindow;
	private boolean run = false;
	
	private OkayAction okayAction;
	private DistributedExecutionConfirmation(RunnerEngine[] engines,JDialog window){
		this.agents = engines;
		this.parentWindow = window;
	}
	
	public void init() throws Exception{
		setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(10,3);
		textArea.setEditable(false);
		textArea.setEnabled(true);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setMargin(new Insets(10,10,10,10));		
		Font f = new Font(textArea.getFont().getName(), Font.PLAIN,16);
		textArea.setFont(f);
		okayAction = new OkayAction();
		setNotificationMessage(textArea);
		add(BorderLayout.CENTER,textArea);
		JButton okButton = new JButton();
		okButton.setAction(okayAction);

		JButton cancelButton = new JButton();
		cancelButton.setAction(new CancelAction());
		
		JButton openManagerDialog = new JButton();
		openManagerDialog.setAction(AgentsListAction.getInstance());
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(openManagerDialog);
		add(BorderLayout.SOUTH,buttonsPanel);
	}

	private void setNotificationMessage(JTextArea textArea) {
		if (RunnerEngineManager.getRunnerEngine() instanceof JSystemAgentClient){
			textArea.append(CONNECTED_TO_REMOTE_AGENT);
		}else 
		if ("true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_DISTRIBUTED_EXECUTION))){
			textArea.append(DISTRIBUTED_EXECUTION_DISABLED);
		} else{
			textArea.append(MESSAGE);
			boolean noConnection = false;
			boolean nullEngine = false;
			for (RunnerEngine engine:agents){
				if (engine == null){
					nullEngine = true;
					continue;
				}
				if (!engine.getConnectionState().equals(ConnectionState.connected)){
					noConnection = true;
				}
			}
			if (nullEngine){
				okayAction.setEnabled(false);
				textArea.setText(AGNET_NOT_IN_LIST_MESSAGE);
			}
			if (noConnection){
				okayAction.setEnabled(false);
				textArea.setText(CONNECTION_MESSAGE);
			}
		}
	}
	
	class OkayAction extends IgnisAction{
		private static final long serialVersionUID = 1L;

		private OkayAction(){
			super();
			putValue(Action.NAME, "Ok");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			parentWindow.setVisible(false);
			parentWindow.dispose();
			run = true;
		}
	}
	
	class CancelAction extends IgnisAction{
		private static final long serialVersionUID = 1L;
		private CancelAction() {
			super();
			putValue(Action.NAME, "Cancel");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			parentWindow.setVisible(false);
			parentWindow.dispose();
			run = false;
		}
		
	}

	public boolean isRun() {
		return run;
	}

	public static boolean showConfirmationDialog(RunnerEngine[] engines) throws Exception{
		JDialog dialog = new JDialog();
		dialog.setIconImage(ImageCenter.getInstance()
				.getAwtImage(ImageCenter.ICON_JSYSTEM));

		dialog.setTitle("Confirmation");
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		DistributedExecutionConfirmation panel = new DistributedExecutionConfirmation(engines,dialog);
		panel.init();
		dialog.setContentPane(panel);
		dialog.setPreferredSize(new Dimension(550,400));
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return panel.run;
	}
	
	public static void main(String[] args) throws Exception{
		showConfirmationDialog(new RunnerEngine[0]);
	}
}
