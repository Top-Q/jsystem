package com.aqua.jsystemobjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jsystem.framework.DBProperties;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

import org.jsystem.environment.EnvironmentController;
import org.jsystem.objects.clients.ClientBasic;

import com.aqua.jsystemobjects.clients.BaseClient;
import com.aqua.jsystemobjects.clients.JApplicationClient;
import com.aqua.jsystemobjects.clients.JRemoteInformationClient;
import com.aqua.jsystemobjects.clients.JReporterClient;
import com.aqua.jsystemobjects.clients.JScenarioClient;
import com.aqua.jsystemobjects.clients.JTestsTreeClient;
import com.aqua.utils.Commons;

public class JSystemEnvController extends EnvironmentController {
	private static Logger log = Logger.getLogger(JSystemEnvController.class.getName());
	private String envName = "runnerout";
	private String testFolder;
	private String testSrc;
	private String regressionPath = null;
	// private int foundPort = -1;
	private File envDir = null;
	private File distRunOut = null;
	private File jsystemPropertiesFile = null;
	private File runnerOutJarListProperties = null;
	private File dbProperties = null;
	private boolean jarListFileExist = false;
	private static String[] REQUIRED_JARS;
	private static String[] JSYSTEM_JARS;
	private static String[] JSYSTEM_THIRDPARTY;
	/**
	 * Used to deploy the jsystem from Set from the SUT file
	 */
	private String runnerOutDir = null;
	private String runnerSourceDir = null;
	private String regressionSourceDir = null;
	/**
	 * if set in sut then used for debugging the remote runner
	 */
	private String debugRemoteRunnerPort = "";

	/**
	 * if set in sut then used for debugging the remote runner tests
	 */
	private String debugRemoteTestPort = "";

	public void init() throws Exception {
		super.init();
		if (runnerOutDir != null && runnerOutDir.endsWith(File.separator)) {
			// cut out the seperator
			runnerOutDir = runnerOutDir.substring(0, runnerOutDir.length() - 1);
		}
		envDir = new File(runnerOutDir, envName);// runner+"runnerout"
		distRunOut = new File(envDir, "runner");// runner/runnerout/runner
		jsystemPropertiesFile = new File(distRunOut, CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);
//		dbProperties = new File(distRunOut, DBProperties.DB_PROPERTIES_FILE);
		// if running the tests from eclipse
		if (System.getProperty("user.dir").contains("newJregression")) {
			regressionPath = System.getProperty("user.dir");
			// if running from runner
		} else {
			if (runnerSourceDir == null) {
				runnerSourceDir = System.getProperty("user.dir");
				report.report("runner source dir is " + runnerSourceDir);
			} else if (runnerSourceDir.endsWith(File.separator)) {
				runnerSourceDir = runnerSourceDir.substring(0, runnerSourceDir.length() - 1);
			}
			Properties ps = FileUtils.loadPropertiesFromFile(runnerSourceDir + File.separatorChar
					+ CommonResources.JSYSTEM_PROPERTIES_FILE_NAME);
			regressionPath = ps.getProperty(FrameworkOptions.TESTS_CLASS_FOLDER.toString());
			if (regressionPath.contains("newJregression")) {// test that
															// regressionPath
															// from
															// jsystem.properties
															// is correct.
				regressionPath = regressionPath.replace("classes", "");
			} else {
				throw new Exception("switch project to newJregression");
			}
		}
		runnerOutJarListProperties = new File(regressionPath, "runnerOutJarList.properties");
		jarListFileExist = runnerOutJarListProperties.exists();
	}

	@Override
	protected String buildClassPath() {
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

			buf.append(File.separatorChar + allJars.elementAt(i).getName());
			if (i != (allJars.size() - 1)) {
				buf.append(File.pathSeparatorChar);
			}
		}
		// add the testsProject/classes dir to classpath so tests in it will be
		// found
		// by remote runner.
		String testProject = new String(File.pathSeparator + envDir.getAbsolutePath() + File.separatorChar
				+ "testsProject" + File.separatorChar + "classes");
		buf.append(testProject);
		return buf.toString();
	}

	public ClientBasic getSystemClient(ClientHandlerType type) throws Exception {
		BaseClient client = null;
		if (type.equals(ClientHandlerType.APPLICATION)) {
			client = (BaseClient) new JApplicationClient(getXmlRpcHelper());
		} else if (type.equals(ClientHandlerType.SCENARIO)) {
			client = (BaseClient) new JScenarioClient(getXmlRpcHelper());
		} else if (type.equals(ClientHandlerType.REPORTER)) {
			client = (BaseClient) new JReporterClient(getXmlRpcHelper());
		} else if (type.equals(ClientHandlerType.REMOTEINFO)) {
			client = (BaseClient) new JRemoteInformationClient(getXmlRpcHelper());
		} else if (type.equals(ClientHandlerType.TESTS_TREE)) {
			client = (BaseClient) new JTestsTreeClient(getXmlRpcHelper());
		} else {
			report.report("must specify a client type that exists");
			throw new Exception("must specify a client type that exists");
		}
		client.setP(getP());
		client.setPort(getPortNumber());
		client.setUserDir(distRunOut.getAbsolutePath());
		return client;
	}

	public File getApplicationRootDirectory() throws Exception {
		return distRunOut;
	}

	/**
	 * 
	 * @param folder
	 * @param sutFileName
	 * @throws Exception
	 */
	public void setRunnerEnvTestFolder(String folder, String sutFileName) throws Exception {
		File testProject = new File(envDir, "testsProject");
		FileUtils.copyDirectory(new File(folder), testProject);
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
	 * 
	 * @param toParse
	 * @return
	 */
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

	/**
	 * Build the runner environment Should be called before creating the
	 * jsystem.
	 * 
	 * @throws Exception
	 */
	public void buildRunnerEnv() throws Exception {
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

			// if runnerSourceDir isn't supplied then the runner source dir will
			// be set to the current working directory which in runner
			// standalone run
			// is the runner itself as the source for jars
			// when running from eclipse must set the runnerSourceDir property.
			if (runnerSourceDir == null) {
				runnerSourceDir = System.getProperty("user.dir");
			}
			// if no regression source dir is given, then the regression source
			// will be set as the regressionPath either from jsystem.properties
			// test.dir
			// or from the current directory in eclipse.
			if (regressionSourceDir == null) {
				regressionSourceDir = regressionPath;
			}
			// if given copy the the jar from the given regression project.
			report.report("copying newJregression.jar from " + regressionSourceDir + File.separatorChar + "lib"
					+ File.separator + "newJregression.jar" + " to ");
			report.report(distRunOut + File.separator + "lib" + File.separator + "newJregression.jar");
			FileUtils.copyFile(new File(regressionSourceDir + File.separatorChar + "lib" + File.separator
					+ "newJregression.jar"), new File(distRunOut + File.separator + "lib" + File.separator
					+ "newJregression.jar"));
			for (int i = 0; i < REQUIRED_JARS.length; i++) {
				FileUtils.copyFile(new File(runnerSourceDir + File.separator + "thirdparty" + File.separator + "lib"
						+ File.separator + REQUIRED_JARS[i]), new File(thirdpartyLib, REQUIRED_JARS[i]));
			}

			for (int i = 0; i < JSYSTEM_THIRDPARTY.length; i++) {
				FileUtils.copyFile(new File(runnerSourceDir + File.separator + "thirdparty" + File.separator
						+ "commonLib" + File.separator + JSYSTEM_THIRDPARTY[i]), new File(thirdpartyCommonLib,
						JSYSTEM_THIRDPARTY[i]));
			}
			for (int i = 0; i < JSYSTEM_JARS.length; i++) {
				FileUtils.copyFile(
						new File(runnerSourceDir + File.separator + "lib" + File.separator + JSYSTEM_JARS[i]),
						new File(outLib, JSYSTEM_JARS[i]));
			}
			FileUtils.copyDirectory(new File(runnerOutDir + File.separator + "thirdparty" + File.separator + "ant"),
					ant);
		}
		// if no file was given, copy all jar hirarchy from runner to runnerout,
		// and copy the newJregression jar from newJregression/lib
		else if (!jarListFileExist) {
			FileUtils.copyDirectory(runnerSourceDir + File.separator + "lib", distRunOut.getAbsolutePath()
					+ File.separatorChar + "lib");
			FileUtils.copyDirectory(runnerSourceDir + File.separator + "thirdparty", distRunOut.getAbsolutePath()
					+ File.separatorChar + "thirdparty");
			FileUtils.copyFile(regressionPath + File.separatorChar + "lib" + File.separatorChar + "newJregression.jar",
					distRunOut.getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar
							+ "newJregression.jar");
		}
		/*
		 * Delete the jsystem.properties file
		 */
		if (jsystemPropertiesFile.exists()) {
			jsystemPropertiesFile.delete();
		}
		setInitialJsystemProperties();

		/*
		 * Delete the db.properties file
		 */
//		if (dbProperties.exists()) {
//			dbProperties.delete();
//		}
//		FileUtils.copyFile(new File(runnerSourceDir + File.separator + DBProperties.DB_PROPERTIES_FILE),
//				new File(envDir.getAbsolutePath() + File.separator + "runner" + File.separator
//						+ DBProperties.DB_PROPERTIES_FILE));

		/*
		 * Copy Run Runner activation files
		 */
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "run.bat"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "run.bat"));
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "run"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "run"));
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "runBase.bat"),
				new File(envDir.getAbsolutePath() + File.separator + "runner" + File.separator + "runBase.bat"));
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "runBase"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runBase"));
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "runAgent.bat"),
				new File(envDir.getAbsolutePath() + File.separator + "runner" + File.separator + "runAgent.bat"));
		FileUtils.copyFile(new File(runnerSourceDir + File.separator + "runAgent"), new File(envDir.getAbsolutePath()
				+ File.separator + "runner" + File.separator + "runAgent"));
	}

	public void setInitialJsystemProperties() throws Exception {
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()) {
			JSystemProperties.getInstance().setPreference(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * envController.getJSystemEnv(); call the remote JServer exit method using
	 * the xmlrpc.
	 * 
	 * @throws Exception
	 * 
	 * @throws Exception
	 */
	public void kill() throws Exception {
		report.report("killing remote process");
		if (getP() != null) {
			stopXmlRpcServer();
		}
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

	private String[] createJarArray(String jars) {
		return jars.split(":");
	}

	// ******************************* SUT getters and setter
	// *********************************************
	public String getRunnerOutDir() {
		return runnerOutDir;
	}

	public void setRunnerOutDir(String runnerOutDir) {
		this.runnerOutDir = runnerOutDir;
	}

	public String getRunnerSourceDir() {
		return runnerSourceDir;
	}

	public void setRunnerSourceDir(String runnerSourceDir) {
		this.runnerSourceDir = runnerSourceDir;
	}

	public String getRegressionSourceDir() {
		return regressionSourceDir;
	}

	public void setRegressionSourceDir(String regressionSourceDir) {
		this.regressionSourceDir = regressionSourceDir;
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

	@Override
	public String getJVMArguments() throws Exception {
		if (!StringUtils.isEmpty(debugRemoteRunnerPort)) {
			String[] values = parseDebugString(debugRemoteRunnerPort);
			return "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=" + values[0]
					+ ",server=y,suspend=" + values[1];
		} else {
			return "";
		}

	}

}
