/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author goland
 */
public class DateCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	
	private ParametersTableDateTimeChooser chooser;
	public DateCellEditor(ParametersTableDateTimeChooser chooser){
		this.chooser = chooser;
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return chooser;
	}
	public Object getCellEditorValue() {
		return chooser.getDate();
	}	
}
