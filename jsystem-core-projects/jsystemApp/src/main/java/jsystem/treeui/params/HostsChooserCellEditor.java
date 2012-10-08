/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor for the agents chooser field.<br>
 * @author goland
 */
public class HostsChooserCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	private ParametersTableHostChooser chooser;
	public HostsChooserCellEditor(ParametersTableHostChooser chooser){
		this.chooser = chooser;
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return chooser;
	}

	public Object getCellEditorValue() {
		return chooser.getSelectedHosts();
	}	
	
    /**
     * Returns true.
     * @param e  an event object
     * @return true
     */
    public boolean isCellEditable(EventObject e) { 
    	return chooser.isEnabled();
    } 
}
