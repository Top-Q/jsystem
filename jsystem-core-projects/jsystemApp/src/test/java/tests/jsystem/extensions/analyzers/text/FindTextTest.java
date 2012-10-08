/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.text;

import jsystem.extensions.analyzers.text.FindText;
import junit.framework.SystemTestCase;

public class FindTextTest extends SystemTestCase{
    FindText find = null;
    String text = "JSystem is a framework for writing and running automated tests, based on JUnit. Its main goal is to support automation of functional/system testing. See our home page for more information.";
     public void testSimpleFind(){
         find = new FindText("framework");
         find.setTestAgainst(text);
         find.analyze();
         assertTrue("Fail to find the word: framework", find.getStatus());
     }
    public void testNotFound(){
        find = new FindText("dog");
        find.setTestAgainst(text);
        find.analyze();
        assertFalse("Unexpectd find of the word: dog", find.getStatus());
    }
    public void testCaseSensetivity(){
        find = new FindText("Framework");
        find.setTestAgainst(text);
        find.analyze();
        assertFalse("Unexpectd find of the word: Framework", find.getStatus());
    }
    public void testRegExp(){
        find = new FindText("f.r", true);
        find.setTestAgainst(text);
        find.analyze();
        assertTrue("The word: for wasn't found", find.getStatus());
    }
}
