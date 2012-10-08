/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

/**
 * Code element represent a java package
 * 
 * @author guy.arieli
 * 
 */
public class Package implements CodeElement {
	/**
	 * the package name
	 */
	private String packageName = null;

	/**
	 * 
	 * @param packageName
	 *            package name
	 */
	public Package(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * The package as string
	 */
	public String toString() {
		if (packageName == null) {
			return "";
		}
		return "package " + packageName + ";";
	}

	/**
	 * Add the package to the code
	 */
	public void addToCode(Code code) {
		code.addLine(toString());
	}

	/**
	 * 
	 * @return the package name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * 
	 * @param packageName
	 *            the package name
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
