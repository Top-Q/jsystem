/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;

public class UserDefinedCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 2843107110677929461L;
	private ParameterTableUserDefine userDefined;
	
	public UserDefinedCellEditor(ParameterTableUserDefine userDefined){
		this.userDefined = userDefined;
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return userDefined;
	}

	public Object getCellEditorValue() {
		try {
			return userDefined.getField();
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Fail the get value", e, ErrorLevel.Warning);
			return null;
		}
	}	
}
