/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jsystem.utils.FileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * 
 * @author guy.arieli
 * 
 */
public class PublisherTreeModel implements TreeModel {
	private static Logger log = Logger.getLogger(PublisherTreeModel.class.getName());

	public static final int VIEW_ALL = 0;

	public static final int VIEW_FAIL_ONLY = 1;

	public static final int VIEW_NOT_SUCCESS = 2;

	public static final String[] VIEW_TYPES = new String[] { "All", "Fail Only", "Fail & Warning" };

	public static int getViewType(String selectType) {
		if (selectType.equals(VIEW_TYPES[VIEW_ALL])) {
			return VIEW_ALL;
		} else if (selectType.equals(VIEW_TYPES[VIEW_FAIL_ONLY])) {
			return VIEW_FAIL_ONLY;
		} else {
			return VIEW_NOT_SUCCESS;
		}
	}

	private ElementNode root = null;

	private Document doc;

	private int viewType;

	private File xmlFile;

	private File tmpXmlFile;

	public PublisherTreeModel(File xmlFile) throws Exception {
		this.xmlFile = xmlFile;
		FileUtils.getEmptyXmlFile(xmlFile);
		this.viewType = VIEW_ALL;
		tmpXmlFile = new File(xmlFile.getPath() + ".tmp");
	}

	public void refresh(boolean fromOriginal) throws Exception {
		if (fromOriginal) {
			if (!xmlFile.exists()) {
				FileUtils.getEmptyXmlFile(xmlFile);
			}
			FileUtils.copyFile(xmlFile, tmpXmlFile);
		}
		doc = FileUtils.readDocumentFromFile(tmpXmlFile);
		root = new ElementNode(doc.getDocumentElement(), null, viewType);
	}

	public void modelChanged() {
		writeToXmlFile(tmpXmlFile);
	}

	public void writeToXmlFile() {
		if (doc != null) {
			writeToXmlFile(tmpXmlFile);
		} else
			try {
				FileUtils.getEmptyXmlFile(tmpXmlFile);
			} catch (Exception e1) {
				log.log(Level.WARNING, "exception while initializing xml file");
			}
		try {
			FileUtils.copyFile(tmpXmlFile, xmlFile);
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to write to: " + xmlFile.getPath(), e);
		}
	}

	private void writeToXmlFile(File file) {
		try {
			// Prepare the DOM document for writing
			FileUtils.saveDocumentToFile(doc, file);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to write to: " + xmlFile.getPath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		return ((ElementNode) parent).getChildCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		return ((ElementNode) node).isLeaf();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		return ((ElementNode) parent).getChildAt(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent != null) {
			int i = 0;

			for (Enumeration<?> e = ((ElementNode) parent).children(); e.hasMoreElements(); i++) {
				if (e.nextElement().equals(child)) {
					return i;
				}
			}
		}
		return -1;
	}

	public ElementNode findNode(Element element) {
		return find(root, element);
	}

	private ElementNode find(ElementNode parent, Element element) {
		ElementNode found = null;
		Enumeration<?> enum1 = parent.children();
		while (enum1.hasMoreElements()) {
			ElementNode node = (ElementNode) enum1.nextElement();
			if (node.getElement().equals(element)) {
				found = node;
				break;
			}
			found = find(node, element);
		}
		return found;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void printModel(ElementNode node) {
		System.out.println("Node: " + node.getName());
		Enumeration<?> enum1 = node.children();
		while (enum1.hasMoreElements()) {
			ElementNode n = (ElementNode) enum1.nextElement();
			printModel(n);
		}
	}

	/**
	 * @return Returns the viewType.
	 */
	public int getViewType() {
		return viewType;
	}

	/**
	 * @param viewType
	 *            The viewType to set.
	 */
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public boolean equals(Object o) {
		return false;
	}
}
