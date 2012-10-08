/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * @version 1.0 11/09/98
 */
public class RowRenderer implements TableCellRenderer {
	
	protected Hashtable<Integer, TableCellRenderer> renderers;

	protected TableCellRenderer renderer, defaultRenderer;

	public RowRenderer() {
		renderers = new Hashtable<Integer, TableCellRenderer>();
		defaultRenderer = new DefaultTableCellRenderer();
	}

	public void add(int row, TableCellRenderer renderer) {
		renderers.put(Integer.valueOf(row), renderer);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		renderer = (TableCellRenderer) renderers.get(Integer.valueOf(row));
		if (renderer == null) {
			renderer = defaultRenderer;
		}
		return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
