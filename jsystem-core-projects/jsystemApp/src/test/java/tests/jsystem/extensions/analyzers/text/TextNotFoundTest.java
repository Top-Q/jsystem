/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.text;

import jsystem.extensions.analyzers.text.TextNotFound;
import junit.framework.SystemTestCase;

public class TextNotFoundTest extends SystemTestCase{
    TextNotFound find = null;
    String text = "JSystem is a framework for writing and running automated tests, based on JUnit. Its main goal is to support automation of functional/system testing. See our home page for more information.";
     public void testSimpleFind(){
         find = new TextNotFound("frameworkx");
         find.setTestAgainst(text);
         find.analyze();
         assertTrue("The text 'frameworkx' shouldn't be bound", find.getStatus());
     }
    public void testSimpleFindNegative(){
        find = new TextNotFound("framework");
        find.setTestAgainst(text);
        find.analyze();
        assertFalse("The text 'framework' should be bound", find.getStatus());
    }
}
