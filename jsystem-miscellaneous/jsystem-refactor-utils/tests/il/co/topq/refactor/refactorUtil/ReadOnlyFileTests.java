package il.co.topq.refactor.refactorUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ReadOnlyFileTests extends AbstractTestCase {

	@Before
	public void before() throws Exception {
		Properties config = new Properties();
		config.put("jar.path", "resources/RefactorUtilsSCMPlugin.jar");
		config.put("class.name", "il.co.topq.refactor.scm.SourceControlMock");
		final File configFile = new File("JSystemUtilImpl.properties");
		configFile.delete();
		configFile.createNewFile();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(configFile);
			config.store(os, null);
		} finally {
			os.close();
		}
		super.before();

	}

	@Test
	public void testSimpleReadOnly() throws Exception {
		File xmlFile = new File(testbed, "scenarios/MutipleTests.xml");
		File scenarioFile = new File(testbed, "scenarios/MutipleTests.properties");
		File rootXmlFile = new File(testbed, "scenarios/Root.xml");
		Assert.assertTrue("Failed to set file to read only", xmlFile.setReadOnly());
		Assert.assertTrue("Failed to set file to read only", scenarioFile.setReadOnly());
		Assert.assertTrue("Failed to set file to read only", rootXmlFile.setReadOnly());

		boolean changed = util.renameScenario("scenarios/MutipleTests", "scenarios/NewScenario");
		Assert.assertTrue("Nothing was changed", changed);
		Assert.assertEquals(3, util.numberOfFilesAffected);
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.xml.old");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.properties.old");
		FileAssert.assertFileNotExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.xml");
		FileAssert.assertFileNotExists(testbed.getAbsolutePath() + "/scenarios/MutipleTests.properties");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/NewScenario.xml");
		FileAssert.assertFileExists(testbed.getAbsolutePath() + "/scenarios/NewScenario.properties");

	}

}
