/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jsystem.runner.agent.clients.JSystemAgentClient;
import jsystem.runner.agent.server.RunnerEngine.ConnectionState;
import jsystem.treeui.actionItems.IgnisAction;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

/**
 * Agents chooser for the parameters panel.
 * 
 * @author goland
 */
public class AgentsSelectionDialog extends JDialog {
	private static final ImageIcon OKAY = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_OK);
	private static final ImageIcon ERROR = ImageCenter.getInstance().getImage(ImageCenter.ICON_REMOTEAGENT_PROBLEM);

	private static final long serialVersionUID = 1L;
	private AgentSelectTableModel agentsListTableModel;
	private JTable table;
	private Set<String> selectedUrlsSet;
	private boolean isOkay = false;

	public static void showAgentsDialog(String[] selectedUrls) throws Exception {
		AgentsSelectionDialog dialog = new AgentsSelectionDialog();
		dialog.initDialog(selectedUrls);
	}

	public void initDialog(String[] selectedUrls) throws Exception {
		setTitle("Select Agents");
		((Frame) this.getOwner()).setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));
		setModalityType(ModalityType.APPLICATION_MODAL);
		selectedUrlsSet = StringUtils.stringArrayToSet(selectedUrls);
		JPanel mainPanel = new JPanel(new BorderLayout());
		buildAgentsListTableModel();
		table = new JTable(agentsListTableModel);
		table.setRowSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getTableHeader().setReorderingAllowed(false);

		JScrollPane tablescroll = new JScrollPane(table);
		mainPanel.add(tablescroll, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(new SaveAction(this)));
		buttonsPanel.add(new JButton(new CloseAction(this)));
		JButton selectUnSelect = new JButton(new SelectUnSelectAction(table));
		selectUnSelect.setPreferredSize(new Dimension(120, selectUnSelect.getPreferredSize().height));
		buttonsPanel.add(selectUnSelect);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		setContentPane(mainPanel);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 4, screenHeight / 5);
		setPreferredSize(new Dimension(350, 300));
		pack();
		setVisible(true);
	}

	private void buildAgentsListTableModel() throws Exception {
		Vector<Vector<Object>> model = new Vector<Vector<Object>>();
		JSystemAgentClient[] clients = (JSystemAgentClient[]) JSystemAgentClientsPool.getClients(null);
		for (JSystemAgentClient client : clients) {
			Vector<Object> clientRow = getJSystemAgentDataVector(client);
			model.add(clientRow);
		}
		Vector<String> columns = new Vector<String>();
		columns.add("");
		columns.add("Status");
		columns.add("URL");
		agentsListTableModel = new AgentSelectTableModel(model, columns);
	}

	private Vector<Object> getJSystemAgentDataVector(JSystemAgentClient client) throws Exception {
		Vector<Object> data = new Vector<Object>();
		if (selectedUrlsSet.contains(client.getId())) {
			data.add(Boolean.TRUE);
		} else {
			data.add(Boolean.FALSE);
		}
		if (!client.getConnectionState().equals(ConnectionState.connected)) {
			data.add(ERROR);
		} else {
			data.add(OKAY);
		}
		data.add(client.getId());
		return data;
	}

	public boolean isOkay() {
		return isOkay;
	}

	public String[] getSelectedUrls() {
		Set<String> selected = new HashSet<String>();
		int numberOfRows = agentsListTableModel.getRowCount();
		for (int i = 0; i < numberOfRows; i++) {
			if (agentsListTableModel.getValueAt(i, 0).equals(Boolean.TRUE)) {
				selected.add((String) agentsListTableModel.getValueAt(i, 2));
			}
		}
		return selected.toArray(new String[0]);
	}

	class AgentSelectTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		AgentSelectTableModel(Vector<Vector<Object>> model, Vector<String> columns) {
			super(model, columns);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		};

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return Boolean.class;
			}
			if (columnIndex == 1) {
				return ImageIcon.class;
			}
			return super.getColumnClass(columnIndex);
		}
	}

	/**
	 * Actions and events
	 */

	/**
	 * Refresh button is pressed. Fetching agent information;
	 */
	class SelectUnSelectAction extends IgnisAction {
		private static final long serialVersionUID = 1L;
		private static final String SELECT_ALL = "Select All";
		private static final String UNSELECT_ALL = "Unselect All";

		SelectUnSelectAction(JTable table) {
			super();
			putValue(Action.NAME, SELECT_ALL);
			putValue(Action.SHORT_DESCRIPTION, "select all agents");
			putValue(Action.ACTION_COMMAND_KEY, "selectunselect");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String buttonText = (String) getValue(Action.NAME);
			Boolean select = false;
			if (SELECT_ALL.equals(buttonText)) {
				select = true;
			}
			int numberOfRows = agentsListTableModel.getRowCount();
			for (int i = 0; i < numberOfRows; i++) {
				agentsListTableModel.setValueAt(select, i, 0);
			}
			putValue(Action.NAME, select ? UNSELECT_ALL : SELECT_ALL);
		}
	}

	/**
	 * Dialog is closed
	 */
	class SaveAction extends IgnisAction {
		private static final long serialVersionUID = 1L;
		private JDialog dialog;

		SaveAction(JDialog dialog) {
			putValue(Action.SHORT_DESCRIPTION, "Save user selection");
			putValue(Action.NAME, "Save");
			putValue(Action.ACTION_COMMAND_KEY, "save");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			isOkay = true;
			dialog.setVisible(false);
			dialog.dispose();
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
			isOkay = false;
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	public static void main(String[] args) throws Exception {
		JSystemAgentClientsPool.initPoolFromRepositoryFile();
		// AgentsSelectionDialog dialog = new AgentsSelectionDialog();
		AgentsSelectionDialog.showAgentsDialog(new String[0]);

	}
}
