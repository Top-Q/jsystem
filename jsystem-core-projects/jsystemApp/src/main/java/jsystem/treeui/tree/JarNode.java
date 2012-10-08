/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jsystem.utils.StringUtils;

public class JarNode extends AssetNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3847105949397627635L;
	JarFile jar;

	public JarNode(AssetNode parent, File jarFile) throws Exception {
		super(parent, jarFile);
		jar = new JarFile(jarFile);
		initChildren(getAllSubEntries(jar, ""));
		System.out.println("Jar node: " + jar.getName());
	}

	public static Object[] getAllSubEntries(JarFile jar, String root) {
		int rootLevels = StringUtils.countString(root, "/");
		Vector<JarEntry> v = new Vector<JarEntry>();
		Enumeration<JarEntry> enum1 = jar.entries();

		while (enum1.hasMoreElements()) {
			JarEntry entry = (JarEntry) enum1.nextElement();
			String entryName = entry.getName();

			if (!entryName.startsWith(root)) {
				continue;
			}

			int entryLevels = StringUtils.countString(entry.getName(), "/");

			if (entry.isDirectory()) {

				if (rootLevels + 1 == entryLevels) {
					v.add(entry);
				}
			} else {

				if (rootLevels == entryLevels) {
					v.add(entry);
				}
			}
		}
		return v.toArray();
	}

	public String toString() {
		File f = new File(jar.getName());
		return f.getName();
		// return jar.getName();
	}
}
