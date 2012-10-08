/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.management.remote.JMXConnectionNotification;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.treeui.actionItems.ConnectToAgentAction;
import jsystem.treeui.client.AgentList;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;

/**
 * Manages remote agent connectivity UI components.
 * The involved compoenets are:<br>
 * 1. Status button. - green = connection is okay. red - problems with connection to agent.<br>
 * 2. Agents list - list of agent the the user recently worked with. when user presses enter the runner tries to connect to selected agent <br> 
 * 3. Connect button - when pressed the runner closes the currently active connection and connects to selected agent.<br>
 * <br>
 * @author goland
 */
public class RemoteAgentUIComponents {

	private static JButton statusButton;
	private static JComboBox agentsList;
	
	/**
	 * 
	 * @param runner
	 * @return
	 */
	public static JToolBar getToolBar(final TestRunner runner) {
		JToolBar toolBar = SwingUtils.getJToolBarWithBgImage("My Toolbar", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_TOP_TOOLBAR_BG));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		Insets margins = new Insets(4, 2, 4, 2);
		statusButton = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
		statusButton.setToolTipText("Status: Okay");
		statusButton.setMargin(margins);
		
		agentsList = new JComboBox();
		SwingUtils.setToolBarComboBoxLAF(agentsList);
		agentsList.setBorder(BorderFactory.createEmptyBorder(5, 0, 1, 0));
		agentsList.setPreferredSize(new Dimension(200,20));
		agentsList.setEditable(true);
		//whenever user changes agent, the icon should become grey.
		//signaling the user that the selected agent is not actually 
		//connected.
		agentsList.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				statusButton.setIcon(ImageCenter
						.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_NOTCONNECTED));				
			}
			
		});
		refreshAgentList();
		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.add(statusButton);
		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.add(agentsList);
		toolBar.add(ConnectToAgentAction.getInstance());
		return toolBar;
	}
	
	/**
	 * Reads agents list from jsystem.properties and updates drop down list.
	 */
	public static void refreshAgentList()  {
		agentsList.setModel(new DefaultComboBoxModel(AgentList.getAgentsList().toArray()));
		agentsList.repaint();
	}
	
	/**
	 * Returns the agent that is currently selected in the dropdown list.
	 */
	public static String getSelectedAgent()  {
		return ""+agentsList.getSelectedItem();
	}

	
	/**
	 * Updates status button.
	 */
	public static void setConnectionStatus(String  notificationType) {
		if (statusButton == null){
			return;
		}
		if (notificationType.equals(JMXConnectionNotification.CLOSED) ||
				notificationType.equals(JMXConnectionNotification.FAILED)){
			statusButton.setIcon(ImageCenter
					.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM));
			enableAgentList(true);
		}
		if (notificationType.equals(JMXConnectionNotification.OPENED)){
			statusButton.setIcon(ImageCenter
					.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK));
			
		}
	}
	
	/**
	 * Checks in jsystem.properties whether the user 
     * is allowed to change agent during test execution. 
     * If so, returns thus leaving the agent combo and connect button
     * in their default state which is enabled. Otherwise, enables/disables
     * components according to <code>enable</code>
	 */
	public static void checkJSystemPropsAndEnableAgentList(boolean enable){
		String allowChangeDuringRun = JSystemProperties.getInstance().getPreference(FrameworkOptions.CHANGE_AGENT_DURING_RUN);
		if (!StringUtils.isEmpty(allowChangeDuringRun) && "true".equals(allowChangeDuringRun)){
			return;
		}
		enableAgentList(enable);
	}
	
	/**
	 * Enables/disable agent list combo and connect button
	 */
	private static void enableAgentList(boolean enable){
		agentsList.setEnabled(enable);
		ConnectToAgentAction.getInstance().setEnabled(enable);
	}
}