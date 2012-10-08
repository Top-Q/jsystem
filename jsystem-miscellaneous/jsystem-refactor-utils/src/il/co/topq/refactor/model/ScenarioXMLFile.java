package il.co.topq.refactor.model;

import il.co.topq.refactor.exception.ExtractScenarioTestsException;
import il.co.topq.refactor.exception.ExtractTestUUIDException;
import il.co.topq.refactor.exception.ScenarioXmlParseException;
import il.co.topq.refactor.exception.UnmodifiableFileException;
import il.co.topq.refactor.utils.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.extensions.sourcecontrol.SourceControlI;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Itai Agmon
 *         <p/>
 *         represents an XMLProjectFile
 */
public class ScenarioXMLFile extends JSystemFile {

	private final static String XPATH_GET_ALL_SCENARIOS = "//jsystem-ant[contains(@antfile,'${scenarios.base}')]";
	private final static String XPATH_GET_ALL_TESTS = "//test";
	private final static String XPATH_GET_TEST_NODES = "//test[@name='%s']";
	private final static String XPATH_GET_SCENARIO_NODES = "//jsystem-ant[@antfile='${scenarios.base}/%s.xml']";
	private final static String XPATH_GET_SCENARIO_ROOT_ELEMENT = "//project";
	private final static String XPATH_GET_TEST_UUIDS = "//test[@name='%s']/../sysproperty[@key='jsystem.uuid']/@value";

	private Document doc;

	// For example: D:\workspaceJSystem\Sandbox\tests\
	private final String scenariosFolder;

	public ScenarioXMLFile(File xmlFile) {
		super(xmlFile);
		scenariosFolder = xmlFile.getAbsolutePath().split("scenarios")[0].replaceAll("/", File.separator);
		File scenarioFolderFile = new File(scenariosFolder);
		if (!scenarioFolderFile.exists() && !scenarioFolderFile.isDirectory()) {
			throw new IllegalStateException("Folder " + scenariosFolder + " is illegal");
		}

	}

	private void init() throws ParserConfigurationException, IOException, SAXException {
		if (doc != null) {
			return;
		}
		doc = XmlUtils.parseDocument(file);
	}

	/**
	 * 
	 * @return the scenario name as specified in the scneario XML file.
	 * @throws ScenarioXmlParseException
	 */
	public String getScenarioName() throws ScenarioXmlParseException {

		try {
			init();
			return XmlUtils.getElement(doc, XPATH_GET_SCENARIO_ROOT_ELEMENT).getAttribute("name");
		} catch (Exception e) {
			log.info("Failed to get scenario name");
			throw new ScenarioXmlParseException();
		}
	}

	/**
	 * Changes the scenario name to the new given name. The change is of the
	 * name file and in the file xml body.
	 * 
	 * @param newScenarioName
	 * @throws ScenarioXmlParseException
	 * @throws UnmodifiableFileException
	 *             If has problem to access the file.
	 * @throws IOException
	 */
	public void rename(final String newScenarioName) throws ScenarioXmlParseException, UnmodifiableFileException,
			IOException {
		log.info("Renaming scenario " + getScenarioName() + " to " + newScenarioName);
		final File newXmlFile = prepareDestinationFile(newScenarioName);
		try {
			init();
			if (!file.renameTo(newXmlFile)) {
				throw new UnmodifiableFileException(file);
			}
			file = newXmlFile;
			XmlUtils.getElement(doc, XPATH_GET_SCENARIO_ROOT_ELEMENT).setAttribute("name", newScenarioName);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Excpetion was caught during renaming of file", e);
			throw new ScenarioXmlParseException();
		}

	}

	/**
	 * Checks that the destination file is good.
	 * 
	 * @param newScenarioName
	 * @return detinatio file.
	 * @throws IOException
	 */
	private File prepareDestinationFile(String newScenarioName) throws IOException {
		final File newXmlFile = new File(scenariosFolder + newScenarioName + ".xml");
		if (!newXmlFile.getParentFile().exists()) {
			throw new IOException("Scenario destination folder " + newXmlFile.getParentFile().getAbsolutePath()
					+ " is not exist");
		}
		if (newXmlFile.exists()) {
			throw new IOException("Scenario XML file with the same name is already exists: "
					+ newXmlFile.getAbsolutePath());
		}
		return newXmlFile;

	}

	public void rename(final String newScenarioName, SourceControlI sourceControlHandler)
			throws ScenarioXmlParseException, UnmodifiableFileException, IOException {
		if (null == sourceControlHandler) {
			rename(newScenarioName);
			return;
		}
		log.info("Renaming scenario " + getScenarioName() + " to " + newScenarioName);
		final File newXmlFile = prepareDestinationFile(newScenarioName);
		try {
			init();
			sourceControlHandler.moveFile(file, newXmlFile);
			file = newXmlFile;
			XmlUtils.getElement(doc, XPATH_GET_SCENARIO_ROOT_ELEMENT).setAttribute("name", newScenarioName);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Excpetion was caught during renaming of file", e);
			throw new ScenarioXmlParseException();
		}

	}

	/**
	 * Check if the test exists in the scenario.
	 * 
	 * @param testQualifiedName
	 * @return true if, and only if, the specified test exists in the scenario
	 * @throws ExtractScenarioTestsException
	 */
	public boolean isTestExists(CharSequence testQualifiedName) throws ExtractScenarioTestsException {
		Set<MethodTest> scenarioTestSet = getScenarioTests();
		for (MethodTest test : scenarioTestSet) {
			if (test.equals(new MethodTest((String) testQualifiedName, ""))) {
				return true;
			}
		}
		return false;
	}

	public boolean isSubScenarioExists(CharSequence scenarioSourceNamePath) throws ExtractScenarioTestsException {
		Set<AntScenario> scenariosSet = getSubScenarios();
		for (AntScenario scenario : scenariosSet) {
			if (scenario.equals(new AntScenario((String) scenarioSourceNamePath + ".xml"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * return a list of all UUIDs that exists for a specific test
	 * 
	 * @param testQualifiedName
	 *            -
	 * @return - A list of all test uuid
	 * @throws il.co.topq.refactor.exception.ExtractTestUUIDException
	 *             - throw in case a UUID cannot be extracted
	 */
	public List<UUID> getTestUUIDs(CharSequence testQualifiedName) throws ExtractTestUUIDException {
		try {
			init();
			List<UUID> uuidList = new ArrayList<UUID>();
			NodeList testUUIDs = XmlUtils.getNodeList(doc, String.format(XPATH_GET_TEST_UUIDS, testQualifiedName));

			for (int i = 0; i < testUUIDs.getLength(); i++) {
				uuidList.add(UUID.fromString(testUUIDs.item(i).getTextContent()));
			}
			return uuidList;
		} catch (Throwable t) {
			throw new ExtractTestUUIDException("Cannot extract the uuid for the test " + testQualifiedName, t);
		}
	}

	public void save() throws TransformerException {
		// save the results
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		doc.setXmlStandalone(true);
		Result result = new StreamResult(file);
		Source source = new DOMSource(doc);
		transformer.transform(source, result);
	}

	public void renameTest(final String testSourceNamePath, final String testTargetNamePath) {
		try {
			init();
			NodeList testNodes = XmlUtils.getNodeList(doc, String.format(XPATH_GET_TEST_NODES, testSourceNamePath));
			for (int i = 0; i < testNodes.getLength(); i++) {
				((Element) testNodes.item(i)).setAttribute("name", testTargetNamePath);
			}
		} catch (Throwable t) {
			// throw new
			// ExtractTestUUIDException("Cannot extract the uuid for the test "
			// + testQualifiedName,t);
		}

	}

	public void renameSubScenario(String scenarioSourceNamePath, String scenarioTargetNamePath) {
		try {
			init();
			NodeList scenariosNodes = XmlUtils.getNodeList(doc,
					String.format(XPATH_GET_SCENARIO_NODES, scenarioSourceNamePath));
			for (int i = 0; i < scenariosNodes.getLength(); i++) {
				((Element) scenariosNodes.item(i)).setAttribute("antfile", "${scenarios.base}/"
						+ scenarioTargetNamePath + ".xml");

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Retrieve all tests in a specific scenario
	 * 
	 * @return return a set of MethodTest
	 * @throws il.co.topq.refactor.exception.ExtractScenarioTestsException
	 *             -
	 */
	public Set<AntScenario> getSubScenarios() throws ExtractScenarioTestsException {
		try {
			init();
			NodeList scenariosNodes = XmlUtils.getNodeList(doc, XPATH_GET_ALL_SCENARIOS);
			Set<AntScenario> scenarios = new HashSet<AntScenario>();
			for (int i = 0; i < scenariosNodes.getLength(); i++) {
				scenarios.add(new AntScenario(((Element) scenariosNodes.item(i)).getAttribute("antfile").replace(
						"${scenarios.base}/", "")));
			}
			return scenarios;
		} catch (Throwable t) {
			throw new ExtractScenarioTestsException();
		}
	}

	/**
	 * Retrieve all tests in a specific scenario
	 * 
	 * @return return a set of MethodTest
	 * @throws il.co.topq.refactor.exception.ExtractScenarioTestsException
	 *             -
	 */
	public Set<MethodTest> getScenarioTests() throws ExtractScenarioTestsException {
		try {
			init();
			NodeList testNodes = XmlUtils.getNodeList(doc, XPATH_GET_ALL_TESTS);
			Set<MethodTest> scenarioTests = new HashSet<MethodTest>();
			for (int i = 0; i < testNodes.getLength(); i++) {
				String testQualifiedName = testNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				String testSimpleName = testQualifiedName.substring(testQualifiedName.lastIndexOf('.') + 1);
				scenarioTests.add(new MethodTest(testQualifiedName, testSimpleName));
			}
			return scenarioTests;
		} catch (Throwable t) {
			throw new ExtractScenarioTestsException();
		}
	}

	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

}
