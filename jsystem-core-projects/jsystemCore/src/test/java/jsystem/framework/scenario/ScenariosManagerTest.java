/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import junit.framework.Assert;
import junit.framework.SystemTestCase4;

import org.junit.Test;

public class ScenariosManagerTest extends SystemTestCase4{

	@Test
	public void testIsScenarioExists() throws Exception{
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, "emtpy");
		Assert.assertTrue(ScenariosManager.getInstance().isScenarioExists("scenarios\\jsystemCoreSanity"));
	}

}
