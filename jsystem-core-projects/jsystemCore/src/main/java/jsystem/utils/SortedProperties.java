/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class SortedProperties extends Properties {
	private static final long serialVersionUID = 5667041748967632284L;
	
	public SortedProperties(){
		super();
	}
	
	public SortedProperties(Properties p){
		this();
		putAll(p);
	}
	
	@Override  
	public  Enumeration<Object> keys() {    
		return Collections.enumeration(new TreeSet<Object>(super.keySet()));  
	}
}
