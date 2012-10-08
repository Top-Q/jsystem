package com.aqua.general;

import org.junit.After;
import org.junit.Before;

import com.aqua.jsystemobject.JSystemEnvController;
import com.aqua.jsystemobject.NewActivateRunnerFixture;
import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.clients.JReporterClient;
import com.aqua.jsystemobject.clients.JScenarioClient;
import com.aqua.jsystemobject.handlers.JServerHandlers;


public class JSysTestCase4UseExistingServer extends JSysTestCase4 {
	
	public JSysTestCase4UseExistingServer(){
		super();
		setFixture(NewActivateRunnerFixture.class);
	}
	
	@Before
	public void setUp() throws Exception{
		envController = (JSystemEnvController)system.getSystemObject("envController");
		applicationClient = (JApplicationClient)envController.getSystemClient(JServerHandlers.APPLICATION);
		scenarioClient = (JScenarioClient)envController.getSystemClient(JServerHandlers.SCENARIO);
		reporterClient = (JReporterClient)envController.getSystemClient(JServerHandlers.REPORTER);
	}
	
	@After
	public void tearDown(){
		
	}
}
