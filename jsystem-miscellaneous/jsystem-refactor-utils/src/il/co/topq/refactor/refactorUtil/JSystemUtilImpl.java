package il.co.topq.refactor.refactorUtil;

import il.co.topq.refactor.infra.ConfigurationHandler;
import il.co.topq.refactor.infra.MyClassLoader;
import il.co.topq.refactor.model.JSystemProject;
import il.co.topq.refactor.model.MultipleScenarioSuiteFile;
import il.co.topq.refactor.model.ScenarioPairFiles;
import il.co.topq.refactor.model.ScenarioPropertiesFile;
import il.co.topq.refactor.model.ScenarioXMLFile;
import il.co.topq.refactor.utils.SerializedBeanUtils;
import il.co.topq.refactor.utils.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.sourcecontrol.SourceControlI;

/**
 * @author Itai Agmon
 */

public class JSystemUtilImpl implements JSystemUtilI {

	private final JSystemProject jsystemProject;

	SourceControlI sourceControHandler;

	// Mostly for unit testing. Keep it package protected
	int numberOfFilesAffected = 0;

	final File configFile = new File("JSystemUtilImpl.properties");

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public JSystemUtilImpl(File projectDir) {
		jsystemProject = new JSystemProject(projectDir);
		ConfigurationHandler config = new ConfigurationHandler(configFile);
		loadSourceControlPlugin(config.getString("jar.path"), config.getString("class.name"));
	}

	public JSystemUtilImpl(File projectDir, SourceControlI sourceControlHandler) {
		jsystemProject = new JSystemProject(projectDir);
		this.sourceControHandler = sourceControlHandler;
	}

	/**
	 * Adds the external plugin jar to the class path and creating new instance
	 * of the source control plugin object.
	 * 
	 * @param jarLocation
	 * @param className
	 */
	@SuppressWarnings("unchecked")
	private void loadSourceControlPlugin(final String jarLocation, final String className) {
		if (StringUtils.isEmpty(jarLocation) || StringUtils.isEmpty(className)) {
			return;
		}
		URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		MyClassLoader l = new MyClassLoader(loader.getURLs());
		Class<SourceControlI> c = null;
		try {
			l.addURL(new URL("file:" + jarLocation));
			c = (Class<SourceControlI>) l.loadClass(className);
			sourceControHandler = c.newInstance();
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to create instance of source control plugin", e);
		}
	}

    /**
     *
     * @param scenarioSourceNamePath
     *            The old scenario name. for example: scenarios/simpleScenario
     * @param scenarioTargetNamePath
     *            The new name the scenario will be renamed to. for example:
     *            scenarios/newScenario
     *
     * @return
     * @throws Exception
     */
	@Override
	public boolean renameScenario(final String scenarioSourceNamePath, final String scenarioTargetNamePath)
			throws Exception {
		boolean changed = false;
		log.info("About to rename " + scenarioSourceNamePath + " with " + scenarioTargetNamePath);
		List<ScenarioPairFiles> pairFilesList = jsystemProject.getProjectScenariosFiles();
		for (ScenarioPairFiles pairFile : pairFilesList) {
			if (pairFile.getXmlFile().getScenarioName().equals(scenarioSourceNamePath)) {
				log.info("Found scenario " + pairFile.getXmlFile());
				// We have found the scenario we want to rename.
				pairFile.backup();
				pairFile.getXmlFile().rename(scenarioTargetNamePath, sourceControHandler);
				pairFile.getPropertiesFile().rename(scenarioTargetNamePath, sourceControHandler);
				pairFile.save(sourceControHandler);
				numberOfFilesAffected = numberOfFilesAffected + 2;
				changed = true;
			} else {
				// The that we want to rename can still be inside the current
				// scenario
				if (pairFile.getXmlFile().isSubScenarioExists(scenarioSourceNamePath)) {
					log.info("Found scenario as sub scenario in " + pairFile.getXmlFile());
					pairFile.getXmlFile().backup();
					pairFile.getXmlFile().renameSubScenario(scenarioSourceNamePath, scenarioTargetNamePath);
					pairFile.getXmlFile().save(sourceControHandler);
					numberOfFilesAffected++;
					changed = true;
				}
			}
		}
		return changed;
	}

    /**
     * 
     * @param testSourceNamePath
     *            The old test full name. For example:
     *            com.aqua.services.multiuser
     *            .TestParamsIncludeExample.testCompareFolder
     * @param testTargetNamePath
     *            The new test name to rename to.For example:
     *            com.aqua.services.multiuser
     *            .TestParamsIncludeExample.testCompareFile
     * @return
     * @throws Exception
     */
	@Override
	public boolean renameTest(String testSourceNamePath, String testTargetNamePath) throws Exception {
		boolean changed = false;
		log.info("About to rename " + testSourceNamePath + " with " + testTargetNamePath);
		List<ScenarioXMLFile> xmlFilesList = jsystemProject.getScenariosXMLFiles();
		for (ScenarioXMLFile xmlFile : xmlFilesList) {
			if (xmlFile.isTestExists(testSourceNamePath)) {
				log.info("Found test " + testSourceNamePath + " in scenario " + xmlFile.toString());
				xmlFile.backup();
				xmlFile.renameTest(testSourceNamePath, testTargetNamePath);
				xmlFile.save(sourceControHandler);
				numberOfFilesAffected++;
				changed = true;
			}
		}
		return changed;
	}

	/**
	 */
	@Override
	public boolean renameTestsParameters(String testQualifiedName, String oldParameterName, String newParameterName)
			throws Exception {
		// JSystem when saving the property capitalize the first letter for GUI
		// reason, therefore
		// we capitalize the first letter of the new parameter name given by the
		// user.
		oldParameterName = StringUtils.firstCharToUpper(oldParameterName);
		newParameterName = StringUtils.firstCharToUpper(newParameterName);
		log.info("About to rename " + oldParameterName + " with " + newParameterName + " in test " + testQualifiedName);
		boolean changed = false; // If any property file will change after this
									// method this flag will be true
		// Collect all test UUIDs for a specific test
		List<ScenarioPairFiles> pairFilesList = jsystemProject.getProjectScenariosFiles();

        //We select the disjoint set data structure to prevent duplication of uuid that will cause multiple
        //changes in each entry instead of single change for each entry
		Set<UUID> allUUIDs = new HashSet<UUID>();

		// Collect the uuid of the test from the whole project
		for (ScenarioPairFiles pairFile : pairFilesList) {
			List<UUID> singleFileUUIDs = pairFile.getXmlFile().getTestUUIDs(testQualifiedName);
			allUUIDs.addAll(singleFileUUIDs);
		}

		for (ScenarioPairFiles pairFile : pairFilesList) {
			boolean fileChange = false;
			log.finer("In file " + pairFile.getPropertiesFile());
			long start = System.currentTimeMillis();
			for (UUID uuid : allUUIDs) {
                boolean changedNow = pairFile.getPropertiesFile().replacePropertyName(uuid, oldParameterName, newParameterName);
				fileChange = fileChange || changedNow;
			}
			log.finer("Finished finding all uuid's in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
			start = System.currentTimeMillis();
			if (fileChange) {
				// We know that the test exists in the file and has the
				// parameter we want to rename.
				changed = true;
				pairFile.getPropertiesFile().backup();
				pairFile.getPropertiesFile().save(sourceControHandler);
				log.info("Found and replaced parameter name in " + pairFile.getPropertiesFile());
				numberOfFilesAffected++;
			}
			pairFile.getPropertiesFile().close();
			log.finer("Finished writing properties to file " + (System.currentTimeMillis() - start) / 1000 + " seconds");
		}
		return changed;
	}

    /**
     * 
     * @param beanSourceNamePath
     *            The full name of the bean. For example:
     *            com.aqua.services.multiuser.SimpleBean
     *
     * @param currentParameterName
     *            The current name of the bean parameter. For example: name
     * @param newParameterName
     *            The new name for the parameter. For example: personName
     * @return
     * @throws Exception
     */
	@Override
	public boolean renameTestsBeanParametersNames(String beanSourceNamePath, String currentParameterName,
			String newParameterName) throws Exception {
		log.info(String.format("Renaming bean %s parameter %s to %s", beanSourceNamePath, currentParameterName,
				newParameterName));
		boolean changed = false;

		List<ScenarioPropertiesFile> propertiesFilesList = jsystemProject.getScenariosPropertiesFiles();
		for (ScenarioPropertiesFile propertiesFile : propertiesFilesList) {
			log.fine("In file " + propertiesFile);
			boolean backedUp = false;
			Map<String, String> content = propertiesFile.getContent();
			for (String prop : content.keySet()) {
				if (SerializedBeanUtils.isBeanExists(content.get(prop), beanSourceNamePath)) {
					log.info("Found bean in " + propertiesFile);
					String newBean = SerializedBeanUtils.renameBeanParameter(content.get(prop), currentParameterName,
							newParameterName);
					if (newBean.equals(content.get(prop))) {
						log.fine("Bean exists but no change was done");
						continue;
					}
					if (!backedUp) {
						log.fine("Creating backup of " + propertiesFile);
						numberOfFilesAffected++;
						propertiesFile.backup();
						backedUp = true;
					}
					propertiesFile.put(prop, newBean);
					propertiesFile.save(sourceControHandler);
					changed = true;
				}
			}
			propertiesFile.close();

		}
		return changed;
	}

    /**
     * 
     * @param multipleSuiteAbsolutePathFileName
     *            The absolute location of the XML file
     * @param scenarioSourceNamePath
     *            The old name of the scenario. For example:
     *            scenarios/flowControl
     * @param scenarioTargetNamePath
     *            The new name for the scenario. For example:
     *            scenarios/flowControl2
     * @return
     * @throws Exception
     */
	@Override
	public boolean renameMultipleScenariosSuiteExecutionScenarioName(String multipleSuiteAbsolutePathFileName,
			String scenarioSourceNamePath, String scenarioTargetNamePath) throws Exception {
		log.info("About to rename scenario " + scenarioSourceNamePath + " to " + scenarioTargetNamePath + " in "
				+ multipleSuiteAbsolutePathFileName);
		if (!(new File(multipleSuiteAbsolutePathFileName).exists())) {
			log.severe("File " + multipleSuiteAbsolutePathFileName + " is not exist");
			return false;
		}
		scenarioSourceNamePath = StringUtils.frontSlashToBackSlash(scenarioSourceNamePath);
		scenarioTargetNamePath = StringUtils.frontSlashToBackSlash(scenarioTargetNamePath);
		MultipleScenarioSuiteFile suiteFile = new MultipleScenarioSuiteFile(multipleSuiteAbsolutePathFileName);
		if (!suiteFile.isScenarioExists(scenarioSourceNamePath)) {
			log.warning("Scenario " + scenarioSourceNamePath + " not exist in " + multipleSuiteAbsolutePathFileName);
			return false;
		}
		suiteFile.backup();
		suiteFile.renameScenario(scenarioSourceNamePath, scenarioTargetNamePath);
		suiteFile.save(sourceControHandler);
		numberOfFilesAffected++;
		return true;

	}

	@Override
	public int getNumberOfAffectedFiles() {
		return numberOfFilesAffected;
	}

}
