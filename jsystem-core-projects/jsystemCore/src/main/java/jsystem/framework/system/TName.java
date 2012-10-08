/*
 * Created on Feb 13, 2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import jsystem.utils.StringUtils;

/**
 * A meta-data object for a JTest. initiated by the TestNameServer
 * 
 * @author guy.arieli
 * 
 */
public class TName {
	String className;

	String methodName;

	String fullName = null;

	String name = null;

	String paramsString = null;

	String userDocumentation;

	String comment;

	/**
	 * get the comment the user gave to this test through "Comment"
	 * 
	 * @return the comment from the xml
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * set the user comment
	 * 
	 * @param comment
	 *            set the comment for this test
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * get the user documentation for this test
	 * 
	 * @return the user documentation
	 */
	public String getUserDocumentation() {
		return userDocumentation;
	}

	/**
	 * set the user documentation for this test
	 * 
	 * @param userDocumentation
	 *            the documentation
	 */
	public void setUserDocumentation(String userDocumentation) {
		this.userDocumentation = userDocumentation;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParamsString() {
		return paramsString;
	}

	public void setParamsString(String paramsString) {
		this.paramsString = paramsString;
	}

	public String getFullName() {
		StringBuffer buf = new StringBuffer();
		buf.append(StringUtils.getClassName(className));
		buf.append('.');
		buf.append(methodName);
		if (paramsString != null) {
			buf.append(methodName);
			buf.append('?');
			buf.append(paramsString);
		}
		name = buf.toString();
		fullName = buf.toString();
		return fullName;
	}

	public String getName() {
		if (name == null) {
			getFullName();
		}
		return name;
	}

	public String getBasicName() {
		return StringUtils.getClassName(className) + "." + methodName;
	}

}
