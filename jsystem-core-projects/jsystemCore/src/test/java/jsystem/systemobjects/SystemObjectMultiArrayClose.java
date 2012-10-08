/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.systemobjects;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import junit.framework.SystemTestCase4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemObjectMultiArrayClose extends SystemTestCase4 {

	private MyNestedSystemObject sysObj;
	@Before
	public void getSysObj() throws Exception{
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, "MyNestedSystemObjectSUT");
		sysObj = (MyNestedSystemObject)system.getSystemObject("nested_sys_obj");
	}
	@Test
	public void systemObjectWithArrayClose() throws Exception {
		sysObj.close();
		for (int i = -1; i <3 ;i ++){
			Assert.assertTrue(MyNestedSystemObject.setOfIndices.contains(i));
		}
	}
}
