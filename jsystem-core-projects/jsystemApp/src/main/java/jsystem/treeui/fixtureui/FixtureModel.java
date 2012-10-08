/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.fixtureui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.fixture.RootFixture;

public class FixtureModel implements TreeModel {

	RootFixture root = RootFixture.getInstance();

	private Vector<TreeModelListener> fModelListeners = new Vector<TreeModelListener>();

	public FixtureModel() {
	}

	public Object getRoot() {
		return root;
	}

	public int getChildCount(Object parent) {
		Fixture p = (Fixture) parent;
		ArrayList<Fixture> childrens = FixtureManager.getInstance().getAllChildrens(p);
		return childrens.size();
	}

	public boolean isLeaf(Object node) {
		if (getChildCount(node) == 0) {
			return true;
		}
		return false;
	}

	public void addTreeModelListener(TreeModelListener l) {
		if (!fModelListeners.contains(l))
			fModelListeners.addElement(l);
	}

	/**
	 * Removes a TestModelListener
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		fModelListeners.removeElement(l);
	}

	public Object getChild(Object parent, int index) {
		Fixture p = (Fixture) parent;
		return FixtureManager.getInstance().getAllChildrens(p).get(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		Fixture p = (Fixture) parent;
		return FixtureManager.getInstance().getAllChildrens(p).indexOf(child);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// we don't support direct editing of the model
		System.out.println("TreeModel.valueForPathChanged: not implemented");
	}

	public void fireNodeChanged(TreePath path, int index) {
		int[] indices = { index };
		Object[] changedChildren = { getChild(path.getLastPathComponent(), index) };
		TreeModelEvent event = new TreeModelEvent(this, path, indices, changedChildren);

		Enumeration<TreeModelListener> e = fModelListeners.elements();
		while (e.hasMoreElements()) {
			e.nextElement().treeNodesChanged(event);
		}
	}

}
