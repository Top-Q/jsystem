/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.io.File;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

public class ExtentionsFileFilter extends FileFilter {

	private String description;

	private Vector<String> extentions;

	public ExtentionsFileFilter() {
		extentions = new Vector<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		for (int i = 0; i < extentions.size(); i++) {
			String ext = (String) extentions.elementAt(i);
			if (f.getName().toLowerCase().endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void addExtention(String ext) {
		extentions.addElement(ext);
	}
}
