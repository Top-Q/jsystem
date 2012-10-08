/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementNode extends DefaultMutableTreeNode implements Comparable<Object>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088309871545463768L;

	public static final int TYPE_ROOT = 0;

	// public static final int TYPE_PACKAGE = 1;
	public static final int TYPE_TEST = 2;

	// public static final int TYPE_STEP = 3;

	public static final int TEST_PASS = 0;

	public static final int TEST_FAIL = 1;

	public static final int TEST_WARNING = 2;

	Element element;

	protected int viewMode;

	private int type;

	private int status = TEST_PASS;

	protected String name;

	public ElementNode(Element element, MutableTreeNode parent, int viewMode) {
		super(element);
		setParent(parent);
		this.element = element;
		this.viewMode = viewMode;
		name = element.getAttribute("name");
		setType();
		initStatus();
		initChildrens();
	}

	private void setType() {
		type = TYPE_ROOT;
		String elementName = element.getNodeName();
		if (elementName.equals("test")) {
			type = TYPE_TEST;
		}
	}

	protected void initStatus() {
		String s = element.getAttribute("status");
		if (s.equals("true")) {
			status = TEST_PASS;
		} else if (s.equals("false")) {
			status = TEST_FAIL;
		} else {
			status = TEST_WARNING;
		}
	}

	public void removeElement(ElementNode n) {
		getElement().removeChild(n.getElement());
		children.removeElement(n);
		recalcStatus();

	}

	@SuppressWarnings("unchecked")
	public void initChildrens() {
		if (children == null) {
			children = new Vector();
		}
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n instanceof Element) {
				Element childElement = (Element) n;
				String status = childElement.getAttribute("status");
				if (status != null && type != TYPE_TEST) {
					if (viewMode == PublisherTreeModel.VIEW_FAIL_ONLY) {
						if (!status.equals("false")) {
							continue;
						}
					} else if (viewMode == PublisherTreeModel.VIEW_NOT_SUCCESS) {
						if (status.equals("true")) {
							continue;
						}
					}
				}
				ElementNode toAdd;
				// if (childElement.getNodeName().equals("step")){
				// toAdd = new TestStepNode(childElement, this, viewMode);
				// }else
				if (childElement.getNodeName().equals("test")) {
					toAdd = new TestNode(childElement, this, viewMode);
				} else {
					toAdd = new ElementNode(childElement, this, viewMode);
				}
				toAdd.initStatus();
				children.addElement(toAdd);

			}
		}
	}

	public void recalcStatus() {
		Enumeration<?> list = children.elements();
		int expectedStatus = TEST_PASS;
		while (list.hasMoreElements()) {
			ElementNode childElement = (ElementNode) list.nextElement();
			if (childElement.getStatus() == TEST_FAIL) {
				expectedStatus = TEST_FAIL;
				break;
			}
		}
		if (status != expectedStatus) {
			setStatus(expectedStatus);
			if (parent != null) {
				((ElementNode) parent).recalcStatus();
			}
		}

	}

	public String toString() {
		return name;
	}

	/**
	 * @return Returns the viewMode.
	 */
	public int getViewMode() {
		return viewMode;
	}

	/**
	 * @param viewMode
	 *            The viewMode to set.
	 */
	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
		switch (status) {
		case TEST_PASS:
			element.setAttribute("status", "true");
			break;
		case TEST_FAIL:
			element.setAttribute("status", "false");
			break;
		case TEST_WARNING:
			element.setAttribute("status", "warning");
			break;
		}
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return Returns the element.
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * @param element
	 *            The element to set.
	 */
	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Object o) {
		return this.getName().compareTo(((ElementNode)o).getName());
	}

}
