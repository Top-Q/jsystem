/*
 * Created on Oct 15, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

/**
 * @author guy.arieli
 * 
 */
public interface ExtendTestReporter extends TestReporter {

	public void saveFile(String fileName, byte[] content);

	public void report(String title, String message, int status, boolean bold, boolean html, boolean link);

	public void startSection();

	public void endSection();

	public void setData(String data);

	/**
	 * adding a property
	 * @param key
	 * 			the property key
	 * @param value
	 * 			the property value
	 */
	public void addProperty(String key, String value);

	/**
	 * Associates a property - value with a container in the scenario hierarchy tree.
	 * Property later can be viewed in the HTML reporter later.
	 * 
	 * @param ancestorLevel - the level of test's ancestor in to which property will be associated.
	 *                        if the value is 0, the property will be associated with test's parent (either a scenario or a flow control)
	 *                        if the value is 1 , the property will be associated with test grandparent etc'.
	 *                        If the value supplied is greater then the number of levels in the scenario tree, property will
	 *                        be associated with root scenario. 
	 *                   
	 * @param key  - property key
	 * @param value - property value
	 */
	public void setContainerProperties(int ancestorLevel, String key,String value);
	
	public void flush() throws Exception;
}
