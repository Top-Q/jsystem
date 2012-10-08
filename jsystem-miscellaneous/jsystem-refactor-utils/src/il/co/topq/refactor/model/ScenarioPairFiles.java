package il.co.topq.refactor.model;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import jsystem.extensions.sourcecontrol.SourceControlI;

/**
 * @author Itai Agmon
 */

public class ScenarioPairFiles extends JSystemFile {

	private ScenarioXMLFile xmlFile;

	private ScenarioPropertiesFile propertiesFile;

	public ScenarioPairFiles(File propertiesFile, File xmlFile) {
		super(null);
		this.xmlFile = new ScenarioXMLFile(xmlFile);
		this.propertiesFile = new ScenarioPropertiesFile(propertiesFile);
	}

	public ScenarioXMLFile getXmlFile() {
		return xmlFile;
	}

	public ScenarioPropertiesFile getPropertiesFile() {
		return propertiesFile;
	}

	@Override
	public void backup() {
		xmlFile.backup();
		propertiesFile.backup();
	}

	@Override
	public void save() throws IOException, TransformerException {
		xmlFile.save();
		propertiesFile.save();
	}

	public void save(SourceControlI sourceControHandler) throws Exception {
		xmlFile.save(sourceControHandler);
		propertiesFile.save(sourceControHandler);
	}

	@Override
	public String toString() {
		return xmlFile.toString() + "\n" + propertiesFile.toString() + "\n";

	}
}
