package com.aqua.base;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;

import com.aqua.fixtures.CreateEnvFixture;
import com.aqua.jsystemobjects.ClientHandlerType;
import com.aqua.jsystemobjects.JSystemEnvController;
import com.aqua.jsystemobjects.clients.JApplicationClient;
import com.aqua.jsystemobjects.clients.JRemoteInformationClient;
import com.aqua.jsystemobjects.clients.JReporterClient;
import com.aqua.jsystemobjects.clients.JScenarioClient;
import com.aqua.jsystemobjects.clients.JTestsTreeClient;


public abstract class JSysTestCase4 extends SystemTestCase4 {
	private RunMode runMode = RunMode.DROP_EVERY_RUN;
	protected JSystemEnvController envController;
	protected JScenarioClient scenarioClient;
	protected JTestsTreeClient testsTreeClient;
	protected JApplicationClient applicationClient;
	protected JReporterClient reporterClient;
	protected JRemoteInformationClient remoteInformationClient;
	private String userDir = null;
	
	public JSysTestCase4() {
		super();
		setFixture(CreateEnvFixture.class);
	}
	
	@Before
	public void setUp() throws Exception{
		envController = (JSystemEnvController)system.getSystemObject("envController");
		envController.startXmlRpcServer();
		applicationClient = (JApplicationClient)envController.getSystemClient(ClientHandlerType.APPLICATION);
		scenarioClient = (JScenarioClient)envController.getSystemClient(ClientHandlerType.SCENARIO);
		reporterClient = (JReporterClient)envController.getSystemClient(ClientHandlerType.REPORTER);
		applicationClient.launch();
	}
	
	/**
	 * in every test teardown, call the environment kill method to
	 * kill the server and remove it's process in an orderly fashion.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception{
		applicationClient.closeApp();
		envController.kill();
	}
	
	protected void backupJSystemProperties() throws Exception{
		userDir = applicationClient.getUserDir();
		File orig = new File(userDir, "jsystem.properties");
		File back = new File(orig.getParentFile(), "jsystem.properties.back");
		FileUtils.copyFile(orig, back);
	}
	
	protected void restoreJSystemProperties() throws Exception{
		if(userDir == null){
			return;
		}
		File orig = new File(userDir, "jsystem.properties");
		File back = new File(orig.getParentFile(), "jsystem.properties.back");
		FileUtils.copyFile(back, orig);
	}
	
	protected Properties getRunProperties() throws Exception{
		userDir = applicationClient.getUserDir();
		File orig = new File(userDir, CommonResources.RUN_PROPERTIES_FILE_NAME);
		Properties prop = new Properties();
		FileInputStream ins = new FileInputStream(orig);
		try {
			prop.load(ins);
		}finally{
			ins.close();
		}
		return prop;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}
}
