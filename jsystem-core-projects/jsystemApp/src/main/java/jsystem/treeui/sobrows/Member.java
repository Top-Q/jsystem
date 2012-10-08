/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

import jsystem.treeui.sobrows.Options.Access;
import jsystem.utils.StringUtils;

/**
 * A class member. Use as method parameter as well.
 * 
 * @author guy.arieli
 * 
 */
public class Member implements CodeElement {
	/**
	 * The member access public/private ...
	 */
	private Access access = Access.NO;

	/**
	 * The name of the member
	 */
	private String name = null;

	/**
	 * The type of the member
	 */
	private String type = null;

	/**
	 * the default value of the member
	 */
	private String value = null;

	/**
	 * diterminate if the member is an array
	 */
	private boolean isArray = false;
	
	/**
	 * Init a member to be used as a class field
	 * 
	 * @param name
	 *            the member name
	 * @param type
	 *            the member type
	 * @param value
	 *            the member default value
	 * @param access
	 *            the access of the member
	 */
	public Member(String name, String type, String value, Access access) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.access = access;
	}

	/**
	 * Init a member to be used as parameter to method
	 * 
	 * @param name
	 *            the nember name
	 * @param type
	 *            the member type
	 */
	public Member(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public Member() {
		// default constractor
	}

	/**
	 * @return the member as a string (public int index = 0 ...)
	 */
	public String toString() {
		return Options.getAccessString(access) + type + ((isArray) ? "[]" : "") + " " + name
				+ ((value == null) ? "" : " = " + value) + ";";
	}

	/**
	 * 
	 * @return the string of method parameter (int index).
	 */
	public String toParameterString() {
		return type + ((isArray) ? "[]" : "") + " " + name;
	}

	/**
	 * 
	 * @return the member access
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * Set the member access see Options class
	 * 
	 * @param access
	 */
	public void setAccess(Access access) {
		this.access = access;
	}

	/**
	 * Get the member name
	 * 
	 * @return the member name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the member name
	 * 
	 * @param name
	 *            the member name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the member type
	 * 
	 * @return the member type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the member type
	 * 
	 * @param type
	 *            the member type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the member defulat value
	 * 
	 * @return member value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the member defualt value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Add to overall code
	 */
	public void addToCode(Code code) {
		code.addLine(toString());
	}

	/**
	 * Get a setter method for this member
	 * 
	 * @return a setter method
	 */
	public Method getSetter() {
		Method m = new Method();
		m.setAccess(Access.PUBLIC);
		m.setJavadoc("set the " + name);
		m.setMethodName("set" + StringUtils.firstCharToUpper(name));
		m.setMethodCode("this." + name + " = " + name + ";");
		m.addParameter(this);
		m.setReturnType("void");
		m.setThrowsName(null);
		// m.setReturnArray(isArray);
		return m;
	}

	/**
	 * Get a getter method for this member
	 * 
	 * @return a getter method
	 */
	public Method getGetter() {
		Method m = new Method();
		m.setAccess(Access.PUBLIC);
		m.setJavadoc("get the " + name);
		m.setMethodName("get" + StringUtils.firstCharToUpper(name));
		m.setMethodCode("return " + name + ";");
		m.setReturnType(type);
		m.setThrowsName(null);
		m.setReturnArray(isArray);
		return m;
	}

	/**
	 * Is member an array
	 * 
	 * @return true if member is an array
	 */
	public boolean isArray() {
		return isArray;
	}

	/**
	 * Set the member array status
	 * 
	 * @param isArray
	 */
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	
}
