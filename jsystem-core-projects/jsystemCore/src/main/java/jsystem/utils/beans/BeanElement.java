/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

import java.lang.reflect.Method;

import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.PropertyValidator;

/**
 * Data object that represent bean element or property of java object.<br>
 * It contain the name of the property (for setName the name of the property will Name),<br>
 * it contain the setter and getter methods and the type of object (class)
 * uses.
 * 
 * @author guy.arieli
 */
public class BeanElement {
	private String name;
	private Method setMethod;
	private Method getMethod;
	private boolean hasOptions = false;
	private String[] options = null;
	private String[] groups = null;
	private boolean editable = true;
	private Class<? extends PropertyValidator>[] validators = null;
	private ParameterProvider parameterProvider = null;
	
	public String[] getGroups() {
		return groups;
	}
	public void setGroups(String[] groups) {
		this.groups = groups;
	}
	private Class<?> type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Method getSetMethod() {
		return setMethod;
	}
	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}
	public Method getGetMethod() {
		return getMethod;
	}
	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public boolean isHasOptions() {
		return hasOptions;
	}
	public void setHasOptions(boolean hasOptions) {
		this.hasOptions = hasOptions;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public Class<? extends PropertyValidator>[] getValidators() {
		return validators;
	}
	public void setValidators(Class<? extends PropertyValidator>[] validators) {
		this.validators = validators;
	}
	
	public ParameterProvider getParameterProvider() {
		return parameterProvider;
	}
	
	public void setParameterProvider(ParameterProvider parameterProvider) {
		this.parameterProvider = parameterProvider;
	}
}
