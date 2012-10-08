/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;

public class DirectoryNode extends AssetNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1388318709827659192L;
	File dir;

	public DirectoryNode(AssetNode parent, File file) throws Exception {
		super(parent, file);
		this.dir = file;
		initChildren(file.listFiles());
	}

	public String toString() {
		return (dir.getName());
	}
}
