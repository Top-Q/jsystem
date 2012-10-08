/*
 * Created on 22/11/2006
 *
 * Copyright 2005 AQUA Software, LTD. All rights reserved.
 * AQUA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.aqua.jsystemobject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import utils.Commons;

import jsystem.framework.DBProperties;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import com.aqua.jsystemobject.clients.BaseClient;
import com.aqua.jsystemobject.clients.JApplicationClient;
import com.aqua.jsystemobject.handlers.JServerHandlers;

public class JSystemEnvControllerOld extends SystemObjectImpl implements TestListener {

	private static Logger log = Logger.getLogger(JSystemEnvControllerOld.class.getName());

	private static String[] REQUIRED_JARS;
	private static String[] JSYSTEM_JARS;
	private static String[] JSYSTEM_THIRDPARTY;
	private boolean jarListFileExist;
	private File runnerOutJarListProperties;
	private File envDir = null;
	private File distRunOut = null;
	private String testFolder = null;
	private String testSrc = null;
	private String regressionPath = null;
	private JSystemClient jsysClient;
	private JApplicationClient appClient;
	private String envName = "runnerout";
	private File jsystemPropertiesFile = null;
	private Process p;
	private int foundPort;

	/**
	 * Some tests needs the location of the regression base tests folder. For
	 * example the JarListTests.
	 */
	private String regressionBaseTestsFolder;
	/**
	 * If set to true will not deploy the application but will use an existing
	 * one.
	 */
	static boolean useExistingServer = false;

	/**
	 * Used to deploy the jsystem from Set from the SUT file
	 */
	String runnerOutDir = null;
	String runnerSourceDir = null;

	/**
	 * if set in sut then used for debugging the remote runner
	 */
	String debugRemoteRunnerPort = "";

	/**
	 * if set in sut then used for debugging the remote runner tests
	 */
	String debugRemoteTestPort = "";

	public enum EnumRunMode {
		DropEveryRun, DropEveryTest;
	}

	/**
	 * checks whether the test is running from the eclipse platform or from a
	 * stand alone runner. if from eclipse the jar list file should be found
	 * under the project directory and if not, the location for the regression
	 * folder will be taken from jsystem.properties, in the tests.dir property,
	 * since the JSystemEnvController will only be called if running a
	 * regression project. in both cases checking if the file exists, will set a
	 * boolean.
	 */
	public void init() throws Exception {
		super.init();
		envDir = new File(runnerOutDir, envName);
		distRunOut = new File(envDir, "runner");
		jsystemPropertiesFile = new File(distRunOut, CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);

		// if running the tests from with in jregression under eclipse
		// then look for runnerOutJarList.properties in user.dir
		if (System.getProperty("user.dir").contains("jregression")) {
			regressionPath = System.getProperty("user.dir");
		} else {
			Properties ps = FileUtils.loadPropertiesFromFile(runnerOutDir
					+ CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);
			regressionPath = ps.getProperty(FrameworkOptions.TESTS_CLASS_FOLDER.toString());
			regressionPath = regressionPath.substring(0, regressionPath.length() - 7); // if
																						// value
																						// is
																						// set,
																						// cut
																						// off
																						// the
																						// classes

		}
		runnerOutJarListProperties = new File(regressionPath, "runnerOutJarList.properties");
		jarListFileExist = runnerOutJarListProperties.exists();
	}

	public JSystemClient getJSystemEnv() throws Exception {
		String classPath = buildClassPath();
		if (jsystemPropertiesFile.exists()) {
			Properties p = FileUtils.loadPropertiesFromFile(jsystemPropertiesFile.getAbsolutePath());
			String tfolde = p.getProperty("tests.dir");
			if (tfolde != null) {
				classPath = tfolde + File.pathSeparatorChar + classPath;
			}
		}
		if (!useExistingServer) {
			/*
			 * Find available port
			 */
			ServerSocket ss = new ServerSocket(0);
			foundPort = ss.getLocalPort();
			ss.close();

			String[] command = null;
			if (!StringUtils.isEmpty(debugRemoteRunnerPort)) {
				String[] values = parseDebugString(debugRemoteRunnerPort);
				command = new String[] { "java", "-Xdebug", "-Xnoagent", "-Djava.compiler=NONE",
						"-Xrunjdwp:transport=dt_socket,address="+values[0]+",server=y,suspend=" + values[1],
						"-classpath", classPath, JServer.class.getName(), Integer.toString(foundPort) };
			} else {
				command = new String[] { "java", "-classpath", classPath, JServer.class.getName(),
						Integer.toString(foundPort) };
			}

			log.info("Execute runner with command: " + StringUtils.objectArrayToString(" ", (Object[]) command));
			report.report("Connection port: " + foundPort);
			p = Runtime.getRuntime().exec(command, null, distRunOut);
			(new SystemOutReader(p.getInputStream(), distRunOut.getAbsolutePath() + File.separatorChar + "out.txt"))
					.start();
			(new SystemOutReader(p.getErrorStream(), distRunOut.getAbsolutePath() + File.separatorChar + "error.txt"))
					.start();
			JSystemClient jsystem = new JSystemClient(p, foundPort, distRunOut.getAbsolutePath());
			XmlHandler.getInstance().setPort(foundPort);// make XmlHandler work
			// on the right port
			if (jsysClient != null) { // kill old clients
				jsysClient.exit();
				report.report("Old Client was terminated");
			}

			jsysClient = jsystem;
			Thread.sleep(5000);

			return jsystem;
		} else {
			return jsysClient;
		}
	}

	private String[] parseDebugString(String toParse) {
		String[] toReturn = new String[2];
		String[] values = toParse.split(CommonResources.DELIMITER);
		if (values.length == 2) {
			return values;
		}
		toReturn[0] = values[0];
		toReturn[1] = "y";

		return toReturn;
	}

	public BaseClient getSystemClient(JServerHandlers handler) throws InstantiationException, IllegalAccessException {
		BaseClient clientClass = (BaseClient) handler.getClientClass().newInstance();
		clientClass.setPort(this.foundPort);
		clientClass.setServerProcess(p);
		clientClass.setUserDir(distRunOut.getAbsolutePath());
		if (clientClass instanceof JApplicationClient) {
			appClient = (JApplicationClient) clientClass;
		}
		return clientClass;
	}

	/**
	 * Kill all the jsystem opened
	 * 
	 * @throws Exception
	 */
	public void kill() {
		try {
			if (useExistingServer) {
				return;
			}
			if (appClient != null) {
				appClient.exit();
				appClient = null;
				jsysClient = null;
				return;
			}
			if (jsysClient != null) {
				jsysClient.exit();
				jsysClient = null;
			}
		} catch (Exception e) {
			report.report("didn't succeed in exiting jsysClient or appClient inside" + "\n kill",
					StringUtils.getStackTrace(e), false);
		}
	}

	public void extract(String envZipPath) throws Exception {
		report.report("extract environemnt: " + envZipPath);
		String zipPath = JSystemProperties.getCurrentTestsPath() + File.separatorChar + envZipPath;
		FileUtils.extractZipFile(new File(zipPath), envDir);
	}

	/**
	 * Build the runner environment Should be called before creating the
	 * jsystem.
	 * 
	 * @throws Exception
	 */
	public void buildRunnerEnv() throws Exception {

		if (!useExistingServer) {
			report.report("Build runner env");
			kill();
			// Remove the runner env folder
			clean();
			/*
			 * Copy the runner to the env dir
			 */
			if (jarListFileExist) {
				// create lists of jars for environment build
				Properties p = FileUtils.loadPropertiesFromFile(runnerOutJarListProperties.getAbsolutePath());
				String requiredJars = p.getProperty("thirdpartyLib");
				REQUIRED_JARS = createJarArray(requiredJars);
				String jsystemJars = p.getProperty("lib");
				JSYSTEM_JARS = createJarArray(jsystemJars);
				String jsystemThirdparty = p.getProperty("thirdpartyCommonLib");
				JSYSTEM_THIRDPARTY = createJarArray(jsystemThirdparty);

				File outLib = new File(distRunOut, "lib");
				File thirdparty = new File(distRunOut, "thirdparty");
				File thirdpartyLib = new File(thirdparty, "lib");
				File thirdpartyCommonLib = new File(thirdparty, "commonLib");
				File ant = new File(thirdparty, "ant");
				if (runnerSourceDir == null) {
					for (int i = 0; i < REQUIRED_JARS.length; i++) {
						FileUtils
								.copyFile(findInClassPath(REQUIRED_JARS[i]), new File(thirdpartyLib, REQUIRED_JARS[i]));
					}

					for (int i = 0; i < JSYSTEM_THIRDPARTY.length; i++) {
						FileUtils.copyFile(findInClassPath(JSYSTEM_THIRDPARTY[i]), new File(thirdpartyCommonLib,
								JSYSTEM_THIRDPARTY[i]));
					}
					for (int i = 0; i < JSYSTEM_JARS.length; i++) {
						FileUtils.copyFile(findInClassPath(JSYSTEM_JARS[i]), new File(outLib, JSYSTEM_JARS[i]));
					}
					FileUtils.copyDirectory(new File(runnerOutDir + File.separator + "thirdparty/ant"), ant);
				} else {
					File regressionJarTemp = new File(runnerSourceDir + "lib" + File.separator + "jregression.jar");

					FileUtils.copyFile(findInClassPath("jregression.jar"), regressionJarTemp);// of
																								// the
																								// jsystem_jars
																								// can
																								// find
																								// it.

					for (int i = 0; i < REQUIRED_JARS.length; i++) {
						FileUtils.copyFile(new File(runnerSourceDir + "thirdparty" + File.separator + "lib"
								+ File.separator + REQUIRED_JARS[i]), new File(thirdpartyLib, REQUIRED_JARS[i]));
					}

					for (int i = 0; i < JSYSTEM_THIRDPARTY.length; i++) {
						FileUtils.copyFile(new File(runnerSourceDir + "thirdparty" + File.separator + "commonLib"
								+ File.separator + JSYSTEM_THIRDPARTY[i]), new File(thirdpartyCommonLib,
								JSYSTEM_THIRDPARTY[i]));
					}
					for (int i = 0; i < JSYSTEM_JARS.length; i++) {
						FileUtils.copyFile(new File(runnerSourceDir + "lib" + File.separator + JSYSTEM_JARS[i]),
								new File(outLib, JSYSTEM_JARS[i]));
					}
					FileUtils.copyDirectory(new File(runnerOutDir + File.separator + "thirdparty" + File.separator
							+ "ant"), ant);
					FileUtils.deleteFile(regressionJarTemp.getAbsolutePath());
				}
			}
			// if no file was given, copy all jar hirarchy from runner to
			// runnerout,
			// and copy the jregression jar from jregression/lib
			else if (!jarListFileExist) {
				FileUtils
						.copyDirectory(runnerOutDir + "lib", distRunOut.getAbsolutePath() + File.separatorChar + "lib");
				FileUtils.copyDirectory(runnerOutDir + "thirdparty", distRunOut.getAbsolutePath() + File.separatorChar
						+ "thirdparty");
				FileUtils.copyFile(
						regressionPath + File.separatorChar + "lib" + File.separatorChar + "jregression.jar",
						distRunOut.getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar
								+ "jregression.jar");
			}
		}
		// ----------------------------------------------------------------------
		// ----------------
		/*
		 * Delete the jsystem.properties file
		 */
		if (jsystemPropertiesFile.exists()) {
			jsystemPropertiesFile.delete();
		}

		setInitialJsystemProperties();


		/*
		 * Copy Run Runner activation files
		 */
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "run.bat"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "run.bat"));
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "run"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "run"));
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "runBase.bat"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runBase.bat"));
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "runBase"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runBase"));
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "runAgent.bat"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runAgent.bat"));
		FileUtils.copyFile(new File(runnerOutDir + File.separator + "runAgent"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runAgent"));
	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			JSystemProperties.getInstance().setPreference(entry.getKey(), entry.getValue());
		}
	}

	private File findInClassPath(String jarName) throws FileNotFoundException {
		String cp = System.getProperty("java.class.path");
		String[] jars = cp.split(File.pathSeparator);
		for (String jar : jars) {
			if (jar.toLowerCase().endsWith(File.separator + jarName.toLowerCase())) {
				return new File(jar);
			}
		}
		throw new FileNotFoundException("jar not in classpath: " + jarName);
	}

	public void setRunnerEnvTestFolder(String regressionBaseTestsFolderParam, String sutFileName) throws Exception {
		// create file runner/runnerout/testsProject
		this.regressionBaseTestsFolder = regressionBaseTestsFolderParam;
		File testProject = new File(envDir, "testsProject");
		// copy the directory jregression/resources/jregressoinBaseTests to
		// runnerout/testsProject
		FileUtils.copyDirectory(new File(regressionBaseTestsFolder), testProject);
		this.testFolder = testProject + File.separator + "classes";
		this.testSrc = testProject + File.separator + "tests";
		Properties p = FileUtils.loadPropertiesFromFile(JSystemProperties.getInstance().getPreferencesFile()
				.getAbsolutePath());

		p.setProperty("tests.dir", testFolder);
		p.setProperty("tests.src", testSrc);

		p.setProperty(FrameworkOptions.HTML_ZIP_DISABLE.toString(), true + "");

		if (sutFileName != null) {
			p.setProperty("sutFile", sutFileName);
		}

		if (!StringUtils.isEmpty(debugRemoteTestPort)) { // if debug remote test
															// was flagged
			String[] values = parseDebugString(debugRemoteTestPort);
			p.setProperty(FrameworkOptions.TEST_VM_PARMS.toString(),
					"-classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${"
							+ values[0] + "},server=y,suspend=" + values[1]);
		} else {
			p.remove(FrameworkOptions.TEST_VM_PARMS.toString());
		}

		FileUtils.savePropertiesToFile(p, jsystemPropertiesFile.getAbsolutePath());
	}

	/**
	 * Remove the env folder and recreate it
	 * 
	 * @throws Exception
	 */
	public void clean() throws Exception {
		report.report("clear jsystem environment");
		FileUtils.deltree(envDir);
		envDir.mkdirs();
	}

	private String[] createJarArray(String jars) {
		return jars.split(":");
	}

	private String buildClassPath() {
		if (distRunOut.list() == null || distRunOut.list().length == 0) {
			throw new RuntimeException(
					"Runner under test environment was not created properlly. Please make sure CreateEnvFixture is activated");
		}
		Vector<File> allJars = new Vector<File>();
		FileUtils.collectAllFiles(distRunOut, (new FilenameFilter() {
			public boolean accept(File pathname, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		}), allJars);

		StringBuffer buf = new StringBuffer();
		buf.append(runnerOutDir + File.separatorChar + "classes" + File.pathSeparatorChar);
		for (int i = 0; i < allJars.size(); i++) {
			if (allJars.elementAt(i).getAbsolutePath()
					.contains("runner" + File.separator + "thirdparty" + File.separator + "lib")) {
				buf.append("thirdparty" + File.separator + "lib");
			}
			if (allJars.elementAt(i).getAbsolutePath().contains("runner" + File.separator + "lib")) {
				buf.append("lib");
			}
			if (allJars.elementAt(i).getAbsolutePath()
					.contains("runner" + File.separator + "thirdparty" + File.separator + "commonLib")) {
				buf.append("thirdparty" + File.separator + "commonLib");
			}
			if (allJars
					.elementAt(i)
					.getAbsolutePath()
					.contains(
							"runner" + File.separator + "thirdparty" + File.separator + "ant" + File.separator + "lib")) {
				buf.append("thirdparty" + File.separator + "ant" + File.separator + "lib");
			}
			buf.append(File.separatorChar);
			buf.append(allJars.elementAt(i).getName());
			if (i != (allJars.size() - 1)) {
				buf.append(File.pathSeparatorChar);
			}
		}
		return buf.toString();
	}

	public void close() {
		kill();
		super.close();
	}

	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub

	}

	public void endTest(Test arg0) {
		kill();
	}

	public void startTest(Test arg0) {
		if (useExistingServer) {
			return;
		}
	}

	public String getRunnerOutDir() {
		return runnerOutDir;
	}

	public void setRunnerOutDir(String runnerOutDir) {
		this.runnerOutDir = runnerOutDir;
	}

	public static boolean isUseExistingServer() {
		return JSystemEnvControllerOld.useExistingServer;
	}

	public static void setUseExistingServer(boolean useExistingServer) {
		JSystemEnvControllerOld.useExistingServer = useExistingServer;
	}

	public String getRunnerSourceDir() {
		return runnerSourceDir;
	}

	public void setRunnerSourceDir(String runnerSourceDir) {
		this.runnerSourceDir = runnerSourceDir;
	}

	public String getDebugRemoteRunnerPort() {
		return debugRemoteRunnerPort;
	}

	public void setDebugRemoteRunnerPort(String debugRemoteRunnerPort) {
		this.debugRemoteRunnerPort = debugRemoteRunnerPort;
	}

	public String getDebugRemoteTestPort() {
		return debugRemoteTestPort;
	}

	public void setDebugRemoteTestPort(String debugRemoteTestPort) {
		this.debugRemoteTestPort = debugRemoteTestPort;
	}

	public File getDistRunOut() {
		return distRunOut;
	}

	public void setDistRunOut(File distRunOut) {
		this.distRunOut = distRunOut;
	}

	public String getTestFolder() {
		return testFolder;
	}

	public void setTestFolder(String testFolder) {
		this.testFolder = testFolder;
	}

	public String getRegressionBaseTestsFolder() {
		return regressionBaseTestsFolder;
	}
	

}

class SystemOutReader extends Thread {
	InputStream in;
	String outFile;

	public SystemOutReader(InputStream in, String outFile) {
		this.in = in;
		this.outFile = outFile;
	}

	public void run() {
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			int c;
			while ((c = in.read()) >= 0) {
				System.out.print((char) c);
				fos.write(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
