/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import java.io.File;

import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;

public class StringUtilsTest extends SystemTestCase{
    public void testGetClassName() throws Exception{
        File file = new File("c:\\work\\project\\src\\com\\xxx\\MyClass.class");
        File root = new File("c:\\work\\project\\src\\");
        assertEquals("Fail to extract class name", "com.xxx.MyClass", StringUtils.getClassName(file.getPath(),root.getPath()));
    }
}
