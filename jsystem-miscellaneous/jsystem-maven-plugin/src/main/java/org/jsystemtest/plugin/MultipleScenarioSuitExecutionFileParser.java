package org.jsystemtest.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Used to parse the JSystem multiple scenario suit execution file.
 * @author Itai Agmon
 *
 */
public class MultipleScenarioSuitExecutionFileParser {

	private final File scenariosFile;
	private final List<Execution> executions;

	public MultipleScenarioSuitExecutionFileParser(File scenariosFile) throws IOException {
		if (scenariosFile == null || !scenariosFile.exists()) {
			throw new IOException("Multiple scenario suit execution file " + scenariosFile.getAbsolutePath()
					+ " is not exist");
		}
		this.scenariosFile = scenariosFile;
		executions = new ArrayList<Execution>();
	}

	/**
	 * Parse the XML file
	 */
	public void parse() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(scenariosFile);

			final NodeList commandsList = doc.getElementsByTagName("command");
			for (int temp = 0; temp < commandsList.getLength(); temp++) {
				Node command = commandsList.item(temp);
				if (command.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) command;
					final String scenario = eElement.getElementsByTagName("scenarioName").item(0).getTextContent();
					final String sut = eElement.getElementsByTagName("sutFile").item(0).getTextContent();
					executions.add(new Execution(scenario, sut));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Execution> getExecutions() {
		return executions;
	}

	public class Execution {
		private final String scenario;
		private final String sut;

		public Execution(final String scenario, final String sut) {
			super();
			this.scenario = scenario;
			this.sut = sut;
		}

		public String getScenario() {
			return scenario;
		}

		public String getSut() {
			return sut;
		}

		@Override
		public String toString() {
			return "Scenario: " + scenario + ", Sut: " + sut;
		}
	}

}
