/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import jsystem.framework.DataType;
import jsystem.guiMapping.JsystemMapping;

/**
 * 
 *	The model of the JSystemPropertiesDialog JTables
 *
 *@author Dror Voulichman
 */
class JSystemPropertiesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	String[] columnNames= new String[]{
			JsystemMapping.getInstance().getJSystemPropertiesTableColumnPropertyName(),
			JsystemMapping.getInstance().getJSystemPropertiesTableColumnDescription(),
			JsystemMapping.getInstance().getJSystemPropertiesTableColumnValue() };

	String[][] tableData = null;
	int rowCount;
	int columnCount;
	public static final int NAME_COLUMN = 0;
	public static final int DESCRIPTION_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	
	/**
	 * JSystemPropertiesTableModel Constructor
	 * @param properties - A list of properties to be presented in this table
	 */
	public JSystemPropertiesTableModel(Vector<JSystemProperty> properties) {
		rowCount = properties.size();
		columnCount = columnNames.length;
		JSystemProperty property;

		tableData = new String[columnCount][rowCount];
		for (int row = 0; row < rowCount; row++) {
			property = properties.get(row);
			tableData[NAME_COLUMN][row] = property.getStringName();
			tableData[DESCRIPTION_COLUMN][row] = property.getDescription();
			tableData[VALUE_COLUMN][row] = property.getValue();
		}
	}

	// Required implementations for AbstractTableModel Interface
	public int getColumnCount() {
		return columnCount;
	}

	public String getColumnName(int col) {
		return (columnNames[col]);
	}

	public Class<?> getColumnClass(int col) {
		Class<?> cls = super.getColumnClass(col);
		if (VALUE_COLUMN == col) {
			cls = DataType.class;
		}
		return (cls);
	}

	public int getRowCount() {
		return rowCount;
	}

	public Object getValueAt(int row, int col) {
		return (tableData[col][row]);
	}

	public boolean isCellEditable(final int row, final int col) {
		boolean editable = false;

		if (col == VALUE_COLUMN) {
			editable = true;
		}
		return (editable);
	}

	public void setValueAt(Object value, int row, int col) {
		tableData[col][row] = (String) value;
		fireTableCellUpdated(row, col);
	}
}

