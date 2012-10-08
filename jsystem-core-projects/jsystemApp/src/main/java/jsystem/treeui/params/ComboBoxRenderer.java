/*
 * Created on Dec 16, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.treeui.params;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author guy.arieli
 * 
 */

public class ComboBoxRenderer implements TableCellRenderer {
	JComboBox cbox;

	public ComboBoxRenderer(JComboBox cbox) {
		this.cbox = cbox;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		cbox.setForeground(table.getForeground());
		cbox.setBackground(table.getBackground());
		cbox.setSelectedItem(value);
		return cbox;
	}
}
