/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.systemobjects;

import java.util.HashSet;

import jsystem.framework.system.SystemObjectImpl;

public class MyNestedSystemObject extends SystemObjectImpl {
	public MyNestedSystemObject[] arrayOfNested;
	public static HashSet<Integer> setOfIndices = new HashSet<Integer>();
	public void close() {
		super.close();
		report.report("closing " + getName() + getSOArrayIndex() );
		setOfIndices.add(getSOArrayIndex());
	}
}
