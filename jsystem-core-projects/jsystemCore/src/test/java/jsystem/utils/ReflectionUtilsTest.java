/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import jsystem.framework.system.SystemManagerImpl;
import junit.framework.Assert;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class ReflectionUtilsTest extends SystemTestCase4 {

	/**
	 * Tests basic functionality of reflection utils  
	 */
	@Test
	public void testNewInstance() throws Exception{
		SystemManagerImpl sysManager = (SystemManagerImpl)ReflectionUtils.newInstance(SystemManagerImpl.class, new Object[0]);
		Assert.assertNotNull(sysManager);
		MyClass mc = (MyClass)ReflectionUtils.newInstance(MyClass.class, new Object[]{"4",5});
		Assert.assertEquals("4",ReflectionUtils.getField("str", MyClass.class).get(mc));
		Assert.assertEquals(5,ReflectionUtils.getField("i", MyClass.class).get(mc));
	}

}

class MyClass {
	private String str;
	private int i;
	private MyClass(String s1,int i){
		str = s1;
		this.i=i;
	}
}