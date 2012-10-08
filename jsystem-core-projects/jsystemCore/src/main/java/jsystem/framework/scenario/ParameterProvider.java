/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.awt.Component;

/**
 * The <code>ParameterProvider</code> is an interface that enable
 * definition of new types of parameters. In order to define new provider
 * following are the main methods that should be implemented:
 * 1. getAsString - take a object and serialize it to string.
 * 2. getFromString - take a string and create the object.
 * 3. showUI - create the UI for this provider.
 * @author guy.arieli
 *
 */
public interface ParameterProvider {
	
	public void setProviderConfig(String...args);
	/**
	 * serialize the parameter object into string
	 * @param o
	 * @return a String representing the object value
	 */
	public String getAsString(Object o);
	
	/**
	 * Build the object from the represented string. It is the opposite of the <code>getAsString</code> method.
	 * @param stringRepresentation the serialized string
	 * @return an object created using the input string.
	 * @throws Exception
	 */
	public Object getFromString(String stringRepresentation) throws Exception;
	
	/**
	 * Show a UI element that enable to set the value for the requested object. This part extend 
	 * the Runner user interface.
	 * @param parent a parent component to bound the new object to.
	 * @param currentScenario the current scenario.
	 * @param rtest the current runner test object
	 * @param classType the object type (class)
	 * @param object the base object to change.
	 * @return the object after it was changed
	 * @throws Exception
	 */
	public Object showUI(Component parent, Scenario currentScenario, RunnerTest rtest, Class<?>classType, Object object,Parameter parameter) throws Exception;
	
	/**
	 * 
	 * @return true if the parameter text field should be editable
	 */
	public boolean isFieldEditable();
	
}
