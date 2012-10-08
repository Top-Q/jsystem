/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.suteditor.planner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.TreeNode;

import jsystem.framework.scenario.PropertyValidator;
import jsystem.framework.sut.SutValidationError;
import jsystem.treeui.tree.AssetNode;
import jsystem.utils.beans.BeanElement;

import org.w3c.dom.Element;

/**
 * The SUT tree node used as the SUT planner tree node. It hold the node type,
 * and the document element. It also implement <code>toString</code> method that
 * is used by the renderer.
 * 
 * @author guy.arieli
 * 
 */
public class SutTreeNode extends AssetNode {

	private static final long serialVersionUID = 8808044683050841026L;

	public enum NodeType {
		ROOT, MAIN_SO, EXTENTION_SO, EXTENTION_ARRAY_SO, SUB_SO, ARRAY_SO, TAG, OPTIONAL_TAG
	}

	/**
	 * Holds the actual value of the parameter. This is relevant only for
	 * tag/optional tag nodes
	 */
	private String actualValue;

	/**
	 * Holds the default value of the parameter. This is relevant only for
	 * tag/optional tag nodes.
	 */
	private String defaultValue;

	/**
	 * Type of the node.
	 */
	private NodeType type;

	/**
	 * Holds the source element from the XML document.
	 */
	Element element;

	/**
	 * Name of the node.
	 */
	private String nodeName;

	/**
	 * Class name. This is relevant only for system objects nodes
	 */
	private String className;
	
	private String arraySuperClassName;

	/**
	 * Index of the system object. This is relevant only for array system
	 * objects.
	 */
	private int index;

	private String javadoc;

	private String filterCache = null;
	private FilterType filterTypeCache = FilterType.ALL;
	private boolean acceptStatusCache = true;
	private boolean acceptedStatus = false;
	
	private BeanElement bean;	

	public SutTreeNode(NodeType type, String nodeName) {
		actualValue = new String("");
		defaultValue = new String("");
		className = new String("");
		index = -1;
		javadoc = new String("");
		acceptedStatus = false;
		this.type = type;
		this.nodeName = nodeName;
	}

	public BeanElement getBean() {
		return bean;
	}

	public void setBean(BeanElement bean) {
		this.bean = bean;
	}

	/**
	 * If the leaf is of the following types it's a leaf: EXTENTION_SO,
	 * EXTENTION_ARRAY_SO, TAG, OPTIONAL_TAG.
	 */
	public boolean isLeaf() {
		return (type == NodeType.EXTENTION_SO
				|| type == NodeType.EXTENTION_ARRAY_SO || type == NodeType.TAG || type == NodeType.OPTIONAL_TAG);
	}

	public boolean accept(FilterType filterType, String expression) {
		if (filterTypeCache != filterType){
			filterCache = null;
		}			
		if (expression == null || expression.trim().equals("")) {			
			filterCache = expression;
			filterTypeCache = filterType;
			return true;
		}
		if (expression.equals(filterCache)) {
			return acceptStatusCache;
		}
		TreeNode parent = getParent();
		while(parent != null){
			if(parent instanceof SutTreeNode){
				if(((SutTreeNode)parent).getAcceptedStatus()){
					return true;
				}
			}
			parent = parent.getParent();
		}
		filterCache = expression;
		filterTypeCache = filterType;
		acceptStatusCache = true;
		boolean isFound = false;
		String stringToSearchIn;
		Pattern regexp;
		Matcher m;
		StringBuffer groupString = new StringBuffer();
		if(bean != null){
			String[] groups = bean.getGroups();
			if(groups != null){
				for(String g: groups){
					groupString.append('.');
					groupString.append(g);
				}
			}
		}
		switch (filterType) {
		case ALL:
			stringToSearchIn = actualValue + "." + defaultValue + "."  + nodeName + "."  + className + "."  + javadoc + groupString;
			regexp = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			m = regexp.matcher(stringToSearchIn);
			isFound = m.find();
			break;
			
		case CLASS:
			stringToSearchIn = className;
			regexp = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			m = regexp.matcher(stringToSearchIn);
			isFound = m.find();
			break;
			
		case NAME:
			stringToSearchIn = nodeName;
			regexp = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			m = regexp.matcher(stringToSearchIn);
			isFound = m.find();
			break;
			
		case VALUE:
			stringToSearchIn = actualValue + defaultValue;
			regexp = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			m = regexp.matcher(stringToSearchIn);
			isFound = m.find();
			break;			
		}
		
		if (isFound) {
			setAcceptedStatus(true);
			return true;
		} else {
			setAcceptedStatus(false);
		}
		Object[] children = getChildren();
		for (Object o : children) {
			if (o instanceof SutTreeNode) {
				if (((SutTreeNode) o).accept(filterType, expression)) {
					return true;
				}
			}
		}
		acceptStatusCache = false;
		return false;
	}
	
	public void getValidationErrors(ArrayList<SutValidationError> errors){
		if(bean != null){
			Class<? extends PropertyValidator>[] validators = bean.getValidators();
			if(validators != null){
				for(Class<? extends PropertyValidator> validatorClass: validators){
					try {
						PropertyValidator validator = validatorClass.newInstance();
						SutValidationError[] verrors = validator.validate(nodeName, actualValue);
						if(verrors != null){
							errors.addAll(Arrays.asList(verrors));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Object[] children = getChildren();
		for (Object o : children) {
			if (o instanceof SutTreeNode) {
				((SutTreeNode)o).getValidationErrors(errors);
			}
		}
	}

	public String toString() {
		if(type.equals(NodeType.ARRAY_SO)){
			for(int i = 0; i < getChildCount(); i++){
				SutTreeNode c = (SutTreeNode)getChildAt(i);
				if(c.getName().equals("role")){
					if(c.getActualValue() != null && !c.getActualValue().isEmpty()){
						return c.getActualValue() + " - " + nodeName + "[" + index +"]";
					} else if(c.getDefaultValue() != null && !c.getDefaultValue().isEmpty()){
						return c.getDefaultValue() + " - " + nodeName + "[" + index +"]";
					} else {
						break;
					}
				}
			}
			return nodeName + "[" + index +"]";
		}
		return nodeName;
	}

	public String getActualValue() {
		return actualValue;
	}

	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public String getName() {
		return nodeName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getJavadoc() {
		return javadoc;
	}

	public void setJavadoc(String javadoc) {
		if (javadoc != null) {
			this.javadoc = javadoc;
		}
	}

	public boolean getAcceptedStatus() {
		return acceptedStatus;
	}

	public void setAcceptedStatus(boolean acceptedStatus) {
		this.acceptedStatus = acceptedStatus;
	}
	
	protected Object[] getChildren() {
		ArrayList<SutTreeNode> getChildren = new ArrayList<SutTreeNode>();
		for (Enumeration<?> e = children(); e.hasMoreElements();) {
			getChildren.add((SutTreeNode) e.nextElement());
		}

		return getChildren.toArray(new SutTreeNode[0]);
	}

	public String getArraySuperClassName() {
		return arraySuperClassName;
	}

	public void setArraySuperClassName(String arraySuperClassName) {
		this.arraySuperClassName = arraySuperClassName;
	}

	public boolean isEditable() {
		if(bean == null){
			return true;
		}
		return bean.isEditable();
	}


}
