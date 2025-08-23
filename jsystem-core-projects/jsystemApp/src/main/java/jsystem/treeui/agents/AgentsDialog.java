/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.agents;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.agent.server.RunnerEngine.ConnectionState;
import jsystem.treeui.RemoteAgentUIComponents;
import jsystem.treeui.actionItems.IgnisAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.client.RemoteAgentClient;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.reporter.ReportersPanel;
import jsystem.treeui.utilities.ProgressBarPanel;
import jsystem.treeui.utilities.Task;
import jsystem.utils.ProgressNotifier;
import jsystem.utils.StringUtils;

/**
 * Agents management dialog.
 * 
 * @author goland
 */
public class AgentsDialog extends JDialog {

	private final String NEW_AGENT_TEXT = "enter url here";

	private static final ImageIcon OKAY_IMAGE = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK);
	private static final ImageIcon ERROR_IMAGE = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM);
	private static final ImageIcon NA_IMAGE = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_NOTCONNECTED);

	private static final long serialVersionUID = 1L;
	private AgentListTableModel agentsListTableModel;
	private List<AgentListDialogAction> actions = new ArrayList<AgentListDialogAction>();
	private int editableRow = -1;
	private JTable table;
	private Set<String> selectedHosts;
	private AgentsConnectionStatusListener connectionStatusListener;

	public AgentsDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public AgentsDialog() {
		super();
	}

	public static void showAgentsDialog() throws Exception {
		AgentsDialog dialog = new AgentsDialog();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.initDialog();
		dialog.setVisible(true);
	}

	public void initDialog() throws Exception {
		setTitle("Agents List");
		connectionStatusListener = new AgentsConnectionStatusListener();
		SwingUtilities.windowForComponent(this).setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));
		JPanel mainPanel = new JPanel(new BorderLayout());
		selectedHosts = new HashSet<String>();
		String[] participatingHosts = DistributedExecutionHelper.getParticipatingHosts();
		for (String s : participatingHosts) {
			selectedHosts.add(s);
		}
		buildAgentsListTableModel();
		table = new JTable(agentsListTableModel);
		table.setRowSelectionAllowed(true);
		table.getColumnModel().getColumn(0).setCellRenderer(new AgentStatusCellRenderer());
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane tablescroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainPanel.add(tablescroll, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		AgentListDialogAction a = new AddAgentAction(table);
		buttonsPanel.add(new JButton(a));
		a = new RemoveAgentAction(table);
		actions.add(a);
		buttonsPanel.add(new JButton(a));

		a = new SynchronizeAction(table);
		actions.add(a);
		buttonsPanel.add(new JButton(a));

		a = new RefreshAgentAction(table);
		actions.add(a);
		buttonsPanel.add(new JButton(a));

		buttonsPanel.add(new JButton(new CloseAction(this)));
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		setContentPane(mainPanel);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 4, screenHeight / 5);
		enableActions(false);
		table.getSelectionModel().addListSelectionListener(new AgentsTableListSelectionListener());
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getTableHeader().setReorderingAllowed(false);
		pack();
		addWindowListener(new AgentDialogListener());
	}

	private void buildAgentsListTableModel() throws Exception {
		Vector<Vector<Object>> model = new Vector<Vector<Object>>();
		RunnerEngine[] clients = JSystemAgentClientsPool.getClients(null);
		for (RunnerEngine client : clients) {
			Vector<Object> clientRow = getJSystemAgentDataVector(client, false);
			model.add(clientRow);
		}
		Vector<String> columns = new Vector<String>();
		columns.add("Status");
		columns.add("URL");
		columns.add("Project");
		columns.add("Scenario");
		columns.add("State");
		columns.add("Version");
		agentsListTableModel = new AgentListTableModel(model, columns);
		agentsListTableModel.addTableModelListener(new AgentsTableModelListener());
	}

	private Vector<Object> getJSystemAgentDataVector(RunnerEngine client, boolean refresh) throws Exception {
		Vector<Object> data = new Vector<Object>();
		if (!client.getConnectionState().equals(ConnectionState.connected) && refresh) {
			try {
				client.init();
			} catch (Throwable e) {

			}
		} else {
			client.removeListener(connectionStatusListener);
			client.addListener(connectionStatusListener);
		}
		if (!client.getConnectionState().equals(ConnectionState.connected)) {
			data.add(ERROR_IMAGE);
			data.add(client.getId());
		} else {
			data.add(OKAY_IMAGE);
			data.add(client.getId());
			data.add(client.getCurrentProjectName());
			data.add(client.getActiveScenario());
			data.add(client.getEngineExecutionState().toString());
			data.add(client.getEngineVersion());
		}
		return data;
	}

	private void enableActions(boolean enabled) {
		for (AgentListDialogAction a : actions) {
			a.setEnabled(enabled);
		}
	}

	private RunnerEngine[] selectedRowsToClients() {
		int[] selectedRows = table.getSelectedRows();
		RunnerEngine[] clients = new RunnerEngine[selectedRows.length];
		for (int i = 0; i < selectedRows.length; i++) {
			String url = table.getValueAt(selectedRows[i], 1).toString();
			clients[i] = JSystemAgentClientsPool.getClient(url);
		}
		return clients;
	}

	private ProgressNotifier openProgressDialog(String title) {
		Point p = getLocation();
		p.setLocation(p.x + 10, p.y + 50);
		ProgressNotifier notifier = ProgressBarPanel.createAndShowProgressPanel(title, p, false, this);
		return notifier;
	}

	private void refreshUrl(String url, ProgressNotifier notifier, int progress) {
		RunnerEngine client = JSystemAgentClientsPool.getClient(url);
		int index = agentsListTableModel.getAgentRow(url);
		try {
			Vector<Object> v = getJSystemAgentDataVector(client, true);
			agentsListTableModel.removeRow(index);
			agentsListTableModel.insertRow(index, v);
			table.addRowSelectionInterval(index, index);
			if (notifier != null) {
				notifier.notifyProgress("Finished  " + url + " refresh", progress);
			}
		} catch (Exception e1) {
			agentsListTableModel.setValueAt(ERROR_IMAGE, index, 0);
			if (notifier != null) {
				notifier.notifyProgress("Failed refreshing  " + url + " " + e1.getMessage(), progress);
			}
		}
	}

	class AgentListTableModel extends DefaultTableModel {
		AgentListTableModel(Vector<Vector<Object>> model, Vector<String> columns) {
			super(model, columns);
		}

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return (row == editableRow) && (column == 1);
		};

		public int getAgentRow(String agentUrl) {
			for (int i = 0; i < getRowCount(); i++) {
				String url = (String) getValueAt(i, 1);
				if (url.equals(agentUrl)) {
					return i;
				}
			}
			return -1;
		}

		public void removeRow(String urlToRemove) {
			for (int i = 0; i < getRowCount(); i++) {
				String url = (String) getValueAt(i, 1);
				if (urlToRemove.equals(url)) {
					removeRow(i);
				}
			}
		}

		public void refreshAgent(String url) {
			int row = getAgentRow(url);
			fireTableRowsUpdated(row, row);
		}

	}

	class AgentStatusCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String url = (String) agentsListTableModel.getValueAt(row, 1);
			RunnerEngine engine = JSystemAgentClientsPool.getClient(url);
			if (engine == null) {
				label.setIcon(NA_IMAGE);
			} else {
				if (engine.getConnectionState().equals(ConnectionState.connected)) {
					label.setIcon(OKAY_IMAGE);
				} else if (engine.getConnectionState().equals(ConnectionState.disconnected)) {
					label.setIcon(ERROR_IMAGE);
				} else {
					label.setIcon(NA_IMAGE);
				}
			}
			if (selectedHosts.contains(url)) {
				label.setText("**");
			} else {
				label.setText("");
			}
			return label;
		}
	}

	/**
	 * Actions and events
	 */

	/**
	 * Rows selected. Operations buttons availability is updated according to number of selected rows.
	 */
	class AgentsTableListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int count = table.getSelectedRowCount();
			enableActions(count > 0);
		}
	}

	/**
	 * Table changed. table is allowed to change only after user has pressed on the add agent button. The event is invoked in the following
	 * cases: once after row is added, second time after user edits the url, in addition the event is called when row is removed. In case of
	 * remove, and add, ignoring event.
	 */
	class AgentsTableModelListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			if (e.getType() != TableModelEvent.UPDATE) {
				return;
			}
			int column = table.getSelectedColumn();
			int row = table.getSelectedRow();
			if (column != -1 && row != -1) {
				final String data = table.getValueAt(row, column).toString();
				if (!NEW_AGENT_TEXT.equals(data)) {
					try {
						RunnerEngine engine = JSystemAgentClientsPool.getClient(data);
						if (engine != null) {
							ErrorPanel.showErrorDialog("Agent " + data + " already exists in the agents list", "", ErrorLevel.Info);
							agentsListTableModel.setValueAt(NEW_AGENT_TEXT, row, column);
							return;
						}
						JSystemAgentClientsPool.addClient(data, true);
						editableRow = -1;
					} catch (Exception e1) {
						ErrorPanel.showErrorDialog("Failed adding " + data, e1, ErrorLevel.Error);
					} finally {
						ReportersPanel.addAgent(JSystemAgentClientsPool.getClient(data));
						RemoteAgentUIComponents.refreshAgentList();

					}
				}
			}
		}

	}

	/**
	 * Synchronize button is pressed. Synchronizing agent project with local project.
	 */
	class SynchronizeAction extends AgentListDialogAction {
		private static final long serialVersionUID = 1L;

		SynchronizeAction(JTable table) {
			super(table);
			putValue(Action.SHORT_DESCRIPTION, "Synchronize agent project with local project");
			putValue(Action.NAME, "Synchronize");
			putValue(Action.ACTION_COMMAND_KEY, "synchronize");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ScenariosManager.isDirty()) {
				try {
					SaveScenarioAction.getInstance().saveCurrentScenario();
				} catch (Exception e1) {
					ErrorPanel.showErrorDialog("Failed Saving Scenario", e1, ErrorLevel.Error);
				}
			}
			final RunnerEngine[] clients = selectedRowsToClients();
			ProgressNotifier notifier = openProgressDialog("Synchronizing agents");
			try {
				Task syncTask = new Task(notifier) {
					@Override
					protected Void doInBackground() throws Exception {
						JSystemAgentClient[] jsysClients = new JSystemAgentClient[clients.length];
						int j = 0;
						for (RunnerEngine client : clients) {
							jsysClients[j++] = (JSystemAgentClient) client;
						}
						RemoteAgentClient.syncAgentsWithLocalProject(jsysClients, true, this, false);
						int progress = 90;
						for (RunnerEngine client : clients) {
							if (client.getConnectionState() == ConnectionState.connected) {
								refreshUrl(client.getId(), this, progress += 3);
							} else {
								notifyProgress("no connection to  " + client.getId() + " skipping refresh", progress += 3);
							}
						}
						return null;
					}
				};
				syncTask.execute();
			} catch (Throwable e1) {
				ErrorPanel.showErrorDialog("Failed synchronizing agents", StringUtils.getStackTrace(e1), ErrorLevel.Error);
			}

		}
	}

	/**
	 * Refresh button is pressed. Fetching agent information;
	 */
	class RefreshAgentAction extends AgentListDialogAction {
		private static final long serialVersionUID = 1L;

		RefreshAgentAction(JTable table) {
			super(table);
			putValue(Action.NAME, "Refresh");
			putValue(Action.SHORT_DESCRIPTION, "Refresh agent information");
			putValue(Action.ACTION_COMMAND_KEY, "refresh");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final int[] selectedRows = table.getSelectedRows();
			ProgressNotifier notifier = openProgressDialog("Refreshing Agents");
			Task refreshTask = new Task(notifier) {
				@Override
				protected Void doInBackground() throws Exception {
					int i = 0;
					for (int row : selectedRows) {
						String url = table.getValueAt(row, 1).toString();
						refreshUrl(url, this, ++i * (100 / selectedRows.length));
					}
					return null;
				}
			};
			refreshTask.execute();
		}
	}

	/**
	 * Add agent is pressed. Adding a line to the agents table and allowing the user to edit the line.
	 */
	class AddAgentAction extends AgentListDialogAction {
		private static final long serialVersionUID = 1L;

		AddAgentAction(JTable table) {
			super(table);
			putValue(Action.SHORT_DESCRIPTION, "Add remote agent to agents list");
			putValue(Action.NAME, "Add");
			putValue(Action.ACTION_COMMAND_KEY, "add");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int count = agentsListTableModel.getRowCount();
			if (count > 0 && agentsListTableModel.getValueAt(count - 1, 1).equals(NEW_AGENT_TEXT)) {
				return;
			}

			Vector<String> v = new Vector<String>();
			v.add("");
			v.add(NEW_AGENT_TEXT);
			agentsListTableModel.addRow(v);
			editableRow = agentsListTableModel.getRowCount() - 1;
		}
	}

	/**
	 * Removing agent from agents list
	 */
	class RemoveAgentAction extends AgentListDialogAction {
		private static final long serialVersionUID = 1L;

		RemoveAgentAction(JTable table) {
			super(table);
			putValue(Action.SHORT_DESCRIPTION, "Synchronize agent project with local project");
			putValue(Action.NAME, "Remove");
			putValue(Action.ACTION_COMMAND_KEY, "remove");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows = agentTable.getSelectedRows();
			String[] urls = new String[selectedRows.length];
			for (int i = 0; i < selectedRows.length; i++) {
				String url = (String) agentTable.getValueAt(selectedRows[i], 1);
				urls[i] = url;
			}
			for (String url : urls) {
				JSystemAgentClientsPool.removeClient(url);
				agentsListTableModel.removeRow(url);
				ReportersPanel.removeAgent(url);
				RemoteAgentUIComponents.refreshAgentList();
			}
		}
	}

	/**
	 * Dialog is closed
	 */
	class CloseAction extends IgnisAction {
		private static final long serialVersionUID = 1L;
		private JDialog dialog;

		CloseAction(JDialog dialog) {
			putValue(Action.SHORT_DESCRIPTION, "Synchronize agent project with local project");
			putValue(Action.NAME, "Close");
			putValue(Action.ACTION_COMMAND_KEY, "close");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	/**
	 */
	abstract class AgentListDialogAction extends IgnisAction {
		private static final long serialVersionUID = 1L;
		protected JTable agentTable;

		protected AgentListDialogAction(JTable agentsTable) {
			this.agentTable = agentsTable;
		}
	}

	public class AgentsConnectionStatusListener implements NotificationListener {

		@Override
		public void handleNotification(Notification notification, Object handback) {
			if (!(notification instanceof JMXConnectionNotification)) {
				return;
			}
			table.repaint();
		}

	}

	public class AgentDialogListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
			for (RunnerEngine e : engines) {
				e.removeListener(connectionStatusListener);
			}
		}

		@Override
		public void windowClosing(WindowEvent arg0) {

		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			windowClosed(arg0);
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
