/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.fixtureui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureManager;
import jsystem.treeui.images.ImageCenter;

/**
 * A Panel showing a test suite as a tree.
 */
public class FixturePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(FixturePanel.class.getName());

	private JTree fTree;

	private JScrollPane fScrollTree;

	private FixtureModel fModel;

	public FixturePanel() {
		super(new BorderLayout());
		fTree = new JTree();
		fTree.setModel(null);
		fTree.setRowHeight(20);
		DefaultTreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		fTree.setSelectionModel(tsm);
		ToolTipManager.sharedInstance().registerComponent(fTree);
		fTree.putClientProperty("JTree.lineStyle", "Angled");
		fScrollTree = new JScrollPane(fTree);
		add(fScrollTree, BorderLayout.CENTER);
	}

	/**
	 * Returns the selected test or null if multiple or none is selected
	 */
	public Fixture getSelectedFixture() {
		TreePath[] paths = fTree.getSelectionPaths();
		if (paths != null && paths.length == 1)
			return (Fixture) paths[0].getLastPathComponent();
		return null;
	}

	/**
	 * Returns the Tree
	 */
	public JTree getTree() {
		return fTree;
	}

	/**
	 * Shows the test hierarchy starting at the given test
	 */
	public void showFixtureTree() {
		fModel = new FixtureModel();
		fTree.setModel(fModel);
		fTree.setCellRenderer(new FixturePanel.TestTreeCellRenderer());
		expendAll();
	}

	private void expendAll() {
		for (int i = 0; i < fTree.getRowCount(); i++) {
			fTree.expandRow(i);
		}
	}

	public void fireTestChanged(final Fixture fixture) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Vector<Fixture> vpath = null;
				try {
					vpath = FixtureManager.getInstance().getFixturePath(fixture);
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to find fixture path", e);
					return;
				}
				Object[] path = new Object[vpath.size()];
				vpath.copyInto(path);
				TreePath treePath = new TreePath(path);
				fTree.expandPath(treePath);
				fTree.scrollPathToVisible(treePath);
			}
		});
	}

	static class TestTreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		TestTreeCellRenderer() {
			super();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			setText(value.toString());
			if (FixtureManager.getInstance().isCurrent((Fixture) value)) {
				setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_CURRENT_FIXTURE));
			} else {
				setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE));
			}
			return c;
		}
	}
}