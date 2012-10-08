/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.sut;

import junit.framework.SystemTestCase;

public class SutFactoryTest extends SystemTestCase{
    public void testSutWorks() throws Exception{
        assertEquals("Value doesn't much","10.10.10.10", sut().getValue("/sut/device1/ip/text()"));
    }
}
