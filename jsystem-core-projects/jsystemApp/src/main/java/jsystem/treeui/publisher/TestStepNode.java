/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import javax.swing.tree.MutableTreeNode;

import org.w3c.dom.Element;

public class TestStepNode extends ElementNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5470367168617377841L;
	protected String message;

	/**
	 * @param element
	 * @param parent
	 * @param viewMode
	 */
	public TestStepNode(Element element, MutableTreeNode parent, int viewMode) {
		super(element, parent, viewMode);
		message = element.getAttribute("message");
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
