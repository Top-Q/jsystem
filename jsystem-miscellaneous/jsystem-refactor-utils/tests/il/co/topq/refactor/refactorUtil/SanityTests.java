package il.co.topq.refactor.refactorUtil;

import il.co.topq.refactor.utils.XmlUtils;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class SanityTests extends AbstractTestCase {

	private final static String REGEX_PARAMETER_KEY = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.%s";

	private final static boolean NO_SOURCE_CONTROL_PLUGIN = true;

	@Before
	public void before() throws Exception {
		if (NO_SOURCE_CONTROL_PLUGIN) {
			new File("JSystemUtilImpl.properties").delete();
		}
		super.before();
	}

	/**
	 * Rename test in multiple files.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameTestInMultipleFiles() throws Exception {
		final String newName = "com.aqua.services.analyzers.AnalysisTest.NEW_NAME";
		boolean changed = util.renameTest("com.aqua.services.analyzers.AnalysisTest.testIsFileExistsWarning", newName);

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(util.numberOfFilesAffected, 3);
		XmlAssert.assertNodeNumber(testbed.getAbsolutePath() + "/scenarios/renameTestScenario.xml", "//test[@name='"
				+ newName + "']", 5);
		XmlAssert.assertNodeNumber(testbed.getAbsolutePath() + "/scenarios/renameTestScenario1.xml", "//test[@name='"
				+ newName + "']", 5);
		XmlAssert.assertNodeNumber(testbed.getAbsolutePath() + "/scenarios/renameTestScenario2.xml", "//test[@name='"
				+ newName + "']", 5);

	}

	/**
	 * Renames test that has 50 instances.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameMultipleTests() throws Exception {
		final String newName = "com.aqua.services.analyzers.AnalysisTest.NEW_NAME";
		final String oldName = "com.aqua.services.junit4.JUnit4Example.myTestMethod";
		boolean changed = util.renameTest(oldName, newName);

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(util.numberOfFilesAffected, 3);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.xml.old");
		XmlAssert.assertNodeNumber(testbed.getAbsolutePath() + "/scenarios/MutipleTests.xml", "//test[@name='"
				+ newName + "']", 50);

	}

	/**
	 * Asserts that renaming scenario is done correctly. scenario son should be
	 * renamed to daughter. The scenario also appears
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameScenario() throws Exception {
		boolean changed = util.renameScenario("scenarios/son", "scenarios/daughter");

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(util.numberOfFilesAffected, 3);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/daughter.xml");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/daughter.properties");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/son.xml.old");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/son.properties.old");
		FileAssert.assertFileNotExists(testbed.getAbsolutePath() + "/scenarios/son.xml");
		FileAssert.assertFileNotExists(testbed.getAbsolutePath() + "/scenarios/son.properties");
		XmlAssert.assertNodeNumber(testbed.getAbsolutePath() + "/scenarios/daughter.xml",
				"/project[@name='scenarios/daughter']", 1);

	}

	/**
	 * Simple test to assert that renaming parameter is done correctly.The
	 * parameter should appear in 3 scenarios. In scenario son it should appear
	 * once.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameTestParameter() throws Exception {
		boolean changed = util.renameTestsParameters("com.aqua.services.junit4.JUnit4Example.myTestMethod", "param",
				"NEW_PARAM");

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(util.numberOfFilesAffected, 3);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/son.properties.old");
		final String regex = String.format(".{8}-.{4}-.{4}-.{4}-.{12}\\.%s", "NEW_PARAM");
		PropertiesAssert.assertKey(testbed.getAbsolutePath() + "/scenarios/son.properties", regex, 1);

	}

	/**
	 * Tests that parameter that is included in many test instanced is renamed
	 * correctly.<br>
	 * The test appears in scenario multipleTests 50 times and in Root 10 times.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameMultipleParameters() throws Exception {
		boolean changed = util.renameTestsParameters("com.aqua.services.junit4.JUnit4Example.myTestMethod", "param",
				"NEW_PARAM");

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(3, util.numberOfFilesAffected);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.properties.old");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/Root.properties.old");

		final String regex = String.format(REGEX_PARAMETER_KEY, "NEW_PARAM");

		PropertiesAssert.assertKey(testbed.getAbsolutePath() + "/scenarios/MutipleTests.properties", regex, 50);
		PropertiesAssert.assertKey(testbed.getAbsolutePath() + "/scenarios/Root.properties", regex, 10);

	}

	/**
	 * Asserts that we bean that parameter name is changed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameSimpleBeanParameters() throws Exception {
		final String beanName = "com.aqua.services.multiuser.SimpleBean";
		final String parameterName = "name";
		String newParameterName = "NEW_NAME";
		boolean changed = util.renameTestsBeanParametersNames(beanName, parameterName, newParameterName);

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(2, util.numberOfFilesAffected);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/renameBeanRoot.properties.old");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/renameBean.properties.old");
		PropertiesAssert.assertValue(testbed.getAbsolutePath() + "/scenarios/renameBean.properties", beanName
				+ ";#.+\\r\\n" + newParameterName + "=", 2);
		// Assert array of beans
		PropertiesAssert.assertValue(testbed.getAbsolutePath() + "/scenarios/renameBean.properties", "\\d\\."
				+ newParameterName + "=\\S+", 2);
		PropertiesAssert.assertValue(testbed.getAbsolutePath() + "/scenarios/renameBeanRoot.properties", beanName
				+ ";#.+\\r\\n" + newParameterName + "=", 2);
		// Assert array of beans
		PropertiesAssert.assertValue(testbed.getAbsolutePath() + "/scenarios/renameBeanRoot.properties", "\\d\\."
				+ newParameterName + "=\\S+", 2);

	}

	/**
	 * Tests that scenario is kept as marked as test after renaming it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMarkedAsTest() throws Exception {
		util.renameScenario("scenarios/simpleScenario", "scenarios/renamedScenario");
		final File newProp = new File(testbed + "/scenarios", "HasMarkedAsTest.properties");
		final File newXml = new File(testbed + "/scenarios", "HasMarkedAsTest.xml");

		// Get simpleScenario uuid from the renamed scenario
		final String uuid = XmlUtils.getNode(XmlUtils.parseDocument(newXml),
				"//jsystem-ant[contains(@antfile,'renamedScenario')]/property[@name='jsystem.uuid']/@value")
				.getTextContent();
		Assert.assertNotNull("uuis is null", uuid);
		System.out.println("UUID: " + uuid);
		// Asserting that the scenario is still marked as test
		PropertiesAssert.assertKeyValue(newProp, uuid + ".jsystem.scenario.as.test", "true");
	}

	@Test
	public void testMutipleScenarioSuite() throws Exception {
		final File multiFile = new File(testbed, "multi.xml");
		final String newScenario = "scenarios\\NEW_NAME";
		boolean changed = util.renameMultipleScenariosSuiteExecutionScenarioName(multiFile.getAbsolutePath(),
				"scenarios\\flowControl", newScenario);

		// Assertions
		Assert.assertTrue("No files changed", changed);
		Assert.assertEquals(1, util.numberOfFilesAffected);
		FileAssert.assertFileExists(multiFile.getAbsolutePath() + ".old");
		XmlAssert.assertNodeNumber(multiFile, "//scenarioName[text()='" + newScenario + ".xml']", 2);

	}

}
