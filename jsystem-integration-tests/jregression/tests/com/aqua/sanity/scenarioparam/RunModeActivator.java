package com.aqua.sanity.scenarioparam;

import jsystem.framework.GeneralEnums.RunMode;
import junit.framework.SystemTestCase;

import com.aqua.jsystemobject.ActivateRunnerFixture;

/**
 */
public class RunModeActivator extends SystemTestCase {

	private RunMode runMode = RunMode.DROP_EVERY_RUN;
	
	public RunModeActivator() {
		super();
	}

	public void testSetRunMode(){
		ActivateRunnerFixture.RUN_MODE = runMode;
	}
	
	public RunMode getRunMode() {
		return runMode;
	}
	
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}
}

