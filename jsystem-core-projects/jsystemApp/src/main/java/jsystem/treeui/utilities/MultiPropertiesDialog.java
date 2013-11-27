package jsystem.treeui.utilities;

import jsystem.extensions.paramproviders.BeanCellEditorModel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.SwingUtils;
import jsystem.utils.beans.BeanElement;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is the dialog that appears when using a building block parameter of an
 * array type To use the array parameter in the JSystem gui the user must add to
 * the relevant property setter the annotation
 * 
 * @UseProvider(provider = ObjectArrayParameterProvider.class)
 * 
 *                       In case the test is part of a scenario that is
 *                       "edit only locally" the MultiPropertiesDialog should be
 *                       created with the flag enabled=false. This flag disable
 *                       any user editing operation.
 * 
 */
public class MultiPropertiesDialog extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1417293276325153499L;

	private JTable table;

	private JButton addButton;

	private JButton removeButton;

	private JButton upButton;

	private JButton downButton;

	private JButton okButton;

	private JButton cancelButton;

	private boolean approved = false;

	private ArrayList<LinkedHashMap<String, String>> mapValues;

	private int currentSelectedRow = -1;

	private BeanCellEditorModel model;

	private boolean isEnabled = true;

	private LinkedHashMap<String, String> referanceMap;

	private GenericCellEditor gce;

	private List<ParameterProviderListener> listenersList = new ArrayList<ParameterProviderListener>();

	public MultiPropertiesDialog(ArrayList<LinkedHashMap<String, String>> mapValues, String title,
			ArrayList<BeanElement> beanElements, LinkedHashMap<String, String> referanceMap, BeanCellEditorModel model,
			boolean isEditable) {
		this.mapValues = mapValues;
		this.referanceMap = referanceMap;
		this.model = model;
		if (this.model == null) {
			this.model = new BeanCellEditorModel(beanElements, mapValues);
		}
		this.isEnabled = isEditable;
		initComponents();
	}

	private void initComponents() {
		table = new JTable();
		table.getSelectionModel().addListSelectionListener(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form");
		table.setModel(model);
		table.setName("Table");

		gce = new GenericCellEditor(model);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellEditor(gce);
		}

		table.setRowHeight(20);

		getContentPane().add(
				SwingUtils.getJScrollPaneWithWaterMark(
						ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TEST_TREE_BG), table),
				BorderLayout.CENTER);

		JPanel okCancelPanel = SwingUtils.getJPannelWithBgImage(
				ImageCenter.getInstance().getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG), 0);

		okCancelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		addButton = new JButton("Add...");
		addButton.addActionListener(this);
		addButton.setEnabled(isEnabled);
		okCancelPanel.add(addButton);

		removeButton = new JButton("Remove");
		removeButton.setEnabled(isEnabled);
		removeButton.addActionListener(this);
		okCancelPanel.add(removeButton);

		upButton = new JButton("Up");
		upButton.setEnabled(isEnabled);
		upButton.addActionListener(this);
		okCancelPanel.add(upButton);

		downButton = new JButton("Down");
		downButton.setEnabled(isEnabled);
		downButton.addActionListener(this);
		okCancelPanel.add(downButton);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		okCancelPanel.add(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		okCancelPanel.add(cancelButton);

		getContentPane().add(okCancelPanel, BorderLayout.SOUTH);

		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setSelectionForeground(Color.BLACK);

		table.setBackground(new Color(0xf6, 0xf6, 0xf6));
		JTableHeader treeTableHeader = table.getTableHeader();
		treeTableHeader.setBackground(new Color(0xe1, 0xe4, 0xe6));
		table.setEnabled(isEnabled);
		selectFirstRow();
		pack();
	}

	private void selectFirstRow() {
		if (table.getRowCount() > 0) {
			table.getSelectionModel().setSelectionInterval(0, 0);
			currentSelectedRow = 0;
		}
	}

	private void selectRow(int row) {
		if (table.getRowCount() >= row && row >= 0) {
			table.getSelectionModel().setSelectionInterval(row, row);
			currentSelectedRow = row;
		}
	}

	private void selectLastRow() {
		int lastRowIndex = table.getRowCount() - 1;
		if (lastRowIndex >= 0) {
			table.getSelectionModel().setSelectionInterval(lastRowIndex, lastRowIndex);
			currentSelectedRow = lastRowIndex;
		}
	}

	public boolean showAndWaitForApprove() throws InterruptedException {
		setVisible(true);
		return approved;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (ParameterProviderListener listener : listenersList) {
			listener.actionPerformed(this, e);
		}
		gce.stopCellEditing();
		if (okButton.equals(e.getSource())) {
			approved = true;
			dispose();
		} else if (cancelButton.equals(e.getSource())) {
			approved = false;
			dispose();
		} else if (removeButton.equals(e.getSource())) {
			if (currentSelectedRow != -1) {
				mapValues.remove(currentSelectedRow);
				model.fireTableRowsDeleted(currentSelectedRow, currentSelectedRow);
				// model.fireTableStructureChanged();
				selectFirstRow();
			}
		} else if (addButton.equals(e.getSource())) {
			mapValues.add(new LinkedHashMap<String, String>(referanceMap));
			// model.fireTableStructureChanged();
			int lastRowIndex = mapValues.size() - 1;
			model.fireTableRowsInserted(lastRowIndex, lastRowIndex);
			selectLastRow();
		} else if (upButton.equals(e.getSource())) {
			mapValues.add(currentSelectedRow - 1, mapValues.remove(currentSelectedRow));
			int previosSelection = currentSelectedRow;
			model.fireTableStructureChanged();
			selectRow(previosSelection - 1);
		} else if (downButton.equals(e.getSource())) {
			mapValues.add(currentSelectedRow + 1, mapValues.remove(currentSelectedRow));
			int previosSelection = currentSelectedRow;
			model.fireTableStructureChanged();
			selectRow(previosSelection + 1);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		for (ParameterProviderListener listener : listenersList) {
			listener.actionPerformed(this, e);
		}
		if (e != null) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			currentSelectedRow = lsm.getLeadSelectionIndex();
		}
		updateButtonStatus();
	}

	public void addListener(ParameterProviderListener listener) {
		if (null == listenersList) {
			listenersList = new ArrayList<ParameterProviderListener>();
		}
		listenersList.add(listener);
	}

	private void updateButtonStatus() {
		if (currentSelectedRow == -1) {
			removeButton.setEnabled(false);
		} else {
			removeButton.setEnabled(isEnabled);
		}
		if (currentSelectedRow == 0 || currentSelectedRow == -1) {
			upButton.setEnabled(false);
		} else {
			upButton.setEnabled(isEnabled);
		}
		if (currentSelectedRow >= (mapValues.size() - 1) || currentSelectedRow == -1) {
			downButton.setEnabled(false);
		} else {
			downButton.setEnabled(isEnabled);
		}
	}

	public void setListeners(List<ParameterProviderListener> listenersList) {
		this.listenersList = listenersList;

	}

	public JTable getTable() {
		return table;
	}

}
