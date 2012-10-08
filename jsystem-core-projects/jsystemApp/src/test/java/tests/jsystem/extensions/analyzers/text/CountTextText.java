/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.text;

import jsystem.extensions.analyzers.text.CountText;
import junit.framework.SystemTestCase;

public class CountTextText extends SystemTestCase{
    CountText find = null;
    String text = "JSystem is a framework for writing and running automated tests, based on JUnit. Its main goal is to support automation of functional/system testing. See our home page for more information.";
    public void testSimpleCount(){
        find = new CountText("framework", 1);
        find.setTestAgainst(text);
        find.analyze();
        assertTrue("The word: framework should be found once", find.getStatus());
    }
    public void testCount(){
        find = new CountText("is", 2);
        find.setTestAgainst(text);
        find.analyze();
        assertTrue("The word: 'is' should be found twice", find.getStatus());
    }

    public void testDeviation(){
        find = new CountText("is", 1, 1);
        find.setTestAgainst(text);
        find.analyze();
        assertTrue("The word: 'is' should be found twice", find.getStatus());
    }
    public void testDeviation2(){
        find = new CountText("is", 4, 1);
        find.setTestAgainst(text);
        find.analyze();
        assertFalse("The word: 'is' should be found twice", find.getStatus());
    }
}
