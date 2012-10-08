/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import jsystem.treeui.images.ImageCenter;

/**
 * NodeRenderer class This class implements the look of the node in the tree
 */
public class ElementRenderer implements TreeCellRenderer {
//	private static Logger log = Logger.getLogger(ElementRenderer.class.getName());

	protected boolean bSelected = false;

	protected boolean bFocus = false;

	public ElementRenderer() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
		TreeJPanel panel = new TreeJPanel(leaf);
		panel.setEnabled(tree.isEnabled());

		bSelected = isSelected;
		bFocus = hasFocus;

		ElementNode node = (ElementNode) value;

		panel.setBackground(Color.white);

		panel.label.setFont(tree.getFont());
		panel.label.setText(stringValue);
		
		/**
		 * manage the color's and the link's in the "publisher"  
		 */
		switch (node.getStatus()) {
		case ElementNode.TEST_FAIL:
			panel.label.setColor(Color.red);
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_ERR));
			break;
		case ElementNode.TEST_PASS:
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_OK));
			panel.label.setColor(Color.green.darker());
			break;
		case ElementNode.TEST_WARNING:
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_WARNING));

		// orange - #ff6600
			panel.label.setColor(Color.decode("#ff6600"));
			break;
		}

		if (value instanceof TestStepNode) {
			TestStepNode tsn = (TestStepNode) value;
			panel.setToolTipText(tsn.getMessage());
		}

		return panel;
	}

	/* This class will hold the text and tree table */
	class TreeLabel extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3375831941429179054L;

		boolean hasFocus = false;

		Color color = null;

		TreeLabel() {
		}

		public void setColor(Color color) {
			this.color = color;
			setForeground(color);
		}

		public void paint(Graphics g) {
			String str = null;
			Icon currentI = null;

			if ((str = getText()) != null) {

				if (str.length() > 0) {

					Color bColor;
					currentI = getIcon();

					// Set the correct foreground color
					// setForeground(Color.black);

					// Set the correct background color 
					bColor = bSelected ? Color.lightGray : Color.white;
			
					
					if (!bFocus) {
						bColor = bSelected ? Color.lightGray : Color.white;
					}

					g.setColor(bColor);

					Dimension d = getPreferredSize();
					int imageOffset = 0;
					currentI = getIcon();

					if (currentI != null) {
						imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
					}

					g.fillRect(imageOffset, 0, d.width - imageOffset, d.height);

					super.paint(g);
				}
			}
		}
	}

	class TreeJPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3760686174925273622L;

		public TreeLabel label;

		boolean isLeaf;

		public TreeJPanel(boolean isLeaf) {
			this.isLeaf = isLeaf;
			label = new TreeLabel();
			add(label);
		}

		public Dimension getPreferredSize() {
			return label.getPreferredSize();
			/*
			 * if (!isLeaf){ return label.getPreferredSize(); } else { Dimension
			 * d_check = check.getPreferredSize(); Dimension d_label =
			 * label.getPreferredSize(); return new Dimension(d_check.width +
			 * d_label.width, (d_check.height < d_label.height ? d_label.height :
			 * d_check.height)); }
			 */
		}

		public void doLayout() {
			if (!isLeaf) {
				Dimension d_label = label.getPreferredSize();

				int y_label = 0;

				label.setLocation(0, y_label);
				label.setBounds(0, y_label, d_label.width, d_label.height);
			} else {
				// Dimension d_check = check.getPreferredSize();
				Dimension d_label = label.getPreferredSize();

				int y_label = 0;

				// check.setLocation(0, y_check);
				// check.setBounds(0, y_check, d_check.width, d_check.height);
				label.setLocation(0, y_label);
				label.setBounds(0, y_label, d_label.width, d_label.height);
			}
		}

	}
}
