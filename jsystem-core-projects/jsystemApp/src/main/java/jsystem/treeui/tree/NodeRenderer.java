/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import jsystem.framework.scripts.ScriptEngine;
import jsystem.framework.scripts.ScriptsEngineManager;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.suteditor.planner.SutTreeNode;
import jsystem.treeui.suteditor.planner.SutTreeRenderer;
import jsystem.utils.PackageUtils;

/**
 * NodeRenderer class This class implements the look of the node in the tree
 */
public class NodeRenderer implements TreeCellRenderer {

	protected boolean bSelected = false;

	protected boolean bFocus = false;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
		TreeJPanel panel = new TreeJPanel(leaf);
		panel.setOpaque(false);
		panel.setEnabled(tree.isEnabled());

		bSelected = isSelected;
		bFocus = hasFocus;

		panel.check.setSelected(((AssetNode) value).isSelected());

		panel.setBackground(Color.white);
		panel.check.setBackground(Color.white);

		panel.label.setFont(tree.getFont());
		panel.label.setText(stringValue);
		panel.check.setVisible(false);
		if (leaf) {
			panel.label.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			panel.label.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			panel.label.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}

		if (value instanceof JarNode) {
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_JAR));
			JarNode jn = (JarNode) value;
			File f = (File) jn.getUserObject();
			panel.setToolTipText(f.getPath());
		} else if(value instanceof ScriptNode){
			panel.check.setVisible(true);
			ScriptNode sn = (ScriptNode)value;
			ScriptEngine engine = ScriptsEngineManager.getInstance().findExecutor(sn.getRunnerScript().getExecutor().getClass().getName());
				//sn.getRunnerScript().getExecutor().getEngine();
			ImageIcon icon = null;
			if(engine != null){
				icon = engine.getBasicImageIcon();
			}
			if(icon == null){
				icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT);
			}
			panel.label.setIcon(icon);
		} else if (value instanceof TestNode) {
			panel.check.setVisible(true);
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST));
		} else if (value instanceof ScenarioAsATestNode) {
			panel.check.setVisible(true);
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO_AS_TEST));
		} else if (value instanceof ScenarioNode) {
			panel.check.setVisible(true);
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SCENARIO));
		} else if (value instanceof DirectoryNode) {
			DirectoryNode dn = (DirectoryNode) value;

			if (dn.isClassPath()) {
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_PATH));
				panel.setToolTipText(((File) ((DirectoryNode) value).getUserObject()).getPath());
				panel.label.setText(((File) ((DirectoryNode) value).getUserObject()).getPath());
			} else {
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_DIR));
			}
		} else if (value instanceof JarEntryNode) {
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_DIR));
		} else if (value instanceof TestCaseNode) {
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_CASE));
		} else if (value instanceof FixtureNode) {
			panel.check.setVisible(true);
			panel.label.setText(PackageUtils.getOnlyClassName(stringValue));
			panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE));
		} else if(value instanceof SutTreeNode){
			SutTreeRenderer sutRenderer = new SutTreeRenderer();
			return sutRenderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
		}

		return panel;
	}

	/* This class will hold the text and tree table */
	class TreeLabel extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		boolean hasFocus = false;

		Color color = null;

		TreeLabel() {
			//
		}

		public void setColor(Color color) {
			this.color = color;
			setForeground(color);
		}

		@Override
		public void paint(Graphics g) {
			if ((getText()) != null) {
				Icon currentI = getIcon();

				if (bSelected) {
					setForeground(Color.white);

					Color bColor = Color.black;

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
				}
			}

			super.paint(g);
		}
	}

	class TreeJPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JCheckBox check;;

		public TreeLabel label;

		boolean isLeaf;

		public TreeJPanel(boolean isLeaf) {
			this.isLeaf = isLeaf;
			check = new JCheckBox();
			label = new TreeLabel();
			label.setOpaque(false);
			check.setOpaque(false);
			if (isLeaf) {
				add(check);
			}
			add(label);
		}

		public Dimension getPreferredSize() {
			if (!isLeaf) {
				return label.getPreferredSize();
			} else {
				Dimension d_check = check.getPreferredSize();
				Dimension d_label = label.getPreferredSize();
				return new Dimension(d_check.width + d_label.width, (d_check.height < d_label.height ? d_label.height
						: d_check.height));
			}
		}

		public void doLayout() {
			if (!isLeaf) {
				Dimension d_label = label.getPreferredSize();

				int y_label = 0;

				label.setLocation(0, y_label);
				label.setBounds(0, y_label, d_label.width, d_label.height);
			} else {
				Dimension d_check = check.getPreferredSize();
				Dimension d_label = label.getPreferredSize();

				int y_check = 0;
				int y_label = 0;

				check.setLocation(0, y_check);
				check.setBounds(0, y_check, d_check.width, d_check.height);
				label.setLocation(d_check.width, y_label);
				label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
			}
		}
	}
}
