/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import java.io.IOException;

import jsystem.utils.ClassPathFile;
import junit.framework.SystemTestCase;

public class ClassPathFileTest extends SystemTestCase{
    public void testClassPahtListFile() throws IOException {
      /*  ClassPathFile cpf = new ClassPathFile();
        String[] list = cpf.listFile("electric/util");
        for (int i = 0; i< list.length; i++){
            if (list[i].endsWith("ArrayUtil.class")){
                return;
            }
        }
        fail("Unable to find class in jar");*/
    }
    public void testClassPahtFile() throws IOException {
        ClassPathFile cpf = new ClassPathFile();
        String file = cpf.getFileAsString("sut/xml4Test.xml");
        assertNotNull("Fail to get file", file);
    }

}
