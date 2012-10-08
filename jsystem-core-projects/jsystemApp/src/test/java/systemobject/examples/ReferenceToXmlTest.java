/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import junit.framework.SystemTestCase;

public class ReferenceToXmlTest extends SystemTestCase {

	public void testRefToXML() throws Exception {
		SimpleSystemObject life = (SimpleSystemObject)system.getSystemObject("lifecycle0");
		System.out.println(life.getXPath());
		life = (SimpleSystemObject)system.getSystemObject("lifecycle1");
		System.out.println(life.getXPath());
	}

}
