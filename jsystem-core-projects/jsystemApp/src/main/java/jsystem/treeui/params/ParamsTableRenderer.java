/*
 * Created on Dec 15, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import jsystem.framework.scenario.Parameter;

/**
 * @author guy.arieli
 * 
 */
public class ParamsTableRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int COLUMN_NAME = 0;

	public static final int COLUMN_DESCRIPTION = 1;

	public static final int COLUMN_TYPE = 2;

	public static final int COLUMN_VALUE = 3;

	private Color bColor;

	Component comp = null;

	public ParamsTableRenderer() {

	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		ParamsTableModel model = (ParamsTableModel) table.getModel();
		Parameter parameter = model.getParam(row);
		Object v = model.getValueAt(row, column);
		String s = null;
		if(v != null){
			s = v.toString();
		}
		setText(s);

		setBorder(new LineBorder(Color.white, 1));

		if(parameter.isMandatory()){
			if ((row % 2) == 0) {
				bColor = new Color(0xff, 0x0d, 0x00);
			} else {
				bColor = Color.RED;
			}
		} else {
			if ((row % 2) == 0) {
				bColor = new Color(0xf7, 0xfd, 0xff);
			} else {
				bColor = Color.white;
			}
		}
		


		setEnabled(parameter.isEditable());
		return this;
	}

	public void paint(Graphics g) {
		g.setColor(bColor);

		// Draw a rectangle in the background of the cell
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		super.paint(g);
	}
	
	public void setColor(Color color){
		this.bColor = color;
	}
}
