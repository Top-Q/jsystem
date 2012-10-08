package com.aqua.base;

import org.junit.After;
import org.junit.Before;

import com.aqua.fixtures.NewActivateRunnerFixture;
import com.aqua.jsystemobjects.ClientHandlerType;
import com.aqua.jsystemobjects.JSystemEnvController;
import com.aqua.jsystemobjects.clients.JApplicationClient;
import com.aqua.jsystemobjects.clients.JRemoteInformationClient;
import com.aqua.jsystemobjects.clients.JReporterClient;
import com.aqua.jsystemobjects.clients.JScenarioClient;
import com.aqua.jsystemobjects.clients.JTestsTreeClient;

public class JSysTestCase4UseExistingServer extends JSysTestCase4 {

	public JSysTestCase4UseExistingServer() {
		super();
		setFixture(NewActivateRunnerFixture.class);
	}

	@Before
	public void setUp() throws Exception {
		envController = (JSystemEnvController) system.getSystemObject("envController");
		applicationClient = (JApplicationClient) envController.getSystemClient(ClientHandlerType.APPLICATION);
		scenarioClient = (JScenarioClient) envController.getSystemClient(ClientHandlerType.SCENARIO);
		testsTreeClient = (JTestsTreeClient) envController.getSystemClient(ClientHandlerType.TESTS_TREE);
		reporterClient = (JReporterClient) envController.getSystemClient(ClientHandlerType.REPORTER);
		remoteInformationClient = (JRemoteInformationClient) envController
				.getSystemClient(ClientHandlerType.REMOTEINFO);
	}

	@After
	public void tearDown() throws Exception {

	}
}
