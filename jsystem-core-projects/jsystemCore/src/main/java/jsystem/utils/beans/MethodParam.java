/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

public class MethodParam {
	
	public Class<?> type;
	public String name;
	
	public MethodParam(Class<?> type, String name){
		this.type = type;
		this.name = name;
	}
	
}
