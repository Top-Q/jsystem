package org.jsystemtest.plugin;

import java.io.File;
import java.io.IOException;

import jsystem.extensions.report.junit.JUnitReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.RunningProperties;
import jsystem.runner.AntExecutionListener;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

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
	private static final String DEFAULT_REPORTERS = "jsystem.extensions.report.html.LevelHtmlTestReporter;jsystem.framework.report.SystemOutTestReporter;jsystem.extensions.report.xml.XmlReporter";

	/**
	 * The current project representation.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject mavenProject;

	/**
	 * @parameter expression="${scenario}"
	 * @required
	 */
	private String scenario;

	/**
	 * @parameter expression="${sut}"
	 * @required
	 */
	private String sut;

	/**
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		getLog().info("change user working dir to: "+mavenProject.getBasedir().getAbsolutePath());
		// This line is for setting the current folder to the project root
		// folder. This is very important if we want to run the plug-in from the
		// parent folder.
		System.setProperty("user.dir", mavenProject.getBasedir().getAbsolutePath());

		// Collect parameters that required for the execution
		final File scenariosPath = new File(mavenProject.getBasedir(), SCENARIO_PATH);
		final File sutFile = new File(mavenProject.getBasedir() + File.separator + SUT_PATH, sut);
		final File scenarioFile = new File(scenariosPath, scenario.replaceFirst("scenarios", "") + ".xml");

		final Project p = createNewAntProject(scenariosPath, scenarioFile, sutFile);
		updateJSystemProperties(sutFile, scenarioFile);

		try {
			createEmptyTestPropertiesFile(scenariosPath);
		} catch (IOException e) {
			getLog().error("Failed to create new empty scenario properties file");
			getLog().error(e);
			return;
		}

		try {
			executeScenario(scenarioFile, p);
		} catch (Exception e) {
			getLog().error("Failed to execute scenario");
			getLog().error(e);
		}
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
	private void updateJSystemProperties(final File sutFile, final File scenarioFile) {
		JSystemProperties.getInstance().setPreference(FrameworkOptions.LOG_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "log");
		String reporters = JSystemProperties.getInstance().getPreference(FrameworkOptions.REPORTERS_CLASSES);

		// Make sure that the JUnit reporter is in the reporter.classes
		String reporterName = JUnitReporter.class.getName();
		if (null == reporters) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.REPORTERS_CLASSES, DEFAULT_REPORTERS + ";" + reporterName);
		} else if (!reporters.contains(reporterName)) {
			reporters += ";" + reporterName;
			JSystemProperties.getInstance().setPreference(FrameworkOptions.REPORTERS_CLASSES, reporters);
		}

		// Configure all other required parameters:
		
		//Scenario 
		JSystemProperties.getInstance().setPreference(FrameworkOptions.CURRENT_SCENARIO, scenario);

		//SUT
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sut);
		
		//Class Folder
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_CLASS_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "target" + File.separator + "classes");
		
		//Test Source 
		JSystemProperties.getInstance().setPreference(FrameworkOptions.TESTS_SOURCE_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "java");
		
		//resources folder. 
		JSystemProperties.getInstance().setPreference(
				FrameworkOptions.RESOURCES_SOURCE_FOLDER,
				mavenProject.getBasedir().getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator
						+ "resources");

	}

	private void executeScenario(final File scenarioFile, final Project p) {
		
		getLog().info("------------------------Jsystem Maven Plugin---------------------");
		getLog().info("About to execute scenario " + scenarioFile.getName());
		getLog().info("of project=" + p.getBaseDir());
		getLog().info("------------------------------------------------------------------------");

		
		p.fireBuildStarted();
		p.init();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, scenarioFile);
		p.executeTarget(p.getDefaultTarget());
		p.fireBuildFinished(null);

		getLog().info("------------------------------------------------------------------------");
		getLog().info("Execution of scenario " + scenarioFile.getName() + " ended ");
		getLog().info(
				"Reports can be found in " + mavenProject.getBasedir().getAbsolutePath() + File.separator + "log" + File.separator
						+ "current");
		getLog().info("------------------------------------------------------------------------");
	}

	/**
	 * Create ANT project that can be executed programatically
	 * 
	 * @param scenariosPath
	 * @param scenarioFile
	 * @param sutFile
	 * @return
	 */
	private Project createNewAntProject(final File scenariosPath, File scenarioFile, File sutFile) {
		Project p = new Project();

		p.setBaseDir(mavenProject.getBasedir());
		p.addBuildListener(new AntExecutionListener());
		System.setProperty(RunningProperties.CURRENT_SCENARIO_NAME, scenario);
		System.setProperty(RunningProperties.CURRENT_SUT, sut);
		p.setProperty("basedir", scenariosPath.getAbsolutePath());
		p.setProperty("scenarios.base", scenariosPath.getParentFile().getAbsolutePath());
		p.setProperty("sutFile", sut);

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
