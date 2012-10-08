/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import jsystem.treeui.images.ImageCenter;

public class JSystemOPropertiesTableHeaderRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				setForeground(header.getForeground());
				setBackground(header.getBackground());
				setFont(header.getFont());
			}
		}

		setIcon(ImageCenter.getInstance().getImage(
				ImageCenter.ICON_TABLE_HEADER));

		setForeground(Color.white);

		switch (column) {
		case 0:
			setText("Name");
			break;
		case 1:
			setText("Description");
			break;
		case 2:
			setText("Value");
			break;
		default:
			break;
		}

		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(JLabel.CENTER);
		
		return this;
	}

	public void paint(Graphics g) {
		Dimension size = this.getSize();
		g.drawImage(ImageCenter.getInstance().getAwtImage(
				ImageCenter.ICON_TABLE_HEADER), 0, 0, size.width, size.height,
				this);

		super.paint(g);
	}

}
