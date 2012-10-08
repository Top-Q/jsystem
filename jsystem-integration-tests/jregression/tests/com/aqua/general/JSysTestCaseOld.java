package com.aqua.general;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystemClient;
import com.aqua.jsystemobject.JSystemEnvControllerOld;
import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.clients.JReporterClient;
import com.aqua.jsystemobject.clients.JScenarioClient;
import com.aqua.jsystemobject.handlers.JServerHandlers;


public abstract class JSysTestCaseOld extends SystemTestCase {
	private RunMode runMode = RunMode.DROP_EVERY_RUN;
	protected JSystemEnvControllerOld envController;
	protected JSystemClient jsystem;
	protected JScenarioClient scenarioClient;
	protected JApplicationClient applicationClient;
	protected JReporterClient reporterClient;
	private String userDir = null;
	
	public JSysTestCaseOld() {
		super();
		setFixture(CreateEnvFixtureOld.class);
	}
	public void setUp() throws Exception{
		envController = (JSystemEnvControllerOld)system.getSystemObject("envControllerOld");
		jsystem = envController.getJSystemEnv();
		applicationClient = (JApplicationClient)envController.getSystemClient(JServerHandlers.APPLICATION);
		scenarioClient = (JScenarioClient)envController.getSystemClient(JServerHandlers.SCENARIO);
		reporterClient = (JReporterClient)envController.getSystemClient(JServerHandlers.REPORTER);
		report.report("jsystem is " + jsystem);
	}
	
	public void tearDown() throws Exception{
		if(jsystem == null){
			return;
		}
		if (!isPass()) {
			jsystem.screenCapture();
		}
	}
	
	protected void backupJSystemProperties() throws Exception{
		userDir = jsystem.getUserDir();
		File orig = new File(userDir, CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);
		File back = new File(orig.getParentFile(), CommonResources.JSYSTEM_PROPERTIES_FILE_NAME+".back");
		report.report("Backing up "+ orig.getAbsolutePath() +" To "+back.getAbsolutePath());
		FileUtils.copyFile(orig, back);
	}
	
	protected void restoreJSystemProperties() throws Exception{
		if(userDir == null){
			return;
		}
		File orig = new File(userDir, CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);
		File back = new File(orig.getParentFile(),CommonResources.JSYSTEM_PROPERTIES_FILE_NAME+".back");
		report.report("Restoring from "+ back.getAbsolutePath() +" To "+orig.getAbsolutePath());
		FileUtils.copyFile(back, orig);
	}
	
	protected Properties getRunProperties() throws Exception{
		userDir = jsystem.getUserDir();
		Properties prop = FileUtils.loadPropertiesFromFile(userDir + File.separator + CommonResources.RUN_PROPERTIES_FILE_NAME);
		filterSystemProps(prop);
		return prop;
	}
	
	/**
	 * Filters system properties from .run.properties file.
	 */
	private static void filterSystemProps(Properties props){
		report.report("about to remove keys which begin with \"jsystem.\" or \"summary.\"");
		Enumeration<Object> e = props.keys();
		while (e.hasMoreElements()){
			String key = e.nextElement().toString();
			report.report("key = "+key);
			if (key.startsWith("jsystem.") || key.startsWith("summary.")){
				props.remove(key);
			}
		}
	}
	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}
}
