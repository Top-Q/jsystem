/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import jsystem.treeui.sobrows.Options.Access;


public class Method implements CodeElement {
	/**
	 * The access of the method
	 */
	private Access access = Access.NO;

	/**
	 * The method code
	 */
	private String methodCode = null;

	/**
	 * The method return type
	 */
	private String returnType = "void";

	/**
	 * The method name
	 */
	private String methodName;

	/**
	 * The method parameters
	 */
	private LinkedHashMap<String, Member> parameters = null;

	/**
	 * The method javadoc
	 */
	private String javadoc = null;

	/**
	 * The exception name
	 */
	private String throwsName = null;

	/**
	 * true if the return type is array
	 */
	private boolean returnArray = false;

	private ArrayList<String>annotations = null;

	/**
	 * Add parameter to the parameters map
	 * 
	 * @param param
	 *            the member to add
	 */
	public void addParameter(Member param) {
		if (parameters == null) {
			parameters = new LinkedHashMap<String, Member>();
		}
		parameters.put(param.getName(), param);
	}

	/**
	 * Get the parameters as string
	 * 
	 * @param addType
	 *            if true the parameters type will be added to the string
	 * @return the parameters string
	 */
	public String getParametersString(boolean addType) {
		StringBuffer params = new StringBuffer();
		if (parameters != null) {
			Iterator<String> iter = parameters.keySet().iterator();
			boolean first = true;
			while (iter.hasNext()) {
				if (!first) {
					params.append(", ");
				} else {
					first = false;
				}
				if (addType) {
					params.append((parameters.get(iter.next())).toParameterString());
				} else {
					params.append((parameters.get(iter.next())).getName());

				}

			}
		}
		return params.toString();
	}

	/**
	 * Add the method to the code
	 */
	public void addToCode(Code code) {
		/*
		 * first the javadoc
		 */
		code.addMultiLines(javadoc);
		if(annotations != null){
			
			for(String annotation: annotations){
				code.addLine(annotation);
			}
		}
		/*
		 * then the decleration
		 */
		String throwString = "";
		if (throwsName != null) {
			throwString = "throws " + throwsName + " ";
		}
		code.addLine(Options.getAccessString(access) + returnType + ((returnArray) ? "[]" : "") + " " + methodName
				+ "(" + getParametersString(true) + ") " + throwString + "{");

		/*
		 * Then the code
		 */
		code.shiftRight();
		code.addMultiLines(methodCode);
		code.shiftLeft();
		code.addLine("}");
	}

	/**
	 * 
	 * @return the method access
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * 
	 * @param access
	 *            the method access
	 */
	public void setAccess(Access access) {
		this.access = access;
	}

	/**
	 * 
	 * @return the method javadoc as string
	 */
	public String getJavadoc() {
		return javadoc;
	}

	/**
	 * Set the method javadoc (support multi-lines)
	 * 
	 * @param javadoc
	 */
	public void setJavadoc(String javadoc) {
		if (javadoc != null && javadoc.indexOf("/*") < 0) {
			StringBuffer buf = new StringBuffer();
			String[] lines = javadoc.split("\n");
			buf.append("/**\n");
			for (int i = 0; i < lines.length; i++) {
				buf.append(" * " + lines[i] + "\n");
			}
			buf.append(" */");
			this.javadoc = buf.toString();
		} else {
			this.javadoc = javadoc;
		}
	}

	/**
	 * 
	 * @return the method code
	 */
	public String getMethodCode() {
		return methodCode;
	}

	/**
	 * 
	 * @param methodCode
	 *            the method code
	 */
	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	/**
	 * 
	 * @return method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * 
	 * @param methodName
	 *            method name
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * get the method parameters
	 * 
	 * @return the method parameters
	 */
	public HashMap<String, Member> getParameters() {
		return parameters;
	}

	/**
	 * Set the method parameters
	 * 
	 * @param parameters
	 */
	public void setParameters(LinkedHashMap<String, Member> parameters) {
		this.parameters = parameters;
	}

	/**
	 * 
	 * @return method return type
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * 
	 * @param returnType
	 *            method return type
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * 
	 * @return exeption name (or null if non)
	 */
	public String getThrowsName() {
		return throwsName;
	}

	/**
	 * 
	 * @param throwsName
	 *            the exeption name
	 */
	public void setThrowsName(String throwsName) {
		this.throwsName = throwsName;
	}

	/**
	 * 
	 * @return true if method return an array
	 */
	public boolean isReturnArray() {
		return returnArray;
	}

	/**
	 * set to true if method return an array
	 * 
	 * @param returnArray
	 */
	public void setReturnArray(boolean returnArray) {
		this.returnArray = returnArray;
	}

	/**
	 * 
	 * @return a comma list of all the parameters name
	 */
	public String getParametersName() {
		StringBuffer params = new StringBuffer();
		if (parameters != null) {
			Iterator<String> iter = parameters.keySet().iterator();
			boolean first = true;
			while (iter.hasNext()) {
				if (!first) {
					params.append(",");
				} else {
					first = false;
				}
				params.append(( parameters.get(iter.next())).getName());

			}
		}
		return params.toString();

	}
	public void addAnnotation(String annotation){
		if(annotations == null){
			annotations = new ArrayList<String>();
		}
		annotations.add(annotation);
	}

}
