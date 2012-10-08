/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.exec;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

public class JavaExecute {

	/**
	 * Utility function to execute external java process.
	 * The current application class path will be used.
	 * @param mainClass
	 * @param additional
	 * @return
	 * @throws Exception
	 */
	public static Command javaExecute(Class<?> mainClass,Properties additional, String...args) throws Exception{
		Command cmd = new Command();
		ArrayList<String> cmdStringArray = new ArrayList<String>();
		String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
		String[] vmParamsArr = new String[0];
		if (vmParams != null) {
			vmParamsArr = vmParams.split(" ");
		}

		cmdStringArray.add(System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java");
		for (int i = 0; i < vmParamsArr.length; i++) {
			cmdStringArray.add(vmParamsArr[i]);
		}
		cmdStringArray.add("-classpath");
		cmdStringArray.add(System.getProperty("java.class.path"));
		if (additional != null) {
			Enumeration<Object> enum1 = additional.keys();
			while (enum1.hasMoreElements()) {
				String key = (String) enum1.nextElement();
				String value = additional.getProperty(key);
				cmdStringArray.add("-D" + key + "=" + value);
			}
		}
		cmdStringArray.add(mainClass.getName());
		if(args != null){
			cmdStringArray.addAll(Arrays.asList(args));
		}
		cmd.setCmd(cmdStringArray.toArray(new String[0]));
		cmd.setDir(new File(System.getProperty("user.dir")));
		Execute.execute(cmd, false, false, true);
		return cmd;
	}
}
