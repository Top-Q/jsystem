/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.JavaExecute;

/**
 * Utility class that uses to explore the default value of an object.
 *  
 * @author guy.arieli
 */
public class BeanDefaultsExtractor {
	private static Logger log = Logger.getLogger(BeanDefaultsExtractor.class.getName());
	public static Properties getBeanDefaults(Class<?> c, String...properties) throws Exception{
		String[] args = new String[properties.length + 1];
		boolean debug = false;
		String debugString = "-classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y";
		String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
		if (null != vmParams) {
			debug = true;
			JSystemProperties.getInstance().removePreference(FrameworkOptions.TEST_VM_PARMS);
		}
		System.arraycopy(properties, 0, args, 1, properties.length);
		args[0] = c.getName();
		Command command = JavaExecute.javaExecute(BeanDefaultsExtractor.class, new Properties(), args);
		int returnCode = command.getProcess().waitFor();
		if(debug)
			JSystemProperties.getInstance().setPreference(FrameworkOptions.TEST_VM_PARMS, debugString);
		if(returnCode != 0){
			log.warning("Fail to process " + c.getName());
			throw new Exception(command.getStd().toString());
		}
		
		Properties prop = new Properties();
		prop.load(new StringReader(command.getStdout().toString()));
		return prop;
	}
	
	public static void main(String...args) throws Exception{
		File logFile = new File("log.txt");
		FileUtils.write(logFile, "Start\n", false);
		String className = args[0];
		try {
			if(args.length < 2){
				return;
			}
			Class<?> c = Class.forName(className);
			Object o = c.newInstance();
			String[]classProperties = new String[args.length -1];
			System.arraycopy(args, 1, classProperties, 0, classProperties.length);
			Properties properties = new Properties();
			FileUtils.write(logFile, "Go over the properties\n", true);

			for(String property: classProperties){
				FileUtils.write(logFile, "process: " + property + "\n", true);
				
				Method m = null;
				try {
					m = c.getMethod("get" + property, new Class<?>[0]);
				} catch (Exception retry){
					m = c.getMethod("is" + property, new Class<?>[0]);
				}
				if(m == null){
					continue;
				}
				Object ro = m.invoke(o);
				if(ro != null){
					properties.setProperty(property, ro.toString());
				}
				FileUtils.write(logFile, "end: " + property + "\n", true);
			}
			properties.store(System.out, null);
		} catch (Exception e) {
			FileUtils.write(logFile, "Ex: " + StringUtils.getStackTrace(e) + "\n", true);
			e.printStackTrace();
			System.exit(1);
		}
		FileUtils.write(logFile, "end\n", true);
		System.exit(0);
	}
}
