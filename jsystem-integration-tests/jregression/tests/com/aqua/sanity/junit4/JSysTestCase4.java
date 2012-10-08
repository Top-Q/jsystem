package com.aqua.sanity.junit4;

import org.junit.Before;

import junit.framework.SystemTestCase4;

import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemEnvControllerOld;

public class JSysTestCase4 extends SystemTestCase4 {
	protected JSystemEnvControllerOld envController;
	protected JSystemClient jsystem;
	
	public JSysTestCase4() {
		super();
		setFixture(CreateEnvFixtureOld.class);
	}
	
	@Before
	public void before() throws Exception{
		envController = (JSystemEnvControllerOld)system.getSystemObject("envController");
		jsystem = envController.getJSystemEnv();
		report.report("jsystem is " + jsystem);
	}
}
