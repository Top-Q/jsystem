/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

/**
 * Interface of all the code elements
 * 
 * @author guy.arieli
 * 
 */
public abstract interface CodeElement {

	/**
	 * Add the element to the code
	 * 
	 * @param code
	 *            the code object
	 */
	public abstract void addToCode(Code code);
}
