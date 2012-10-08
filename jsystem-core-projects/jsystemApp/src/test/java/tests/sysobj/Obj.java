/*
 * Created on Sep 28, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.sysobj;

import jsystem.framework.system.SystemObjectImpl;

/**
 * @author guy.arieli
 *
 */
public class Obj extends SystemObjectImpl {
	String tag;
	public Obj(){
		System.out.println("Obj construct");
	}
	public void init() throws Exception{
		super.init();
		System.out.println("Obj inits");
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		System.out.println("tag was set: " + tag);
		this.tag = tag;
	}
	
	public void close(){
		super.close();
		System.out.println("Obj close");
		
	}
}
