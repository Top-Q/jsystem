/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import jsystem.framework.common.CommonResources;
import jsystem.treeui.actionItems.IgnisAction;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * MultiOptions chooser for the parameters panel.
 * @author goland
 */
public class OptionsMultiSelectDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private OptionsSelectTableModel optionsListTableModel;
	private JTable table;
	private Set<String> selectedUrlsSet;
	private Object[] fullList;
	private boolean isOkay=false;
	
	public static void showMultiOptionsDialog(String[] fullList,String[] selectedOptions) throws Exception {
		OptionsMultiSelectDialog dialog = new OptionsMultiSelectDialog();		
		dialog.initDialog(fullList,selectedOptions);
	}
	
	public void initDialog(Object[] fullList, String options) {
        initDialog(fullList, options.split(CommonResources.DELIMITER));
    }

	
	public void initDialog(Object[] fullList,String[] options) {
		setTitle("Select Options");
		((Frame) this.getOwner()).setIconImage(ImageCenter.getInstance()
				.getAwtImage(ImageCenter.ICON_JSYSTEM));
		setModal(true);
		this.fullList = fullList;
		selectedUrlsSet = StringUtils.stringArrayToSet(options);
		JPanel mainPanel = new JPanel(new BorderLayout());
		buildOptionsListTableModel();
		table = new JTable(optionsListTableModel);
		table.setRowSelectionAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
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
		setPreferredSize(new Dimension(350,300));
		pack();
		setVisible(true);
	}

	private void buildOptionsListTableModel()  {
        Vector<Vector<Object>> model = new Vector<>();
		for (Object s : fullList) {
			model.add(getOptionDataVector(s.toString()));
		}
		Vector<String> columns = new Vector<String>();
		columns.add("");
		columns.add("Text");
		optionsListTableModel = new OptionsSelectTableModel(model, columns);
	}

	private Vector<Object> getOptionDataVector(String option)  {
		Vector<Object> data = new Vector<Object>();
		if (selectedUrlsSet.contains(option)){
			data.add(Boolean.TRUE);
		}else {
			data.add(Boolean.FALSE);
		}
		data.add(option);
		return data;
	}

	public boolean isOkay(){
		return isOkay;
	}

	public String[] getSelectedOptions(){
		Set<String> selected = new HashSet<String>();
		int numberOfRows = optionsListTableModel.getRowCount();
		for (int i = 0 ; i < numberOfRows;i++){
			if (optionsListTableModel.getValueAt(i, 0).equals(Boolean.TRUE)){
				selected.add((String)optionsListTableModel.getValueAt(i, 1));
			}
		}
		return selected.toArray(new String[0]);
	}

	class OptionsSelectTableModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;
		
        OptionsSelectTableModel(Vector<Vector<Object>> model,Vector<String> columns){
			super(model,columns);
		}
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		};
		@Override
	    public Class<?> getColumnClass(int columnIndex) {
	        if (columnIndex==0){
	            return Boolean.class;
	        }
	        return super.getColumnClass(columnIndex);
	    }
	}

	/**
	 *  Actions and events 
	 */

	/**
	 * Refresh button is pressed.
	 * Fetching agent information;
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
			String buttonText = (String)getValue(Action.NAME);
			Boolean select = false;
			if (SELECT_ALL.equals(buttonText)){
				select = true;
			}
			int numberOfRows = optionsListTableModel.getRowCount();
			for (int i = 0 ; i < numberOfRows;i++){
				optionsListTableModel.setValueAt(select,i,0);
			}
			putValue(Action.NAME,select ? UNSELECT_ALL: SELECT_ALL);
		}
	}

	/**
	 * Dialog is closed
	 */
	class SaveAction extends IgnisAction {
		private static final long serialVersionUID = 1L;
		private JDialog dialog;

		SaveAction(JDialog dialog) {
			putValue(Action.SHORT_DESCRIPTION,
					"Save user selection");
			putValue(Action.NAME, "Okay");
			putValue(Action.ACTION_COMMAND_KEY, "okay");
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
			putValue(Action.NAME, "Cancel");
			putValue(Action.ACTION_COMMAND_KEY, "cancel");
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
		OptionsMultiSelectDialog.showMultiOptionsDialog(new String[]{"1","2"},new String[]{"2"});		
	}
}
