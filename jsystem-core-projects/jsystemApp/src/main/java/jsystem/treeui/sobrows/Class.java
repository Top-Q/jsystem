/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import jsystem.treeui.sobrows.Options.Access;


/**
 * Represent a java class file, addToCode is used to generate the code of the
 * class. It hold the java doc of the class, the class imports, members, and
 * methods.
 * 
 * @author guy.arieli
 * 
 */
public class Class implements CodeElement {
	/**
	 * The access of the class (the defulat is public).
	 */
	private Access access = Access.PUBLIC;

	/**
	 * the name of the super class
	 */
	private String extendsName = null;

	/**
	 * The class package name
	 */
	private Package packageName = null;

	/**
	 * The imports of the class
	 */
	public Imports imports = new Imports();

	/**
	 * The javadoc of the class
	 */
	private String javadoc = null;

	/**
	 * An array of methods
	 */
	public ArrayList<Method> methods = new ArrayList<Method>();

	/**
	 * Ordered hashmap of all the members (fields)
	 */
	public LinkedHashMap<String, Member> members = new LinkedHashMap<String, Member>();

	/**
	 * The class name (without the package)
	 */
	private String className = null;

	/**
	 * the abstract status of the class
	 */
	private boolean isAbstract = false;

	public void addToCode(Code code) {
		/*
		 * First add the package name
		 */
		if (packageName != null) {
			packageName.addToCode(code);
		}

		/*
		 * Then all the imports
		 */
		imports.addToCode(code);

		/*
		 * The the javadoc
		 */
		code.addMultiLines(javadoc);

		/*
		 * Now add the class definitions
		 */
		StringBuffer buf = new StringBuffer();
		buf.append(Options.getAccessString(access));
		if (isAbstract) {
			buf.append("abstract ");
		}
		buf.append("class ");
		buf.append(className);
		if (extendsName != null) {
			buf.append(" extends ");
			buf.append(extendsName);
		}
		buf.append("{");
		code.addLine(buf.toString());
		code.shiftRight();

		/*
		 * Add all the members
		 */
		Iterator<Member> iter = members.values().iterator();
		while (iter.hasNext()) {
			iter.next().addToCode(code);
		}

		/*
		 * first add the methods that are not setter or getters
		 */
		for (int i = 0; i < methods.size(); i++) {
			if (!methods.get(i).getMethodName().startsWith("set") && !methods.get(i).getMethodName().startsWith("get")) {
				methods.get(i).addToCode(code);
			}
		}

		/*
		 * Then add the setters and getters
		 */
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).getMethodName().startsWith("set") || methods.get(i).getMethodName().startsWith("get")) {
				methods.get(i).addToCode(code);
			}
		}
		code.shiftLeft();
		code.addLine("}");
	}

	/**
	 * Check if a method is allready exist in the class
	 * 
	 * @param methodName
	 *            the method name to check
	 * @return true if the method exist
	 */
	public boolean isMethodExist(String methodName) {
		for (Method m : methods) {
			if (m.getMethodName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return the access status
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * Set the access see the Options class
	 * 
	 * @param access
	 */
	public void setAccess(Access access) {
		this.access = access;
	}

	/**
	 * get the class name
	 * 
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Set the class name
	 * 
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Get the super class name
	 * 
	 * @return super class name
	 */
	public String getExtendsName() {
		return extendsName;
	}

	/**
	 * Set the super class name
	 * 
	 * @param extendsName
	 *            the super class name
	 */
	public void setExtendsName(String extendsName) {
		this.extendsName = extendsName;
	}

	/**
	 * Get the java doc of the class
	 * 
	 * @return the java doc
	 */
	public String getJavadoc() {
		return javadoc;
	}

	/**
	 * Set the javadoc (The string will be splited to lines and formated, as
	 * javadoc).
	 * 
	 * @param javadoc
	 */
	public void setJavadoc(String javadoc) {
		if (javadoc == null) {
			return;
		}
		String[] javaDocLines = javadoc.split("\n");
		StringBuffer jdbuf = new StringBuffer();
		jdbuf.append("/**\n");
		for (int i = 0; i < javaDocLines.length; i++) {
			jdbuf.append(" * ");
			jdbuf.append(javaDocLines[i]);
			jdbuf.append("\n");
		}
		jdbuf.append(" */");
		this.javadoc = jdbuf.toString();
	}

	/**
	 * Get the package
	 * 
	 * @return the package
	 */
	public Package getPackage() {
		return packageName;
	}

	/**
	 * Set the package
	 * 
	 * @param packageName
	 *            packageName
	 */
	public void setPackage(Package packageName) {
		this.packageName = packageName;
	}

	/**
	 * 
	 * @return true if the class is abstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * Set the class abstract status
	 * 
	 * @param isAbstract
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

}
