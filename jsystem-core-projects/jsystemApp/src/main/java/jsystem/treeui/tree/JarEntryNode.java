/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarEntryNode extends AssetNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6004776007856576523L;

	public JarEntryNode(AssetNode parent, JarEntry userObject) throws Exception {
		super(parent, userObject);
		initChildren(JarNode.getAllSubEntries(new JarFile((File) getRootUserObject()), userObject.getName()));
	}

	public String toString() {
		String[] array = userObject.toString().split("/");
		if (array.length == 0) {
			return "";
		}
		return array[array.length - 1];
	}
}
