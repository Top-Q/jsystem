/*
 * Created on 05/05/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import jsystem.utils.build.BuildException;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

public class AntExecutor {
	private static Logger log = Logger.getLogger(AntExecutor.class.getName());
	
	public static Command executeAnt(File antHome, File buildFile) throws Exception {
		return executeAnt(antHome, buildFile, null, null);
	}

	public static Command executeAnt(File antHome, File buildFile, Properties prop, String targate) throws Exception {
		return executeAnt(antHome, buildFile, prop, targate, true);
	}
	public static Command executeAnt(File antHome, File buildFile, Properties prop, String targate, boolean block) throws Exception {

		File antLauncher = new File(antHome + File.separator + "lib", "ant-launcher.jar");
		String osName = System.getProperty("os.name");
		String antBatch = null;
		if (osName.toLowerCase().startsWith("windows")) {
			antBatch = "ant.bat";
		} else {
			antBatch = "ant";
		}
		Command command = new Command();
		command.setDir(buildFile.getParentFile());

		File antBin = new File(antHome, "bin");
		File antBat = new File(antBin, antBatch);
		if (!antBat.exists()) {
			command.getStderr().append("Can't find ANT bat file. Please set the ANT_HOME propery");
			return command;
		}
		String[] params = processProperties(prop);
		ArrayList<String> antCommand = new ArrayList<String>();
		antCommand.add(System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java");
		antCommand.add("-classpath");
		antCommand.add(antLauncher.getAbsolutePath());
		antCommand.add("-Dant.home=" + antHome.getAbsolutePath());
		antCommand.add("org.apache.tools.ant.launch.Launcher");
		antCommand.add("-buildfile");
		antCommand.add(buildFile.getAbsolutePath());
		if(params != null){
			for(String param: params){
				antCommand.add(param);
			}
		}

		if (targate != null) {
			antCommand.add(targate);
		}

		command.setCmd(antCommand.toArray(new String[0]));
		/*
		 * Set the ANT_HOME env parameter
		 */
		command.setEnvParams(new String[] { "ANT_HOME=" + antHome.getAbsolutePath() });
		log.info(command.toString());
		Execute.execute(command, block, true, true, block);
		if(block){
			String antOut = command.getStdout().toString() + "\n----------------------------------------\n"
			+ command.getStderr();
			if (antOut.indexOf("BUILD SUCCESSFUL") < 0) {
				BuildException be = new BuildException("Ant build fail");
				be.setAntFailString(antOut);
				throw be;
			}
		}
		
		return command;
	}

	public static Command executeAnt(File antHome, InputStream buildFileInputStream, Properties prop, String targate)
			throws Exception {
		File outFile = File.createTempFile("ant", ".xml");
		FileOutputStream out = new FileOutputStream(outFile);
		int c = -1;
		while ((c = buildFileInputStream.read()) >= 0) {
			out.write(c);
		}
		out.close();
		Command cmd = executeAnt(antHome, outFile, prop, targate);
		outFile.delete();
		return cmd;
	}

	public static String[] processProperties(Properties prop) {
		if (prop == null) {
			return null;
		}
		Enumeration<Object> keys = prop.keys();
		Vector<String> pstr = new Vector<String>();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = prop.getProperty(key);
			if (key.equals("lib")) {
				pstr.addElement("-lib");
				pstr.addElement(value);
				continue;
			}
			pstr.addElement("-D" + key + "=" + value);
		}
		if (pstr.size() == 0) {
			return null;
		}
		String[] toReturn = new String[pstr.size()];
		System.arraycopy(pstr.toArray(), 0, toReturn, 0, toReturn.length);
		return toReturn;
	}
}
