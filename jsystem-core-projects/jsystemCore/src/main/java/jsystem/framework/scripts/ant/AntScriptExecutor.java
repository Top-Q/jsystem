/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scripts.ScriptExecutor;
import jsystem.utils.AntExecutor;
import jsystem.utils.FileUtils;
import jsystem.utils.exec.Command;

public class AntScriptExecutor extends ScriptExecutor {
	private static Logger log = Logger.getLogger(AntScriptExecutor.class.getName());
	protected String target;
	protected String scriptName;
	Command command;
	public AntScriptExecutor() {
		//
	}


	@Override
	public String getTagName() {
		return scriptName + "." + target; 
	}

	public String getScriptName() {
		return scriptName;
	}

	public void configScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getTarget() {
		return target;
	}

	public void configTarget(String target) {
		this.target = target;
	}

	@Override
	public void configTagName(String tag) {
		String[] tags = tag.split("\\.");
		scriptName = tags[0];
		target = tags[1];
	}

	@Override
	protected void startExecute() throws Throwable {
		report.report("Exexute script: " + scriptName +", target: " + target);
		/*
		 * Check that the file exist
		 */
		File buildFile = new File(JSystemProperties.getCurrentTestsPath() + File.separatorChar + filePath);
		if(!buildFile.exists()){
			throw new FileNotFoundException("File not found: " + buildFile.getAbsolutePath());
		}
		
		/*
		 * Execute the build process
		 */
		command = AntExecutor.executeAnt(CommonResources.getAntDirectory(), buildFile, getParametersAsProperties(), target, false);
		
		/*
		 * Init the stdout and stderr
		 */
		stdout = new BufferedReader(new InputStreamReader(command.getProcess().getInputStream()));
		stderr = new BufferedReader(new InputStreamReader(command.getProcess().getErrorStream()));
	}

	@Override
	protected void waitForExecuteEnd() throws Throwable {
		/*
		 * What for the and process to end
		 */
		command.getProcess().waitFor();
	}

	/**
	 * Convect the parameters to properties
	 * @return
	 */
	private Properties getParametersAsProperties(){
		Properties p = new Properties();
		Iterator<String> iter = parameters.keySet().iterator();
		while (iter.hasNext()){
			String key = iter.next();
			Object value = parameters.get(key).getValue();
			if(value == null){
				continue;
			}
			String v = null;
			v = value.toString();
			if(v.equals("")){
				continue;
			}
			p.setProperty(key, v);
		}
		return p;
	}


	@Override
	public void initParamsFromFile() {
		/*
		 * Check that the file exist
		 */
		File buildFile = new File(JSystemProperties.getCurrentTestsPath() + File.separatorChar + filePath);
		if(!buildFile.exists()){
			log.warning("File not found: " + buildFile.getAbsolutePath());
			return;
		}
		try {
			/*
			 * Extract all the ant params in the format ${blabla}
			 */
			String antString = FileUtils.read(buildFile);
			Pattern p = Pattern.compile("\\$\\{(.+)\\}");
			Matcher m = p.matcher(antString);
			int from = 0;
			while(m.find(from)){
				from = m.end();
				String key = m.group(1);
				Parameter param = new Parameter();
				param.setName(key);
				param.setType(ParameterType.STRING);
				parameters.put(key, param);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	protected void processStdOutLine(String line) {
		if(line != null){
			int stepIndex = line.indexOf("step:");
			if(stepIndex >= 0){
				report.step(line.substring(stepIndex + 5).trim());
			} else {
				report.report(line);
			}
		}
	}

}
