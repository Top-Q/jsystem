/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.reference;

import jsystem.framework.system.SystemObjectImpl;

public class InternalSysObject extends SystemObjectImpl {
	
	public String descriptor;
	public String field2;

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String name) {
		this.descriptor = name;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
		
}
