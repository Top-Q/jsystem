/*
 * Created on Apr 20, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.text;

import jsystem.extensions.analyzers.text.FindText;
import jsystem.extensions.analyzers.text.RemoveLines;
import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.system.SystemObjectImpl;
import junit.framework.SystemTestCase;

/**
 * @author guy.arieli
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveLinesTest extends SystemTestCase {
	public void testLineRemovel(){
		String text = "xxxx\nyyyyy\ndddd";
		DummyObject dummy = new DummyObject();
		dummy.setTestAgainstObject(text);
		dummy.analyze(new FindText("yyyy"));
		dummy.analyze(new RemoveLines("yyy"));
		dummy.analyze(new TextNotFound("yyyy"));
	}

}

class DummyObject extends SystemObjectImpl{
	
}
