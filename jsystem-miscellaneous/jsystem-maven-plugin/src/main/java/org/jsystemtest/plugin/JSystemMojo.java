package org.jsystemtest.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.jsystemtest.plugin.MultipleScenarioSuitExecutionFileParser.Execution;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.RunningProperties;
import jsystem.runner.AntExecutionListener;
import jsystem.utils.StringUtils;

/**
 * 
 * This plug in purpose is to execute JSystem scenarios
 * 
 * @author Itai Agmon
 * 
 * @goal run
 * @requiresProject true
 * 
 * @phase integration-test
 */
public class JSystemMojo extends AbstractMojo {

	private static final String SCENARIO_PATH = "target/classes/scenarios";
	private static final String SUT_PATH = "target/classes/sut";
	private static final String TEST_PROPERTIES_FILE_EMPTY = ".testPropertiesFile_Empty";
	private static final String DEFAULT_REPORTERS = "jsystem.extensions.report.difido.HtmlReporter;jsystem.framework.report.SystemOutTestReporter;jsystem.extensions.report.xml.XmlReporter;jsystem.extensions.report.junit.JUnitReporter";
	private static final String DELIMITER = ",";

	/**
	 * The current project representation.
	 * 
	 * @parameter property="project"
	 * @required
	 * @readonly
	 */
	private MavenProject mavenProject;

	/**
	 * @parameter property="scenario"
	 */
	private String scenario;

	/**
	 * @parameter property="sut"
	 */
	private String sut;

	/**
	 * @parameter property="xmlFile"
	 */
	private String xmlFile;

	/**
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (null == scenario) {
			getLog().error("Please specify a valid scenario name. Scenario can't be " + scenario);
			throw new MojoExecutionException("Please specify a valid scenario name. Scenario can't be " + scenario);
		}
		if (null == sut) {
			getLog().error("Please specify a valid sut name. Sut can't be " + sut);
			throw new MojoExecutionException("Please specify a valid sut name. Sut can't be " + sut);
		}

		getLog().info("Changing user working dir to: " + mavenProject.getBasedir().getAbsolutePath());
		// This line is for setting the current folder to the project root
		// folder. This is very important if we want to run the plug-in from the
		// parent folder.
		System.setProperty("user.dir", mavenProject.getBasedir().getAbsolutePath());

		final File scenariosPath = new File(mavenProject.getBasedir(), SCENARIO_PATH);
		// Collect parameters that are required for the execution
		if (!StringUtils.isEmpty(xmlFile)) {
			xmlFileToParameters();
		}

		final File[] sutFilesArr = sutParameterToFileArray();
		final File[] scenarioFilesArr = scenarioParameterToFileArray(scenariosPath);

		// Check input correction
		if (sutFilesArr == null || sutFilesArr.length == 0 || scenarioFilesArr == null
				|| scenarioFilesArr.length == 0) {
			throw new MojoFailureException("Sut or scenario parameters was not specified");
		}

		if (sutFilesArr.length != scenarioFilesArr.length) {
			throw new MojoFailureException("Number of scenarios must be equals to the number of sut files");
		}

		try {
			// This file is mandatory for scenario execution
			createEmptyTestPropertiesFile(scenariosPath);
		} catch (IOException e) {
			getLog().error("Failed to create new empty scenario properties file");
			getLog().error(e);
			throw new MojoFailureException("Failed to create new empty scenario properties file");
		}

		getLog().info("--------------------------Jsystem Maven Plugin--------------------------");
		getLog().info("About to execute scenarios " + scenario + " with sut files " + sut);
		getLog().info("of project=" + mavenProject.getBasedir());
		getLog().info("------------------------------------------------------------------------");

		for (int i = 0; i < scenarioFilesArr.length; i++) {
			if (!scenarioFilesArr[i].exists()) {
				throw new MojoFailureException("Scenario file " + scenarioFilesArr[i] + " is not exist");
			}
			if (!sutFilesArr[i].exists()) {
				throw new MojoFailureException("Sut file " + sutFilesArr[i] + " is not exist");
			}
			String scenarioName = scenario.split(DELIMITER)[i];
			if (!scenarioName.startsWith("scenarios/")) {
				scenarioName = "scenarios/" + scenarioName;
			}
			final Project p = createNewAntProject(scenariosPath, scenarioFilesArr[i], scenarioName,
					sut.split(DELIMITER)[i]);

			updateJSystemProperties(sutFilesArr[i], sut.split(DELIMITER)[i], scenarioFilesArr[i], scenarioName);
			executeSingleScenario(scenarioFilesArr[i], p);
		}
		getLog().info("------------------------------------------------------------------------");
		getLog().info("Execution of scenarios " + scenario + " ended ");
		getLog().info("Reports can be found in " + mavenProject.getBasedir().getAbsolutePath() + File.separator + "log"
				+ File.separator + "current");

	}

	private void xmlFileToParameters() throws MojoFailureException {
		try {
			MultipleScenarioSuitExecutionFileParser parser = new MultipleScenarioSuitExecutionFileParser(
					new File(xmlFile));
			parser.parse();
			StringBuilder scenarioSb = new StringBuilder();
			StringBuilder sutSb = new StringBuilder();
			for (Execution execution : parser.getExecutions()) {
				scenarioSb.append(execution.getScenario().replaceFirst("\\.xml", "")).append(",");
				sutSb.append(execution.getSut().replaceFirst("sut\\\\", "")).append(",");
			}
			scenario = scenarioSb.toString();
			sut = sutSb.toString();
		} catch (IOException e) {
			throw new MojoFailureException(e.getMessage());
		}
	}

	private void executeSingleScenario(final File scenarioFile, final Project p) {
		getLog().info("Executing scenario " + scenarioFile.getName() + " with sut " + p.getProperty("sutFile"));
		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, scenarioFile);
			p.executeTarget(p.getDefaultTarget());
		} catch (Exception e) {
			getLog().error("Failed to execute scenario " + scenarioFile.getName());
			getLog().error(e);
		} finally {
			p.fireBuildFinished(null);
		}
		getLog().info("Execution of scenario " + scenarioFile.getName() + " with sut " + p.getProperty("sutFile")
				+ " has ended");
		getLog().info("------------------------------------------------------------------------");
	}

	private File[] scenarioParameterToFileArray(File scenariosPath) {
		final List<File> filesList = new ArrayList<File>();
		for (String scenarioName : scenario.split(DELIMITER)) {
			filesList.add(new File(scenariosPath, scenarioName.replaceFirst("scenarios", "") + ".xml"));
		}
		return filesList.toArray(new File[] {});

	}

	private File[] sutParameterToFileArray() {
		final List<File> filesList = new ArrayList<File>();
		for (String sutFileName : sut.split(DELIMITER)) {
			filesList.add(new File(mavenProject.getBasedir() + File.separator + SUT_PATH, sutFileName));
		}
		return filesList.toArray(new File[] {});
	}

	/**
	 * Updates the JSystem properties file with all the data required for the
	 * execution
	 * 
	 * @param sutFile
	 *            - The SUT file to use
	 * @param scenarioFile
	 *            - The scenario to use
	 */
	private void updateJSystemProperties(final File sutFile, final String sutName, final File scenarioFile,
			final String scenarioName) {
		JSystemProperties.getInstance().setPreference(FrameworkOptions.LOG_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "log");
		String reporters = JSystemProperties.getInstance().getPreference(FrameworkOptions.REPORTERS_CLASSES);

		// Making sure that there are reporters configured in the properties
		// file.
		if (null == reporters) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.REPORTERS_CLASSES, DEFAULT_REPORTERS);
		}

		// Configure all other required parameters:

		// Scenario
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, scenarioName);

		// SUT
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sutName);

		// Class Folder
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "target" + File.separator + "classes");

		// Test Source
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main"
						+ File.separator + "java");

		// resources folder.
		JSystemProperties.getInstance().setPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main"
						+ File.separator + "resources");

	}

	/**
	 * Create ANT project that can be executed programatically
	 * 
	 * @param scenariosPath
	 * @param scenarioFile
	 * @param sutFile
	 * @return
	 */
	private Project createNewAntProject(File scenariosPath, File scenarioFile, String scenarioName, String sutName) {
		System.setProperty(RunningProperties.CURRENT_SCENARIO_NAME, scenarioName);
		System.setProperty(RunningProperties.CURRENT_SUT, sutName);
		Project p = new Project();
		p.setName("JSystem Maven Plugin Project");
		p.setBaseDir(mavenProject.getBasedir());
		p.addBuildListener(new AntExecutionListener());
		p.setProperty("basedir", scenariosPath.getAbsolutePath());
		p.setProperty("scenarios.base", scenariosPath.getParentFile().getAbsolutePath());
		p.setProperty("sutFile", sutName);
		p.setProperty("ant.file", scenarioFile.getAbsolutePath());
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
		return p;
	}

	/**
	 * This is required for executing scenarios
	 * 
	 * @param scenariosPath
	 * @throws IOException
	 */
	private void createEmptyTestPropertiesFile(final File scenariosPath) throws IOException {
		File testPropFile = new File(scenariosPath, TEST_PROPERTIES_FILE_EMPTY);
		getLog().debug("About to create file " + testPropFile.getAbsolutePath());
		if (!testPropFile.exists()) {
			if (!testPropFile.createNewFile()) {
				throw new IOException("Failed to create new empty properties file");
			}
		}

		if (!testPropFile.exists()) {
			throw new IOException("Failed to create " + testPropFile.getAbsolutePath());
		}
		getLog().debug("Created file " + testPropFile.getAbsolutePath());
	}

}
