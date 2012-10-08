/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html.summary;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Tag extends LinkedList<Object> {

	private static final long serialVersionUID = -4472393843931589781L;

	private Attributes attributes; // tag attributes

	private String name; // name of tag

	private boolean close; // closing tag y/n

	public Tag(String name) {
		super();
		close = true;
		this.name = name.toLowerCase();
		attributes = new Attributes();
	}

	public Tag(String name, boolean close) {
		this(name);
		this.close = close;
	}

	public Tag(String name, Attributes attr) {
		this(name);
		attributes = attr;
	}

	public Tag(String name, Attributes attr, boolean close) {
		this(name, attr);
		this.close = close;
	}

	public Tag(String name, String attr) {
		this(name);
		if (attr == null) {
			attributes = new Attributes();

		} else {
			attributes = new Attributes(attr);
		}
	}

	public Tag(String name, String attr, boolean close) {
		this(name, attr);
		this.close = close;
	}

	public Tag(String name, Attributes attr, List<Object> content) {
		super(content);
		this.name = name.toLowerCase();
		attributes = attr;
	}

	public Tag(String name, Attributes attr, List<Object> content, boolean close) {
		this(name, attr, content);
		this.close = close;
	}

	public Tag(String name, String attr, String toAdd) {
		this(name, attr);
		add(toAdd);
	}

	/**
	 * Get the value of close.
	 * 
	 * @return Value of close.
	 */
	public boolean getClose() {
		return close;
	}

	/**
	 * Set the value of close.
	 * 
	 * @param v
	 *            Value to assign to close.
	 */

	public void setClose(boolean v) {
		this.close = v;
	}

	public String toString() {
		StringBuffer out = new StringBuffer("<" + name);
		out.append(attributes.toString());
		out.append(">");
		// content
		ListIterator<Object> iterator = super.listIterator();
		while (iterator.hasNext()) {
			out.append(iterator.next().toString());
		}
		if (close)
			out.append("</" + name + ">\n");
		return out.toString();
	}

	public boolean add(Object o) {
		if (o == null) {
			throw new NullPointerException();
		}
		return super.add(o);
	}

	/**
	 * Get the value of attributes.
	 * 
	 * @return Value of attributes.
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Set the value of attributes.
	 * 
	 * @param v
	 *            Value to assign to attributes.
	 */
	public void setAttributes(Attributes v) {
		this.attributes = v;
	}

	public void addAttribute(Attribute attr) {
		attributes.add(attr);
	}

}
