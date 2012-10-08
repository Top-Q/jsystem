/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Parameter.ParameterType;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTestCase;

/**
 * The executor is used to execute 3'rd party scripts.
 * It should be extended by the spesifc script executor.
 * 
 * @author guy.arieli
 *
 */
public abstract class ScriptExecutor extends SystemTestCase {
	/**
	 * Contain the script parameters
	 */
	protected HashMap<String, Parameter>parameters;
	
	/**
	 * The script tag. A unique id that identify the test.
	 */
	protected String tag;
	
	/**
	 * Script executon standart output
	 */
	protected BufferedReader stdout;
	
	/**
	 * Script execution standart error
	 */
	protected BufferedReader stderr;
	
	/**
	 * Path to the script file (can be used with class loader).
	 */
	protected String filePath;
	
	
	public ScriptExecutor(){
		parameters = new HashMap<String, Parameter>();
	}
	/**
	 * The main script execution method.
	 */
	public void runBare() throws Throwable {
		processParameters();
		
		FixtureManager.getInstance().goTo(getFixture().getName());
		setUp();
		try {
			/*
			 * Init the execution
			 */
			startExecute();
			
			/*
			 * Start the line processing that proces the stdout and stderr.
			 */
			if(stdout != null){
				new LineProcess(stdout, true, this).start();
			}
			if(stderr != null){
				new LineProcess(stderr, false, this).start();
			}
			/*
			 * Wait for execution end
			 */
			waitForExecuteEnd();
		}
		catch (Throwable e) {
			tearDown();
			
			try {
				FixtureManager.getInstance().failTo(getTearDownFixture().getName());
			} catch (Throwable e2) {
				// ignore errors during tear down fixture navigation
			}
			
			throw e;
		}

		tearDown();

		/*
		 * If fail with no exception (only report)
		 */
		if (ListenerstManager.getInstance().getLastTestFailed()) {
			throw new AssertionFailedError(
					"Fail report was submitted");
		}
		
	}
	
	/**
	 * Get the test parameters. Should be used after <code>initParamsFromFile</code>.
	 * @return map of the parameters.
	 */
	public HashMap<String,Parameter> getParameters() {
		return parameters;
	}
	
	/**
	 * Add parameters
	 * @param name the parameter name
	 * @param parameter the parameter object
	 */
	public void addParameter(String name, Parameter parameter) {
		parameters.put(name, parameter);
		
	}
	
	/**
	 * 
	 * @return The test unique tag
	 */
	public String getTagName(){
		return tag;
	}
	/**
	 * Set the test unique tag
	 * @param tag
	 */
	public void configTagName(String tag){
		this.tag = tag;
	}
	/**
	 * Init the parameter map from the input file.
	 * Usualy will process the file and extract all the parameters and there types.
	 *
	 */
	public abstract void initParamsFromFile(); 
	
	/**
	 * Start the test execution. and init the stdout and stderr.
	 * @throws Throwable
	 */
	protected abstract void startExecute() throws Throwable;
	
	/**
	 * Wait for the execution to end
	 * @throws Throwable
	 */
	protected abstract void waitForExecuteEnd() throws Throwable;
	
	/**
	 * Process an stderr line
	 * @param line the line to process
	 */
	protected void processStdErrLine(String line) {
		report.report(line,false);
	}

	/**
	 * Process an stdout line
	 * @param line the line to process
	 */
	protected void processStdOutLine(String line) {
		report.report(line);
	}
	
	/**
	 * process the parameter from the the system environment varables.
	 * @throws Exception
	 */
	public void processParameters(){
		Properties map = System.getProperties();
		Iterator<Object> iter = map.keySet().iterator();
		/*
		 * Extract all the properties start with the prefix (jsystem.params.).
		 */
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key != null && key.startsWith(RunningProperties.PARAM_PREFIX)) {
				Parameter param = new Parameter();
				String value = map.getProperty(key);
				key = key.substring(RunningProperties.PARAM_PREFIX.length());
				param.setName(key);
				param.setType(ParameterType.STRING);
				param.setValue(value);
				addParameter(key, param);
			}
			if(key != null && key.equals(RunningProperties.SCRIPT_TAG)){
				configTagName(map.getProperty(key));
			}
			if(key != null && key.equals(RunningProperties.SCRIPT_PATH)){
				configFilePath(map.getProperty(key));
			}
		}
	}

	/**
	 * Get the file path. for example com/aqua/build/Build.xml
	 * @return the path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Set the file path.
	 * @param filePath
	 */
	public void configFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getTestName() {
		return getTagName();
	}
	
}
/**
 * Used to proecess the stdout/stderr lines
 * @author guy.arieli
 *
 */
class LineProcess extends Thread{
	BufferedReader in;
	boolean stdout;
	ScriptExecutor executor;
	public LineProcess(BufferedReader in, boolean stdout, ScriptExecutor executor){
		this.in = in;
		this.stdout = stdout;
		this.executor = executor;
	}
	
	public void run(){
		String line ;
		if(in == null){
			return;
		}
		try {
			while((line = in.readLine()) != null){
				if(stdout){
					executor.processStdOutLine(line);
				} else {
					executor.processStdErrLine(line);
				}
			}
		} catch (IOException e) {
			// TODO Process the error
		}
	}
}
