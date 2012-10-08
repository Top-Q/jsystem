/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

public class JSystemPropertiesTableRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private Color bColor;
	Component comp = null;

	public JSystemPropertiesTableRenderer() {

	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JSystemPropertiesTableModel model = (JSystemPropertiesTableModel) table.getModel();
		Object v = model.getValueAt(row, column);
		String s = null;
		if(v != null){
			s = v.toString();
		}
		setText(s);

		setBorder(new LineBorder(Color.white, 1));

		if ((row % 2) == 0) {
			bColor = new Color(0xf7, 0xfd, 0xff);
		} else {
			bColor = Color.white;
		}
		
		// if cell is selected, set background color to default cell selection background color
	     if (isSelected) {
	    	 bColor = new Color(0x99, 0xcc, 0xff);
	     }

		return this;
	}

	public void paint(Graphics g) {
		g.setColor(bColor);

		// Draw a rectangle in the background of the cell
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		super.paint(g);
	}
}
