package il.co.topq.refactor.refactorUtil;

import il.co.topq.refactor.model.ScenarioPropertiesFile;
import il.co.topq.refactor.model.ScenarioXMLFile;
import il.co.topq.refactor.utils.FileUtils;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class PerformanceTests extends AbstractTestCase {

	private static final int NUM_OF_SCENARIOS = 300;
	private static final String SCENARIO_NAME_XML = "Performance.xml";
	private static final String SCENARIO_NAME_PROP = "Performance.properties";
	private static final String TEST_NAME = "com.aqua.services.diistributed.DistributedExecutionExample.testJustATest";

	private File scenarioFolder;
	private File baseScenarioXml;
	private File baseScenarioProperties;

	@Before
	public void before() throws Exception {
		super.before();
		scenarioFolder = new File(testbed, "scenarios");
		System.out.println("Preparing environment");
		baseScenarioXml = new File(scenarioFolder, SCENARIO_NAME_XML + ".backup");
		new File(scenarioFolder, SCENARIO_NAME_XML).renameTo(baseScenarioXml);

		baseScenarioProperties = new File(scenarioFolder, SCENARIO_NAME_PROP + ".backup");
		new File(scenarioFolder, SCENARIO_NAME_PROP).renameTo(baseScenarioProperties);

		for (int i = 0; i < NUM_OF_SCENARIOS; i++) {
			// Creating new XML file
			File tempXml = new File(scenarioFolder, SCENARIO_NAME_XML);
			FileUtils.copyFile(baseScenarioXml, tempXml);
			ScenarioXMLFile xmlFile = new ScenarioXMLFile(tempXml);
			xmlFile.rename("scenarios/" + SCENARIO_NAME_XML.replaceAll("(Performance).xml", "$1" + i));
			xmlFile.save();

			// Creating new properties file.
			File tempProp = new File(scenarioFolder, SCENARIO_NAME_PROP);
			FileUtils.copyFile(baseScenarioProperties, tempProp);
			ScenarioPropertiesFile propFile = new ScenarioPropertiesFile(tempProp);
			propFile.rename("scenarios/" + SCENARIO_NAME_PROP.replaceAll("(Performance).properties", "$1" + i));
			propFile.save();
		}
		System.out.println("Finished preparing environment");

	}

	@Test
	public void testRenamingTestsInManyScenarios() throws Exception {
		long start = System.currentTimeMillis();
		util.renameTest(TEST_NAME, "com.aqua.services.diistributed.DistributedExecutionExample.testNEW_NAME");
		System.out.println(("Operation finished in " + (System.currentTimeMillis() - start) / 1000) + " seconds");

	}

	@Test
	public void testRenamingParametersInManyScenarios() throws Exception {
		long start = System.currentTimeMillis();
		util.renameTestsParameters(TEST_NAME, "Condition", "NEW_PARAMETER");
		System.out.println(("Operation finished in " + (System.currentTimeMillis() - start) / 1000) + " seconds");

	}

}
