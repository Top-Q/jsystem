/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * each row TableCellEditor
 * 
 * @version 1.1 09/09/99
 * @author Nobuo Tamemasa
 */

public class RowEditor implements TableCellEditor {
	protected Hashtable<Integer,TableCellEditor> editors;

	protected TableCellEditor editor, defaultEditor;

	JTable table;

	/**
	 * Constructs a EachRowEditor. create default editor
	 * 
	 * @see TableCellEditor
	 * @see DefaultCellEditor
	 */
	public RowEditor(JTable table) {
		this.table = table;
		editors = new Hashtable<Integer,TableCellEditor>();
		defaultEditor = new DefaultCellEditor(new JTextField());
	}

	/**
	 * @param row
	 *            table row
	 * @param editor
	 *            table cell editor
	 */
	public void setEditorAt(int row, TableCellEditor editor) {
		editors.put(Integer.valueOf(row), editor);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	public Object getCellEditorValue() {
		if (editor == null) {
			return null;
		}
		return editor.getCellEditorValue();
	}

	@Override
	public boolean stopCellEditing() {
		if (editor == null) {
			return true;
		}
		return editor.stopCellEditing();
	}

	@Override
	public void cancelCellEditing() {
		if (editor == null) {
			return;
		}
		editor.cancelCellEditing();
	}

	@Override
	public boolean isCellEditable(EventObject eventObject) {
		if (!(eventObject instanceof MouseEvent)){
			return true;
		}
		selectEditor((MouseEvent) eventObject);
		if (editor == null) {
			return false;
		}
		return editor.isCellEditable(eventObject);
	}

	@Override
	public void addCellEditorListener(CellEditorListener cellEditorListener) {
		if (editor == null) {
			return;
		}
		editor.addCellEditorListener(cellEditorListener);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener cellEditorListener) {
		editor.removeCellEditorListener(cellEditorListener);
	}

	@Override
	public boolean shouldSelectCell(EventObject eventObject) {
		if (editor == null) {
			return false;
		}
		selectEditor((MouseEvent) eventObject);
		return editor.shouldSelectCell(eventObject);
	}

	protected void selectEditor(MouseEvent mouseEvent) {
		int row;
		if (mouseEvent == null) {
			row = table.getSelectionModel().getAnchorSelectionIndex();
		} else {
			row = table.rowAtPoint(mouseEvent.getPoint());
		}
		if ((editor = (TableCellEditor) editors.get(Integer.valueOf(row))) == null) {
			editor = defaultEditor;
		}
	}
}
